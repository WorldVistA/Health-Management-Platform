package org.osehra.cpe.vpr.web

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vista.rpc.RpcTemplate
import org.osehra.cpe.vpr.Observation
import org.osehra.cpe.vpr.PatientAlert
import org.osehra.cpe.vpr.UidUtils
import org.osehra.cpe.vpr.frameeng.CallEvent
import org.osehra.cpe.vpr.frameeng.FrameJob
import org.osehra.cpe.vpr.frameeng.FrameRegistry
import org.osehra.cpe.vpr.frameeng.FrameRunner
import org.osehra.cpe.vpr.frameeng.IFrame
import org.osehra.cpe.vpr.frameeng.IFrameEvent.InvokeEvent
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.pom.IPatientObject
import org.osehra.cpe.vpr.pom.POMUtils
import org.osehra.cpe.vpr.pom.PatientEvent
import org.osehra.cpe.vpr.pom.jds.JdsTemplate
import org.osehra.cpe.vpr.termeng.TermEng
import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory
import grails.converters.JSON

import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView

@Controller
class FrameController {

	@Autowired
	FrameRegistry registry;
	
	@Autowired
	FrameRunner runner;

	@Autowired
	IGenericPatientObjectDAO dao
	
	@Autowired
	IPatientDAO patdao
	
	@Autowired
	ApplicationContext ctx
	
	@Autowired
	JdsTemplate tpl
	
	@Autowired
	RpcTemplate rpcTemplate;
	
	@Autowired
	TermEng eng;
	
	@RequestMapping(value = "/frame/test")
	ModelAndView testEvent(@RequestParam("urn") String urn) {
		// get the domain object for urn
		IPatientObject obj = dao.findByUID(UidUtils.getDomainClassByUid(urn), urn);
		if (obj == null) {
			throw new RuntimeException("No object found for urn: " + urn);
		}
		
		// wrap the obj in an event
		PatientEvent evt = new PatientEvent(obj);
		
		// submit it ot the frame runner
		FrameJob job = runner.exec(evt);
		
		// dump the results
		return ModelAndViewFactory.contentNegotiatingModelAndView(job);
	}
	
	@RequestMapping(value = "/frame/call")
	ModelAndView call(@RequestParam("urn") String urn, @RequestParam("frame") String frameID, HttpServletRequest request) {
		// get the domain object for urn
		IPatientObject obj = dao.findByUID(UidUtils.getDomainClassByUid(urn), urn);
		if (obj == null) {
			throw new RuntimeException("No object found for urn: " + urn);
		}
		
		// wrap the obj in an event
		CallEvent evt = new CallEvent();
		
		// submit it ot the frame runner
		FrameJob job = runner.exec(evt);
		
		// dump the results
		return ModelAndViewFactory.contentNegotiatingModelAndView(job);
	}

	
	/**
	 * This should be able to replace the view controller eventually.  Specifically invokes a specified frame.
	 * @param xyz
	 * @return
	 */
	@RequestMapping(value = "/frame/invoke")
	ModelAndView exec(@RequestParam(value="uid", required=false) String uid, String entryPoint, HttpServletRequest request) {
		def frames = [:];
		def params = extractParams(request);
		def clazz = UidUtils.getDomainClassByUid(uid);
		def obj = (uid && clazz) ? dao.findByUID(clazz, uid) : patdao.findByAnyPid(uid);

		// pass the results though as a string
		FrameJob job = runner.exec(new InvokeEvent(entryPoint, obj, params));
		for (IFrame f in job.getFrames()) {
			frames.put(f.getID(), f.getName());
		}
		return ModelAndViewFactory.contentNegotiatingModelAndView([actions: job.getActions(), frames: frames])
	}
	
	private GrailsParameterMap extractParams(HttpServletRequest request) {
		def sort = request.getParameter("sort");
		def group = request.getParameter("group");
		def params = new GrailsParameterMap(request);
		if (sort != null) {
			params.put("sort", JSON.parse(sort));
		}
		if (group != null) {
			params.put("group", JSON.parse(group));
		}
		if (request.getMethod().equals("POST")) {
			params.putAll(JSON.parse(request));
		}
		return params;
	}
	
