package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.vpr.frameeng.Frame.FrameExecException;
import org.osehra.cpe.vpr.frameeng.Frame.FrameInitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * TODO: Add paralell rendering back in....
 * 
 * 
 * TODO: Should the FrameJob have a reference to the frameRegistry (sorta a resource thing)
 * - usefull for frames invoking other frames, etc. could keep them namespaced...
 * 
 * TODO: A reference to the frame runner would allow frames to "branch of" additional frames
 * - may be needed instead of appending events to existing job?
 * - could the multi-threaded processing then be moved into the FrameRunner and kept out of the job/task?
 * 
 * TODO: Maybe parallel rendering could be a flag of the Job?
 * - FrameJob would be no (frames cant run in parallel),
 * - FrameTask yes (ViewDefs?)
 * 
 * TODO: Add a supplemental resource list? (addResource(object))
 * - could be useful for passing objects to child tasks?
 */
public class FrameJob implements Iterable <FrameJob.FrameTask> {
	private FrameRegistry registry;
	protected FrameJob parent = null;
	private ApplicationContext ctx;
	private Map<String, Object> params = new HashMap<String, Object>();
	
	// runtime/status info
	protected ExecutorService exec; 
	private List<FrameTask> subtasks = new ArrayList<FrameTask>();

	public FrameJob(ApplicationContext ctx, FrameRegistry registry) {
		this.ctx = ctx;
		this.registry = registry;
	}
	
	public FrameJob(ApplicationContext ctx, ExecutorService exec) {
		this.ctx = ctx;
		this.exec = exec;
	}
	
	// add/get resources ------------------------------------------------------
	
	public Object getResource(String bean) {
		return ctx.getBean(bean);
	}
	
	public <T> T getResource(Class<T> clazz, String name) {
        // try to look it up from spring
        try {
    		return ctx.getBean(name, clazz);
        } catch (NoSuchBeanDefinitionException ex) {
        	throw new RuntimeException("No resource of type " + clazz.getName() + " with name " + name + " found in Spring Context", ex);
        }
	}
	
    public <T> T getResource(Class<T> clazz) {
        // try to look it up from spring
        try {
    		return ctx.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ex) {
        	throw new RuntimeException("No resource of type " + clazz.getName() + " found in Spring Context", ex);
        }
    }
	
	// set/calculate params ---------------------------------------------------
	
	public void setParam(String key, int val) {
        setParam(key, "" + val);
    }
    
	public void setParam(String key, Object val) {
        params.put(key, val);
    }
    
	public void setParams(Map<String,Object> params) {
        setParams("", params);
    }
    
	public void setParams(String prefix, Map<String,Object> params) {
    	if (params == null) return;
        for (String key : params.keySet()) {
            Object val = params.get(key);
            if (val == null) {
                continue;
            }
            
            // if the value is a map, traverse it as well and use a dot notation
            // this is how grails params work
            if (val instanceof Map) {
                setParams(prefix + key + ".", (Map<String, Object>) val);
            }
            
            setParam(prefix + key, val);
        }
    }
    
	// get params -------------------------------------------------------------
	public Map<String, Object> getParams() {
    	Map<String, Object> ret = new HashMap<String, Object>();
    	if (parent != null) {
    		ret.putAll(parent.getParams());
    	}
    	ret.putAll(params);
        return ret;
    }	
    
	@SuppressWarnings("unchecked")
	public <T> T getParam(Class<T> clazz, String key) {
    	Object ret = getParamObj(key);
    	if (ret == null) {
    		return null;
    	} else if (clazz.isInstance(ret)) {
    		return (T) ret;
    	} else if (clazz.equals(String.class)) {
    		return (T) getParamStr(key);
    	} else if (clazz.equals(Integer.class)) {
    		return (T) (Integer) getParamInt(key);
    	} else {
    		// TODO: Plug in spring conversion?
    		throw new IllegalArgumentException("Unrecognized return type: " + clazz);
    	}
    }
    
	public Object getParamObj(String key) {
		Map<String, Object> params = getParams();
    	if (params.containsKey(key)) {
    		// first look for the param in our own context
    		return params.get(key);
    	} else if (parent != null) {
    		// they look for it in the parent context
    		return parent.getParamObj(key);
    	} else {
    		// not found, return null
    		return null;
    	}
    }
    
	public String getParamStr(String key) {
        Object val = getParamObj(key);
        if (val != null) {
            return val.toString();
        }
        return null;
    }
    
