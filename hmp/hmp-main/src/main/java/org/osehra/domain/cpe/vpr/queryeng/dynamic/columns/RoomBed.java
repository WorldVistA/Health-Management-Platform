package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.RoomBed")
@Scope("prototype")
public class RoomBed extends ViewDefDefColDef {

	public RoomBed() {
		super(null);
	}
	
	public RoomBed(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public RoomBed(Environment env) {
		super();
		fieldName = "Room/Bed";
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
	
	public List<Config> getViewdefFilterOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("range");
		conf.setLabel("Start Date Range");
		opts.add(conf);
		return opts;
	}
	
	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Room/Bed";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				Object roomBed = itm.get("roomBed");
				if(roomBed!=null) {
					results.add(roomBed.toString()+(itm.get("current")!=null?itm.get("current"):"")+"<br>");
				}
			}
		} catch (ViewDefRenderException e) {
			e.printStackTrace();
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

	@Override
	public String getDescription() {
		return "Looks at appointments and pulls any room/bed assignments from them.";
	}
}
