package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.format.HL7DateTimeFormat;
import org.osehra.cpe.datetime.format.PointInTimeFormat;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;
import org.osehra.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter;

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

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.MedsDue")
@Scope("prototype")
public class MedsDue extends ViewDefDefColDef {

	public MedsDue() {
		super(null);
	}
	
	public MedsDue(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public MedsDue(Environment env) {
		super();
		fieldName = "Inpt. Meds Due";
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
		return "org.osehra.cpe.vpr.queryeng.MedsViewDef";
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
		conf.setChoiceList(new ArrayList<String>(Arrays.asList("ACTIVE","PENDING","DISCONTINUED","EXPIRED")));
		opts.add(conf);
		conf = new Config();
		conf.setName("range");
		conf.setLabel("Start Date Range");
		opts.add(conf);
		conf = new Config();
		conf.setName("filter_kind");
		conf.setLabel("Type");
		conf.setDataType(Config.DATA_TYPE_LIST);
		conf.setChoiceList(new ArrayList<String>(Arrays.asList( "ALL", "O", "I", "N")));
		opts.add(conf);
		return opts;
	}
	
	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Meds Due";
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
			ArrayList<String> filterz = configPropertyToArray("qualifiedName", configProperties);
			PointInTime now = PointInTime.now();
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				String type = itm.get("ingredientName").toString();
				if(type != null && (filterz == null || poorManFuzzySearch(filterz, type))) {
					List<Map<String, Object>> dosages = (List<Map<String, Object>>)itm.get("dosages");
					if(dosages!=null) {
						for(Map<String, Object> dosage: dosages) {
							if(dosage.get("start")!=null && new PointInTime(dosage.get("start").toString()).compareTo(now)<1) {
								if(dosage.get("stop")==null || new PointInTime(dosage.get("stop").toString()).compareTo(now)>0) {
									String prefix = showNameInTooltip?"<text title='"+itm.get("ingredientName")+"'>":"";
									String suffix = showNameInTooltip?"</text>":" "+itm.get("ingredientName");
									if(dosage.get("adminTimes")!=null) {
										String[] dt = dosage.get("adminTimes").toString().split("-");
										for(String time: dt) {
											if(time.length()==2) {time = time + ":00";}
											results.add(prefix+time+suffix+"<br>");
										}
									} else {
										results.add(prefix+PointInTimeFormat.time().print(HL7DateTimeFormat.parse(dosage.get("start").toString()))+suffix+"<br>");
									}
								}
							}
						} 
					}
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
		return "TODO: This needs to pull from BCMA instead of the dosage history on meds.";
	}
}
