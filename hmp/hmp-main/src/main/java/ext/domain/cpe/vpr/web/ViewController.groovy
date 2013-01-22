package EXT.DOMAIN.cpe.vpr.web
import javax.jms.Session;

import javax.jms.Destination
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.jms.core.SessionCallback;
import EXT.DOMAIN.cpe.vpr.frameeng.CallEvent;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry
import java.util.Map;

import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vista.rpc.RpcTemplate
import EXT.DOMAIN.cpe.vpr.IAppService
import EXT.DOMAIN.cpe.vpr.NotFoundException
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsTemplate
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.IDynamicViewDefService
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.IViewDefDefDAO;
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.PatientPanelViewDef
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.ViewDefDef
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDefRenderer
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.DeferredViewDefDefColDef
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2.JSONViewRenderer2
import grails.converters.JSON

import javax.jms.Message
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

import org.apache.solr.client.solrj.SolrServer
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.convert.ConversionService
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import EXT.DOMAIN.cpe.vpr.pom.POMUtils

import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.stringModelAndView

@Controller
public class ViewController {

    @Autowired
    RpcTemplate rpcTemplate

    @Autowired
    SolrServer solrServer
	
	@Autowired
	ApplicationContext ctx;
    
    @Autowired
    ConversionService vprConversionService;
	
	@Autowired
	IDynamicViewDefService dynamicViewDefService;

	@Autowired(required=false)
	MongoOperations mongoOperations;
	
	@Autowired
	IPatientDAO patientDAO;
	
	@Autowired
	JdsTemplate tpl;
	
	@Autowired	
	JSONViewRenderer2 renderer2;
	
	@Autowired
	IAppService appService;
	
	@Autowired
	IViewDefDefDAO vddDAO;
	
	@Autowired
	FrameRegistry registry;
	
	@Autowired 
	JmsTemplate jmstemplate
	
	Destination dest
	
	private ViewDef getViewDef(String view) {
		return registry.findByID(view);
	}
	
	@RequestMapping(value = "/vpr/view/listen")
	public ModelAndView listenForUpdates(@RequestParam String pid) {
		
		if (dest == null) {
			jmstemplate.execute(new SessionCallback<Object>() {
				@Override
				public Object doInJms(Session session) throws JMSException {
					dest = jmstemplate.getDestinationResolver().resolveDestinationName(session, "ui.notify", true)
				}
			});
		}
		
		def selector = "pid IS NULL OR pid = '" + pid + "'";
		Message msg = jmstemplate.receiveSelected(dest, selector);
		return contentNegotiatingModelAndView([props: (msg) ? msg.getProperties() : null]);
	}
	
	@RequestMapping(value = "/vpr/view/render")
	public ModelAndView render(@RequestParam String view, HttpServletRequest request, HttpServletResponse response) {
		return render2(view, request, response);
	}
	
	@RequestMapping(value = "/vpr/view/{view}")
	public ModelAndView render2(@PathVariable(value="view") String view, HttpServletRequest request, HttpServletResponse response) {
		def tmp = request.getRequestURI();
		ViewDef viewdef = findViewDef(view);
		def params = extractParams(request);
		
		def mode = request.getParameter("mode");

		if (mode == null || mode.equalsIgnoreCase('json')) {
			// pass the results though as a string
			def String ret = renderer2.renderToString(viewdef, params);
			return contentNegotiatingModelAndView([contentType: "application/json", response: POMUtils.parseJSONtoMap(ret)]);
		} else if (mode == 'html') {
			// TODO: Temporarily not supported anymore
		} else {
			// Check params
			def model = [view: view, viewdef: viewdef, params: params, renderer: renderer2]
			return new ModelAndView(mode, model);
		}
	}
	
	@RequestMapping(value = "/vpr/col/render")
	public ModelAndView renderCol(@RequestParam String view, @RequestParam String board, HttpServletRequest request, HttpServletResponse response) {
		def tmp = request.getRequestURI();
		ViewDef viewdef = findViewDef(view);
		ViewDef brd = findViewDef(board);
		def params = extractParams(request);
		
		def mode = request.getParameter("mode");

		if (mode == null || mode.equalsIgnoreCase('json')) {
			// pass the results though as a string
			def String ret = renderer2.renderToString(viewdef, params);
            return stringModelAndView(ret, "application/json")
		} else if (mode == 'html') {
			// TODO: Temporarily not supported anymore
		} else {
			// Check params
			def model = [view: view, viewdef: viewdef, params: params, renderer: renderer2]
			
			// There's got to be a cleaner way to accomplish this.
//			ViewDef brd = vddDAO.
//			List<ColDef> cols = brd.getColumns();
//			for(ColDef col: cols) {
//				if(col instanceof DeferredViewDefDefColDef) {
//					if(((DeferredViewDefDefColDef)col).cdef.getClass().getName().equals(params.get("code"))) {
//						model = col.cdef.postProcessViewDefRenderedDataBeforeSendingToGSP(model);
//					}
//				}
//			}
			if (params.code && ctx.containsBean(params.code)) {
				def hmpApp = ctx.getBean(params.code);
				if(hmpApp) {
					model = hmpApp.postProcessViewDefRenderedDataBeforeSendingToGSP(model, params);
				}
			}
			
			return new ModelAndView(mode, model);
		}
	}
	
	private ViewDef findViewDef(String name) {
		ViewDef vd = getViewDef(name);
		// if no viewdef was found, return error
		if (vd==null) {
			vd = dynamicViewDefService.getViewDefByName(name);
			if(vd==null) {
				throw new NotFoundException("No view found matching: " + name);
			}
		}
		return vd;
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
		return params;
	}
		
    /*
     * Gets a viewdefs metadata without actually executing any of the queries.
     */
    @RequestMapping(value = ["/vpr/view/meta","/view/meta"])
    @ResponseBody
    String meta(@RequestParam view, HttpServletResponse response) {
        ViewDef viewdef = getViewDef(view);
        if (!viewdef) {
            throw new NotFoundException("Unknown view '${view}'")
        }

		CallEvent<Map<String, Object>> evt = new CallEvent<Map<String, Object>>(viewdef, [:]);
		FrameTask task = new FrameTask(ctx, viewdef, evt, viewdef.getTriggers().get(0));
		RenderTask job = new RenderTask(task, viewdef, viewdef.getPrimaryQuery());
		job.calcParams();
		return renderer2.renderMetaData(job).toString();
    }
	
	// The question came up (DEV meeting 10/3/2012) of how best to combine programmed "HMPApp" objects with user-defined / dynamic instances that fit the HMPApp.
	// OSGI? Brute-force sandwiching for now until we find our ultimate solution.
	// Atlassian? (works on top of OSGI?)
	// Home-grown?
	@RequestMapping(value = ["/vpr/view/ptlists","/view/ptlists"])
	public ModelAndView ptlists(HttpServletRequest rq, HttpServletResponse rsp) {
		def vals = appService.getApps("EXT.DOMAIN.cpe.multipatientviewdef");//.values();
		List<ViewDefDef> vdds = vddDAO.findAll();
		for(ViewDefDef vdd: vdds) {
			ViewDef vd = dynamicViewDefService.getViewDefByName(vdd.name);
			if(vd && vd instanceof PatientPanelViewDef) {
				vals.put(vdd.name, [name: vdd.name, code: vdd.name]);
			}
		}
        return contentNegotiatingModelAndView([items: vals.values()]);
	}
}
