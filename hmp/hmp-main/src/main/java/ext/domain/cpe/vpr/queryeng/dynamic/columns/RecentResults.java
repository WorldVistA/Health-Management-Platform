package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.auth.AuthController;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.RecentResults")
@Scope("prototype")
public class RecentResults extends ViewDefDefColDef {

    public RecentResults() {
    	super(null);
    }
    
    public RecentResults(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public RecentResults(Environment env) {
		super();
		fieldName = "Recent Lab Results";
		getViewdefFilters().put("range", "-1y");
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.LabViewDef";
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
		return "Recent Lab Results";
	}
	
	public List<Config> getConfigOptions() {
		
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("typeFilters");
		conf.setLabel("Types (Blank=ALL)");
		conf.setDataType(Config.DATA_TYPE_STRING);
		opts.add(conf);
		conf = new Config();
		conf.setName("nameInTooltip");
		conf.setLabel("Show Name of Lab in Tooltip instead of Column");
		conf.setDataType(Config.DATA_TYPE_BOOLEAN);
		opts.add(conf);
		return opts;
	}
	
	public List<Config> getViewdefFilterOptions() {

		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("range");
		conf.setLabel("Observed Range");
		opts.add(conf);
		return opts;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		boolean showNameInTooltip = configProperties.get("nameInTooltip")!=null?Boolean.parseBoolean(configProperties.get("nameInTooltip").toString()):false;
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			ArrayList<String> filterz = configPropertyToArray("typeFilters", configProperties);
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				String type = itm.get("display").toString();
				if(filterz == null || poorManFuzzySearch(filterz, type)) {
					boolean low = false;
					boolean high = false;
					try {
						if(itm.get("low") != null && itm.get("high") != null && itm.get("result") != null) {
							double lowLimit = Double.parseDouble(itm.get("low").toString());
							double highLimit = Double.parseDouble(itm.get("high").toString());
							double val = Double.parseDouble(itm.get("result").toString());
							low = val<lowLimit;
							high = val>highLimit;
						}
					}
					catch(NumberFormatException e) {
						log.error("RecentResults: Couldn't parse number; low="+itm.get("low")+" high="+itm.get("high")+" rslt="+itm.get("result"));
					}
					results.add((low?"<font color=BLUE>":"") 
							+(high?"<font color=RED>":"")
							+(showNameInTooltip?"<text title='"+itm.get("display")+"'>":itm.get("display")+": ")
							+itm.get("result")+" "+itm.get("units")
							+(showNameInTooltip?"</text>":"")
							+((low||high)?"</font>":"")
							+"<br>");
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
		return "All results within the specified time period that match the specified free-text (CSV) filter. High and low values are colored red and blue, respectively.";
	}
}
