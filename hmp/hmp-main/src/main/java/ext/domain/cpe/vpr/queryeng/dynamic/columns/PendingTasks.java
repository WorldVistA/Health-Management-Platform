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
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.PendingTasks")
@Scope("prototype")
public class PendingTasks extends ViewDefDefColDef {

	@Autowired
	FrameRegistry registry;
	
	@Autowired	
	JSONViewRenderer2 renderer2;
	
	@Autowired
	ApplicationContext ctx;

    public PendingTasks() {
    	super(null);
    }
    
    public PendingTasks(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public PendingTasks(Environment env) {
		super();
		fieldName = "Pending Tasks";
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
		return "org.osehra.cpe.vpr.queryeng.TasksViewDef";
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
		return "Pending Tasks";
	}
	
	// Mandatory Imaging filter
	@Override
	public Map<String, Object> getViewdefFilters() {
		viewdefFilters.put("filter.complete", "false");
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
				/*
				 * This is a blatantly ugly hack that is just intended as a proof of concept.
				 * The column really wants to play a dual role, as both an action and informational column.
				 * 
				 * A.K.A. "Black Magic part 1 of 2"
				 */
				results.add("<a href=\"javascript:;\" onmousedown=\"org.osehra.cpe.TaskWindow.showTaskForPatient(event, "+configProperties.get("pid")+")" +
						//"var taskWindow = org.osehra.cpe.TaskWindow.showTaskForPatient("+configProperties.get("pid")+", true);" +//Ext.getCmp('taskWindow');" +
						"\">Add Task</a><br>");
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
		return "All incomplete tasks.";
	}

}