	/**
	 * There are 2 ways an alert might want to be rendered.
	 * 
	 * 1) a stored alert (exists in the cache under the specified UID)
	 * 2) a generated alert (does not exist, but should have the same fields)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/frame/alert")
	ModelAndView renderAlert(HttpServletRequest request) {
		request.get
		def params = new GrailsParameterMap(request);
		def alert = (params.uid) ? dao.findByUID(params.uid) : null;
		def frame = registry.findByID(params.frameID);
		def links = [];
		if (alert == null) {
			BufferedReader reader = request.getReader();
			StringBuffer sb = new StringBuffer();
			String line = reader.readLine();
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
			
			alert = POMUtils.newInstance(PatientAlert.class, sb.toString())
		}
		
		if (alert != null) {
			for (Map m : alert.getLinks()) {
				if (m.uid) links.add(dao.findByUID(m.uid))
			}
		}
		return new ModelAndView('/frame/alert', [params: params, alert: alert, frame: frame, links: links]);
	}

	@RequestMapping(value = "/frame/info/{uid}")
	ModelAndView renderInfo(@PathVariable(value="uid") String uid) {
		IFrame frame = registry.findByID(uid);
		if (!frame) {
			throw new BadRequestException("unknown frame uid: " + uid)
		}

		return new ModelAndView('/frame/info', [frame: frame, meta: frame.getMeta(), stats: registry.getFrameStats(frame)]);
	}
	
	@RequestMapping(value = "/frame/info")
	ModelAndView renderInfo2(@RequestParam(value="uid") String uid) {
		IFrame frame = registry.findByID(uid);
		if (!frame) {
			throw new BadRequestException("unknown frame uid: " + uid)
		}

		return new ModelAndView('/frame/info', [frame: frame, meta: frame.getMeta(), stats: registry.getFrameStats(frame)]);
	}


	@RequestMapping(value = "/frame/goal/{id}/{pid}")
	ModelAndView renderGoal(@PathVariable(value="id") String id, @PathVariable(value="pid") String pid) {
		return new ModelAndView('/frame/' + id, [pid:pid, dao: dao, rpc: rpcTemplate]);
	}
	
	@ResponseBody
	@RequestMapping(value = "/frame/param/delete/{frame}")
	public String delParam(@PathVariable String frame, @RequestParam(value="pid") String pid) {
		String uid = "urn:va:::frame:" + frame;
		tpl.delete("/vpr/" + pid + "/" + uid);
		return "Deleted";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/frame/param/set/{frame}")
	public String setParam(@PathVariable String frame, @RequestParam(value="pid") String pid, @RequestParam Map params) {
		// fetch the current values
		String uid = "urn:va:::frame:" + frame;
		Map data = null;
		try {
			data = tpl.getForMap("/vpr/" + pid + "/" + uid);
		} catch (Exception ex) {
			// TODO: not found!?!
		}
		if (!data) {
			data = [uid: uid, pid: pid]
		}
		
		// add the specified values
		data.putAll(params);
		
		// update the VPR results
		tpl.postForLocation("/vpr/" + pid, data);
		return "Saved";
	}
	
	@ResponseBody
	@RequestMapping(value = "/frame/param/get/{frame}")
	public String getParam(@PathVariable String frame, @RequestParam(value="pid") String pid, @RequestParam(value="key") String key) {
		String uid = "urn:va:::frame:" + frame;
		try {
			def data = tpl.getForMap("/vpr/" + pid + "/" + uid);
			data = data.get("data").get("items").get(0);
			if (key != null) {
				return data.get(key);
			}
			return data;
		} catch (Exception ex) {
		  // TODO: not found!?!
			return "";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/frame/obs/set/{pid}/{key}")
	public String addObservation(@PathVariable pid, @PathVariable String key, @RequestParam value, @RequestParam(required=false) observed) {
		String uid = "urn:va:::obs:" + key;
		
		def data = [uid: uid, pid: pid, entered: PointInTime.now(), kind: "Clinical Observation",
				typeCode: key, typeName: eng.getDescription(key), result: value, observed: observed]
		def item = new Observation();
		item.setData(data);
		dao.save(item);
		runner.pushEvents(item);  // TODO: This should not be here... 
	}
	
	@ResponseBody
	@RequestMapping(value = "/frame/obs/delete/{pid}/{key}")
	public String delObservation(@PathVariable pid, @PathVariable String key) {
		String uid = "urn:va:::obs:" + key;
		dao.deleteByUID(Observation, uid);
	}
}
