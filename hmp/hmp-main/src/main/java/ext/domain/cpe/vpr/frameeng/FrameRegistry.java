package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.param.ParamService;
import org.osehra.cpe.vista.rpc.RpcTemplate;
import org.osehra.cpe.vpr.frameeng.AdapterFrame.DroolsFrameAdapter;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.frameeng.IFrameEvent.FrameInitEvent;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.queryeng.ColDef;
import org.osehra.cpe.vpr.queryeng.ViewDef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;


/**
 * A registry is a list of eligible frames
 * 
 * - Handles the event resolution of triggers
 * - Responsible for deteriming how many (0+) instances of each frame are active
 * - Responsbile for creating/returning the frame instance to run for the given event (may reuse a single frame, delegate to trigger, etc).
 * 
 * TODO: Work on more metadata stuff, like inferring a list of attributes/categories
 * - find patient specific viewdefs by which ones declare PatientIDParam, etc.
 * - invokable viewdefs by trigger declaration
 * - ViewDefs by instancof ViewDef.class
 * - create some sort of findByType(xxx);
 * 
 * TODO: current type system does not allow you to have multiple types.
 * TODO: If you try to register multiple frames with the same ID, should throw an error.
 * @author brian
 */
public class FrameRegistry extends AbstractCollection<IFrame> implements ApplicationContextAware {
	private Map<IFrame, FrameLoader> allFrames = new HashMap<IFrame, FrameLoader>();
	private Map<String, IFrame> allFramesByID = new HashMap<String, IFrame>();
	private List<FrameLoader> frameLoaders = new ArrayList<FrameLoader>();
	private Map<String, FrameStats> allFrameStats = new HashMap<String, FrameStats>();
	private ApplicationContext ctx;

	public FrameRegistry(FrameLoader... loaders) throws Exception {
		this.frameLoaders.addAll(Arrays.asList(loaders));
	}
	
	/**
	 * This initalization mechanism is invoked by spring after all the other injection has finished....
	 * @throws Exception
	 */
	private void load() throws Exception {
		for (FrameLoader loader : frameLoaders) {
			load(loader);
		}
	}
	
	public void addFrameLoader(FrameLoader loader) {
		load(loader);
		this.frameLoaders.add(loader);
	}
	
	/**
	 * Removes all the frames from the specified loader
	 */
	private void unload(FrameLoader loader) {
		// first remove any frames currently registered under this frameloader
		List<IFrame> removeList = new ArrayList<IFrame>();
		for (IFrame frame : allFrames.keySet()) {
			if (allFrames.get(frame) == loader) {
				removeList.add(frame);
			}
		}
		for (IFrame frame : removeList) {
			String id = frame.getID();
			this.allFrames.remove(frame);
			this.allFramesByID.remove(id);
			this.allFrameStats.remove(id);
			// TODO: Shutdown event?
		}
	}
	
