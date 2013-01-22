package EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;
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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.LabDraws")
@Scope("prototype")
public class LabDraws extends ViewDefDefColDef {
	
    public LabDraws() {
    	super(null);
    }
    
    public LabDraws(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public LabDraws(Environment env) {
		super();
		fieldName = "Lab Draws";
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	
	@Override
	public String getViewdefCode() {
		return "EXT.DOMAIN.cpe.vpr.queryeng.OrdersViewDef";
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
		return "Lab Draws";
	}
	
	// Mandatory Imaging filter
	@Override
	public Map<String, Object> getViewdefFilters() {
		/*
		 *  Mandatory displayGroup values gleaned from FileMan Laboratory structure.
		 *  TODO: Confirm that these four values cover all inpatient nursing lab draw types that will be applicable.
		 */
		viewdefFilters.put("filter_group", Arrays.asList("LAB","CH","HEMA","MI"));
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
				if(start.getDate() == PointInTime.now().getDate() && start.getMonth() == PointInTime.now().getMonth() && start.getYear() == PointInTime.now().getYear()) {
					String time = start.getPrecision().greaterThan(Precision.HOUR)?"<i>Anytime</i>": PointInTimeFormat.dateTime().print(start);
					String detail = (itm.get("summary")!=null?itm.get("summary"):itm.get("content")!=null?itm.get("content"):itm.get("oiName")).toString();
					results.add("<text title='"+detail+"'>"+time+"</text><br>");
				}
			}
		} catch (ViewDefRenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Cannot render LabDraws: "+e.getMessage(), e);
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

	@Override
	public String getDescription() {
		return "All lab orders that have a start time of today and fall into lab-type display groups (\"LAB\",\"CH\",\"HEMA\",\"MI\")";
	}

}