	public int getParamInt(String key) {
        Object val = getParamObj(key);
        if (val == null) {
            // null returns -1
            return -1;
        }
        
        // if its a int already, just return it.
        if (val instanceof Integer) {
            return ((Integer) val).intValue();
        }
        
        // otherwise try parsing the string.
        try {
            int ret = Integer.parseInt(val.toString());
            return ret;
        } catch (NumberFormatException ex) {
            // otherwise return -1
            return -1;
        }
    }
	
	
	// add/get tasks/context  -------------------------------------------------
	
	public FrameRegistry getRegistry() {
		return this.registry;
	}
	
	public FrameJob getParentTask() {
		return this.parent;
	}
	
	public FrameJob getParentContext() {
		return getParentTask();
	}
	
	public FrameTask addSubTask(FrameTask task) {
		this.subtasks.add(task);
		return task;
	}
	
	public List<FrameTask> getSubTasks() {
		return this.subtasks;
	}

	public int size() {
		return this.subtasks.size();
	}
	
	@Override
	public Iterator<FrameTask> iterator() {
		return this.subtasks.iterator();
	}
	
	// job aggregate results --------------------------------------------------
	
	public List<IFrame> getFrames() {
		List<IFrame> ret = new ArrayList<IFrame>();
		Iterator<FrameTask> itr = iterator();
		while (itr.hasNext()) {
			FrameTask task = itr.next();
			ret.add(task.getFrame());
		}
		return ret;
	}
	
	public List<FrameAction> getActions() {
		List<FrameAction> actions = new ArrayList<FrameAction>();
		Iterator<FrameTask> itr = iterator();
		while(itr.hasNext()) {
			FrameTask task = itr.next();
			actions.addAll(task.getActions());
		}
		return actions;
	}
	
	public <T extends FrameAction> T getAction(Class<T> clazz) {
		for (FrameAction action : getActions()) {
			if (action.getClass().isAssignableFrom(clazz)) {
				return (T) action;
			}
		}
		return null;
	}
	
	public <T extends FrameAction> List<T> getActions(Class<T> clazz) {
		List<T> ret = new ArrayList<T>();
		for (FrameAction action : getActions()) {
			if (action.getClass().isAssignableFrom(clazz)) {
				ret.add((T) action);
			}
		}
		return ret;
	}
	
	// execute ----------------------------------------------------------------
	
	public void exec() throws FrameExecException {
		Slf4JStopWatch watch = new Slf4JStopWatch("frame.job");
		
		// run all the subtasks (we do not run frames part of the same job in parallel)
		for (FrameTask task : getSubTasks()) {
			task.exec();
		}
		watch.stop();
	}
	
	////////////////////////////// subclasses ///////////////////////////////
	public static class FrameTask extends FrameJob {
		private IFrame frame;
		private IFrameEvent<?> event;
		private IFrameTrigger<?> trig;
		private List<FrameAction> actions = new ArrayList<FrameAction>();

		private Future<FrameJob> taskFuture;
		private long timeoutMS = -1;
		
		public FrameTask(FrameJob parent, IFrame frame, IFrameEvent<?> event, IFrameTrigger<?> trig) {
			this(parent.ctx, frame, event, trig);
			this.parent = parent;
		}
		
		/**
		 * shortcut constructor, when you only want to run a frame, not setup a whole job.
		 */
		public FrameTask(ApplicationContext ctx, IFrame frame, IFrameEvent<?> event, IFrameTrigger<?> trig) {
			super(ctx, (FrameRegistry) null);
			this.frame = frame;
			this.event = event;
			this.trig = trig;
			
			// initalize the default parameters
			setParams(getFrame().getParamDefaultVals());
		}
		
		public IFrameTrigger<?> getFrameTrigger() {
			return trig;
		}
		
		public IFrame getFrame() {
			return this.frame;
		}
		
		public IFrameEvent<?> getTriggerEvent() {
			return event;
		}
		
		public void addAction(FrameAction action) {
			this.actions.add(action);
		}
		
		public List<FrameAction> getActions() {
			return this.actions;
		}
		
		public Map<String, Object> getParams() {
	    	Map<String, Object> ret = super.getParams();
	    	if (event != null) {
	    		ret.putAll(event.getParams());
	    	}
	        return ret;
	    }	

		
		@Override
		public void exec() throws FrameInitException, FrameExecException {
			// TODO:need to be able to calc the params

			// initalize/validate the frame
			getFrame().validate(this);
			
			try {
				// run the frame
				getFrame().exec(this);
			} catch (Exception ex) {
				throw new FrameExecException("Exception while executing frame", ex, getFrame());
			}
			
			// run any subtasks
			Slf4JStopWatch watch = new Slf4JStopWatch("frame.task");
			for (FrameJob task : getSubTasks()) {
				task.exec();
			}
			watch.stop();
		}
	}
}
