package EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.format.PointInTimeFormat;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderException;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2;
import EXT.DOMAIN.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.RecentVitalSigns")
@Scope("prototype")
public class RecentVitalSigns extends ViewDefDefColDef {

	public RecentVitalSigns() {
		super(null);
	}
	
	public RecentVitalSigns(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public RecentVitalSigns(Environment env) {
		super();
		fieldName = "Last Vital Signs";
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "EXT.DOMAIN.cpe.vpr.queryeng.VitalsViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		return "/rollup/brList";
	}
	
	public List<Config> getViewdefFilterOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("filter_kind");
		conf.setLabel("Type");
		conf.setDataType(Config.DATA_TYPE_LIST);
		conf.setChoiceList(new ArrayList<String>(Arrays.asList("WEIGHT","PAIN","PULSE","PULSE OXIMETRY","HEIGHT","RESPIRATION","BLOOD PRESSURE","TEMPERATURE")));
		opts.add(conf);
		conf = new Config();
		conf.setName("range");
		conf.setLabel("Observed Range");
		opts.add(conf);
		return opts;
	}
	
	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Last Vital Signs";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> results = new ArrayList<String>();
		RenderTask task;
		try {
			task = ((ViewDefRenderer2) map.get("renderer")).render(((ViewDef)map.get("viewdef")), ((Map<String, Object>)map.get("params")));
			Iterator<Map<String, Object>> iter = task.iterator();
			Map<String, Object[]> typeValues = new HashMap<String, Object[]>();
			while(iter.hasNext()) {
				Map<String, Object> itm = iter.next();
				Object observed = itm.get("observed");
				Object typeName = itm.get("typeName");
				Object value = "" + itm.get("result") + itm.get("units");
				if(observed != null && typeName != null && value != null) {
					PointInTime obs = new PointInTime(observed.toString());
					Object[] oldVals = typeValues.get(typeName.toString());
					if(oldVals!=null && ((PointInTime)oldVals[0]).compareTo(obs)>0) {
						// do nothing.
					} else {
						oldVals = new Object[2];
						oldVals[0] = obs;
						oldVals[1] = value;
						typeValues.put(typeName.toString(), oldVals);
					}
				}
			}
            DateTimeFormatter timeFormatter = PointInTimeFormat.forPattern("MM/dd/yyyy HH:mm");
			for(String typeName: typeValues.keySet()) {
				Object[] vals = typeValues.get(typeName);
                results.add(timeFormatter.print((PointInTime) vals[0])+": "+typeName+" "+vals[1].toString()+"<br>");
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
		return "For each type of vital sign measurement, returns the last chronological instance of that type.";
	}

}
