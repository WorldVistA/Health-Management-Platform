package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.format.PointInTimeFormat;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;
import org.osehra.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.OtherAppointments")
@Scope("prototype")
public class OtherAppointments extends ViewDefDefColDef {

	public OtherAppointments() {
		super(null);
	}
	
	public OtherAppointments(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public OtherAppointments(Environment env) {
		super();
		fieldName = "Other Appointments";
		getViewdefFilters().put("range", "+1w");
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.EncounterViewDef";
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
		return "Other Appointments";
	}
	
	public List<Config> getViewdefFilterOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("range");
		conf.setLabel("Appointment Date");
		opts.add(conf);
		
		return opts;
	}

	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				PointInTime t = new PointInTime(itm.get("dateTime").toString());
				results.add(itm.get("typeName")+": "+ PointInTimeFormat.forPattern("MM/dd/yyyy HH:mm").print(t)+"<br>");
			}
		} catch (ViewDefRenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

	@Override
	public String getDescription() {
		return "All future appointments (or appointments in the specified time range, can be past as well.)";
	}
}
