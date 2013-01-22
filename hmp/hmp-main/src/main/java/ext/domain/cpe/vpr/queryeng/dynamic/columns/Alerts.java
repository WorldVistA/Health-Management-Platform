package EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns;

import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderException;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.Alerts")
@Scope("prototype")
public class Alerts extends ViewDefDefColDef {

	public Alerts() {
		super(null);
	}
	
	public Alerts(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public Alerts(Environment env) {
		super();
		fieldName = "Alerts";
//		getViewdefFilters().put("filter.frameID", Arrays.asList("I"));
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "EXT.DOMAIN.cpe.vpr.queryeng.AlertViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		return "/rollup/brList";
	}

	@Override
	@JsonIgnore
	public List<Config> getViewdefFilterOptions() {
		ArrayList<Config> opts = new ArrayList<Config>();
		Config conf = new Config();
		conf.setName("filter.frameIDs");
		conf.setLabel("Frame ID(s) (Blank=ALL)");
		conf.setDataType(Config.DATA_TYPE_STRING);
		opts.add(conf);
		
		return opts;
	}
	
	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Alerts";
	}

    @Override
    public String getDescription() {
        return getName();
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
				results.add((String) itm.get("title"));
			}
		} catch (ViewDefRenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			results.add("Error: "+e.getMessage());
		}
		result.put("results",results);
		return result;
	}

}