	private void load(FrameLoader loader) {
		FrameJob initjob = new FrameJob(ctx, this);
		
		// clear any existing frames first...
		if (this.frameLoaders.contains(loader)) {
			unload(loader);
		}
		
		// then load all the events
		for (IFrame frame : loader.load()) {
			String id = frame.getID();
			this.allFrames.put(frame, loader);
			if (this.allFramesByID.containsKey(id)) {
				// TODO: Throw error
			}
			this.allFramesByID.put(id, frame);
			FrameInitEvent evt = new FrameInitEvent(frame);
			frame.init(new FrameTask(initjob, frame, evt, null));
			// TODO: Fetch/Index Triggers?
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	public List<FrameLoader> getFrameLoaders() {
		return this.frameLoaders;
	}
	
	public boolean isEmpty() {
		return allFrames.isEmpty();
	}
	
	public int size() {
		return allFrames.size();
	}
	
	public Iterator<IFrame> iterator() {
		return this.allFrames.keySet().iterator();
	}
	
	public IFrame findByID(String id) {
		return allFramesByID.get(id);
	}
	
	public List<IFrame> findAllByClass(Class<? extends IFrame> clazz) {
		List<IFrame> ret = new ArrayList<IFrame>();
		for (IFrame f : this) {
			if (clazz.isAssignableFrom(f.getClass())) {
				ret.add(f);
			}
		}
		return ret;
	}
	
	public FrameJob createJob(List<IFrameEvent<?>> events) {
		Slf4JStopWatch watch = new Slf4JStopWatch("job.init");
		FrameJob job = new FrameJob(ctx, this);
		
		for (IFrameEvent<?> event : events) {
			for (IFrame frame : allFrames.keySet()) {
				IFrameTrigger<?> trig = frame.evalTriggers(event);
				FrameLoader loader = this.allFrames.get(frame);
				if (trig != null) {
					job.addSubTask(new FrameTask(job, frame, event, trig));
				}
			}
		}
		watch.stop();
		return job;
	}
	
	public FrameStats getFrameStats(IFrame frame) {
		String id = frame.getID();
		if (!allFrameStats.containsKey(id)) {
			allFrameStats.put(id, new FrameStats());
		}
		return allFrameStats.get(id);
	}
	

	public static class FrameStats {
		public int RUN_COUNT = 0;
		public long RUN_LAST = 0;
		public long RUNTIME_SUM_MS = 0;
		public long RUNTIME_AVG_MS = -1;
		public long RUNTIME_MIN_MS = -1;
		public long RUNTIME_MAX_MS = -1;
		
		public void run(long runtimeMS) {
			RUN_COUNT++;
			RUN_LAST = System.currentTimeMillis();
			RUNTIME_SUM_MS += runtimeMS;
			RUNTIME_AVG_MS = RUNTIME_SUM_MS / RUN_COUNT;
			if (RUNTIME_MIN_MS < 0 || RUNTIME_MIN_MS > runtimeMS) {
				RUNTIME_MIN_MS = runtimeMS;
			}
			if (RUNTIME_MAX_MS < 0 || RUNTIME_MAX_MS < runtimeMS) {
				RUNTIME_MAX_MS = runtimeMS;
			}
		}
	}
	
	public static abstract class FrameLoader {
		public abstract List<IFrame> load();
		public void add(IFrame frame) {
			throw new UnsupportedOperationException();
		}
	}
	
	public static class StaticFrameLoader extends FrameLoader {
		private List<IFrame> frames;

		public StaticFrameLoader(IFrame... frames) {
			this(Arrays.asList(frames));
		}
		
		public StaticFrameLoader(List<IFrame> frames) {
			this.frames = frames;
		}

		@Override
		public List<IFrame> load() {
			return this.frames;
		}
	}
	
	public static class SpringFrameLoader extends FrameLoader implements ApplicationContextAware {
		private ApplicationContext ctx;

		@Override
		public List<IFrame> load() {
			Map<String, IFrame> map = this.ctx.getBeansOfType(IFrame.class);
			return new ArrayList<IFrame>(map.values());
		}

		@Override
		public void setApplicationContext(ApplicationContext ctx) throws BeansException {
			this.ctx = ctx;
		}
		
	}
	
	public static class ProtocolFrameLoader extends FrameLoader {
		private MongoTemplate tpl;
		public ProtocolFrameLoader(MongoTemplate tpl) {
			this.tpl = tpl;
		}
		
		@Override
		public void add(IFrame frame) {
			super.add(frame);
		}
		
		@Override
		public List<IFrame> load() {
			List<IFrame> ret = new ArrayList<IFrame>();
			Query qry = new Query();
			//qry.addCriteria(Criteria.where("type").is("protocol"));
			List<DBObject> results = this.tpl.find(qry, DBObject.class, "frames");
			for (DBObject doc : results) {
				JsonNode node = POMUtils.parseJSONtoNode(doc.toString());
				if (node.has("definition")) {
					
				}
				ret.add(new Frame.ProtocolFrame(node));
			}
			
			return ret;
		}
	}
	
	
	public static class ProtocolFileFrameLoader extends FrameLoader {
		private File dir;

		public ProtocolFileFrameLoader(File dir) {
			this.dir = dir;
		}
		
		@Override
		public List<IFrame> load() {
			List<IFrame> ret = new ArrayList<IFrame>();
			File[] files = this.dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}
			});
			for (File file : files) {
				try {
					ret.add(new Frame.ProtocolFrame(file.toURI(), POMUtils.parseJSONtoNode(new FileInputStream(file))));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return ret;
		}
	}
	
	
	public static class DroolsFrameLoader extends FrameLoader {
		private KnowledgeBase kb;
		
		public DroolsFrameLoader(KnowledgeBase kb) {
			this.kb = kb;
		}
		
		@Override
		public List<IFrame> load() {
			ArrayList<IFrame> ret = new ArrayList<IFrame>();
			ret.add(new DroolsFrameAdapter(kb));
			return ret;
		}
	}
	
	public static class RemindersFrameLoader extends FrameLoader {
		private RpcTemplate tpl;
		
		public RemindersFrameLoader(RpcTemplate tpl) {
			this.tpl = tpl;
		}
		
		@Override
		public List<IFrame> load() {
			Map<String, Object> params = new HashMap<String, Object>();
            params.put("command", "getReminderList");
            params.put("user", "");
            params.put("location", "");
            
//            RpcRequest req = new RpcRequest();
//            tpl.execute(req);
            
            String req = "vrpcb://500:vpruser1;verifycode1&@localhost:29060/VPR UI CONTEXT/VPRCRPC RPC";
            Map resp = tpl.executeForObject(Map.class, req, params);
            List reminders = (List) resp.get("reminders");
            
            List<IFrame> ret = new ArrayList<IFrame>();
			for (Object obj : reminders) {
				Map map = (Map) obj;
				String name = (String) map.get("name");
				String uid = (String) map.get("uid");
				ret.add(new AdapterFrame.ReminderFrame(name, uid));
			}
			System.out.println("REMINDER FRAMES");
			System.out.println(ret);
			return ret;
		}
	}
	
}
