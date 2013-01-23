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

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.ActiveOrders")
@Scope("prototype")
public class ActiveOrders extends ViewDefDefColDef {

	public ActiveOrders() {
		super(null);
	}
	
	public ActiveOrders(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public ActiveOrders(Environment env) {
		super();
		fieldName = "Active Orders";
		getViewdefFilters().put("range", "2000..NOW");
		getViewdefFilters().put("qfilter_status", Arrays.asList("ACTIVE"));
		getViewdefFilters().put("filter_kind", Arrays.asList("I"));
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

	public List<Config> getConfigOptions() {
		
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("qualifiedName");
		conf.setLabel("Name (Blank=ALL)");
		conf.setDataType(Config.DATA_TYPE_STRING);
		opts.add(conf);
		
		return opts;
	}
	
	public List<Config> getViewdefFilterOptions() {

		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("qfilter_status");
		conf.setLabel("Status");
		conf.setDataType(Config.DATA_TYPE_LIST);
		conf.setChoiceList(new ArrayList<String>(Arrays.asList("ACTIVE", "PENDING", "CANCELLED", "COMPLETE", "DISCONTINUED", "EXPIRED", "LAPSED", "SCHEDULED", "UNRELEASED", "DISCONTINUED/EDIT")));
		opts.add(conf);
		conf = new Config();
		conf.setName("range");
		conf.setLabel("Start Date Range");
		opts.add(conf);
		conf = new Config();
		conf.setName("filter_group");
		conf.setLabel("Type");
		conf.setDataType(Config.DATA_TYPE_LIST);
		conf.setChoiceList(new ArrayList<String>(Arrays.asList("NURS", "CH","MI","LAB")));
		opts.add(conf);
		return opts;
	}
	
	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Active Orders";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			ArrayList<String> filterz = configPropertyToArray("qualifiedName", configProperties);
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				String type = (itm.get("name")!=null?itm.get("name").toString():null);
				if(type!=null && itm.get("Status")!=null && itm.get("Summary") != null && (filterz == null || poorManFuzzySearch(filterz, type))) {
					results.add(itm.get("Status")+" "+itm.get("Summary")+(itm.get("start")!=null?itm.get("start"):"")+"<br>");
				}
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
		return "Summary list of orders";
	}
}
