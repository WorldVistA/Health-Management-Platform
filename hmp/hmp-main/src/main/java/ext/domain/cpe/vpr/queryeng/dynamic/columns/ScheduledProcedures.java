package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.format.HL7DateTimeFormat;
import org.osehra.cpe.datetime.format.PointInTimeFormat;
import org.osehra.cpe.vpr.frameeng.FrameRegistry;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2.JSONViewRenderer2;
import org.osehra.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class ScheduledProcedures extends ViewDefDefColDef {

	@Autowired
	FrameRegistry registry;
	
	@Autowired	
	JSONViewRenderer2 renderer2;
	
	@Autowired
	ApplicationContext ctx;

    public ScheduledProcedures() {
    	super(null);
    }
    
    public ScheduledProcedures(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public ScheduledProcedures(Environment env) {
		super();
		fieldName = "Scheduled Procedures";
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	
	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.OrdersViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		return "/rollup/brList";
	}

	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Scheduled Procedures";
	}
	
	// Mandatory Imaging filter
	@Override
	public Map<String, Object> getViewdefFilters() {
		return viewdefFilters;
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
				results.add("Due "+ PointInTimeFormat.dateTime().print(HL7DateTimeFormat.parse(itm.get("dueDate").toString()))+": "+itm.get("summary").toString()+"<br>");
			}
			if(!found) {
				results.add("<a href='makeTask'>Add Task</a><br>");
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
		return "TODO: Unfinished; Returns any orders for procedures (Imaging, Consult)";
	}
}
