package EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.format.HL7DateTimeFormat;
import EXT.DOMAIN.cpe.datetime.format.PointInTimeFormat;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderException;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2.JSONViewRenderer2;
import EXT.DOMAIN.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This class is a complex case.
 * 1) If no CAT scan orders have been placed within (default time, or user spec'ed time) for this patient:
 * 		- Return action advice for creating 
 * @author vhaislchandj
 *
 */
@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.CatScanStatus")
@Scope("prototype")
public class CatScanStatus extends ViewDefDefColDef {
	
	@Autowired
	FrameRegistry registry;
	
	@Autowired	
	JSONViewRenderer2 renderer2;
	
	@Autowired
	ApplicationContext ctx;

    public CatScanStatus() {
    	super(null);
    }
    
    public CatScanStatus(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public CatScanStatus(Environment env) {
		super();
		fieldName = "Cat Scan Results";
		getViewdefFilters().put("range", "-1m");
		getConfigProperties().put("range", "-7d..+1y");
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	
	@Override
	public String getViewdefCode() {
		/**
		 * Here's where we might plug in a frame.. or something with some input/output logic?
		 * Multiple chained frames or viewdefs?
		 * Need to learn more about the frame concept, work with BB on that.
		 */
		return "EXT.DOMAIN.cpe.vpr.queryeng.ProceduresViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		/**
		 * Maybe this should be renamed. So far all of these columns are rendering GSP's, 
		 * but maybe that's just because I'm lazy.
		 */
		return "/rollup/brList";
	}

	@Override
	public String getSummaryType() {
		/**
		 * I can't remember why this was different from getType() above. It might be redundant at this point.
		 */
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "CAT Scan Status";
	}
	
	// Mandatory Imaging filter
	@Override
	public Map<String, Object> getViewdefFilters() {
		viewdefFilters.put("qfilter_kind", "Imaging");
		return viewdefFilters;
	}
	
	public List<Config> getViewdefFilterOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("range");
		conf.setLabel("Imaging reports within");
		opts.add(conf);
		return opts;
	}
	
	public List<Config> getConfigOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("range");
		conf.setLabel("Orders within");
		opts.add(conf);
		return opts;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			boolean found = false;
			while(iter.hasNext()) {
				found = true;
				Map<String, Object> itm = iter.next();
				results.add("("+itm.get("status").toString()+"): "+itm.get("summary").toString()+"<br>");
			}
			if(!found) {
				// Need to render a different viewdef - looking for pending orders.
				String oid = "EXT.DOMAIN.cpe.vpr.queryeng.OrdersViewDef";
				ViewDef viewdef = (ViewDef)(registry!=null?registry.findByID(oid):ctx.getBean(oid, ViewDef.class));
				
				//Map<String, Object> params = new HashMap<String, Object>();
				configProperties.put("filter_group", "CT");
				configProperties.put("pid", task.getParams().get("pid"));

				task = ((ViewDefRenderer2) map.get("renderer")).render(viewdef, configProperties);
				iter = task.iterator();
				while(iter.hasNext()) {
					found = true;
					Map<String, Object> itm = iter.next();
					results.add("Ordered for: "+ PointInTimeFormat.dateTime().print(HL7DateTimeFormat.parse(itm.get("start").toString()))+"<br>");
				}
				if(!found) {
					/*
					 * This is a blatantly ugly hack that is just intended as a proof of concept.
					 * The column really wants to play a dual role, as both an action and informational column.
					 * 
					 * A.K.A. "Black Magic part 1 of 2"
					 */
					results.add("<a href=\"javascript:;\" onmousedown=\"EXT.DOMAIN.cpe.TaskWindow.showTaskForPatient(event, "+configProperties.get("pid")+")" +
							//"var taskWindow = EXT.DOMAIN.cpe.TaskWindow.showTaskForPatient("+configProperties.get("pid")+", true);" +//Ext.getCmp('taskWindow');" +
							"\">Add Task</a><br>");
				}
			}
		} catch (ViewDefRenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Cannot render CatScanStatus: "+e.getMessage(), e);
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

	@Override
	public String getDescription() {
		return "CAT scan results (based on Imaging Reports) within the specified time period. If no results found, " +
				"any orders (again within the specified time period) of type 'CT'. If no orders found, provides an " +
				"action link to create a Task of type 'Order'.";
	}
}
