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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.InpatientCheckInTime")
@Scope("prototype")
public class CheckInTime extends ViewDefDefColDef {

	public CheckInTime() {
		super(null);
	}
	
	public CheckInTime(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public CheckInTime(Environment env) {
		super();
		fieldName = "Inpatient Check-In";
//		this.viewdefFilters.put("range", "+0d");
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.AppointmentViewDef";
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
		return "Check-In Time";
	}

	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			PointInTime cit = null;
			String cat = null;
			boolean found = false;
			while(iter.hasNext()) {
				found = true;
				Map<String, Object> itm = iter.next();
				Object cstr = itm.get("checkIn");
				if(cstr!=null) {
					cit = new PointInTime(itm.get("checkIn").toString());
					cat = itm.get("categoryName").toString();
				}
			}
			if(!found) {
				results.add("<i>No Current Appointment</i>");
			} else if(cit!=null) {
				results.add(cat+": "+ PointInTimeFormat.time().print(cit));
			} else {
				results.add("<i>Not Checked In</i>");
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
		return "Check-in time for inpatient appointments. Looks for a 'current' appointment, and if found, looks within that appointment for the checked-in time.";
	}
	
	
}
