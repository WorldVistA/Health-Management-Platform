package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.DietOrders")
@Scope("prototype")
public class DietOrders extends ViewDefDefColDef {

    public DietOrders() {
    	super(null);
    }
    
    public DietOrders(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public DietOrders(Environment env) {
		super();
		fieldName = "Dietary Orders";
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
		return "Dietary Orders";
	}

	@Override
	public void setViewdefFilters(Map<String, Object> viewdefFilters) {
//		this.viewdefFilters = viewdefFilters;
	}
	
	// Mandatory Imaging filter
	@Override
	public Map<String, Object> getViewdefFilters() {
		/*
		 *  Mandatory displayGroup values gleaned from FileMan Dietary structure.
		 */
		viewdefFilters.put("filter_group", Arrays.asList("DIET","DO","TF","D AO","E/L T","PREC","MEAL"));
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
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				PointInTime start = itm.get("start")==null?null:new PointInTime(itm.get("start").toString());
				PointInTime stop = itm.get("stop")==null?null:new PointInTime(itm.get("stop").toString());
				if(start!=null && start.compareTo(PointInTime.now())<1) {
					if(stop==null || stop.compareTo(PointInTime.now())>0) {
						results.add((itm.get("summary")!=null?itm.get("summary"):itm.get("content")!=null?itm.get("content"):itm.get("oiName")).toString()+"<br>");
					}
				}
			}
		} catch (ViewDefRenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Cannot render DietOrders: "+e.getMessage(), e);
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

	@Override
	public String getDescription() {
		return "Returns all dietary orders (\"DIET\",\"DO\",\"TF\",\"D AO\",\"E/L T\",\"PREC\",\"MEAL\") that are current (start before Today and stop after Today.)";
	}
}
