package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.UnverifiedOrders")
@Scope("prototype")
public class UnverifiedOrders extends ViewDefDefColDef {

	public UnverifiedOrders() {
		super(null);
	}
	
	public UnverifiedOrders(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public UnverifiedOrders(Environment env) {
		super();
		fieldName = "Unverified Orders";
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
		return "Unverified Orders";
	}
	
	@Override
	public List<Config> getConfigOptions() {
		Config conf = new Config();
		conf.setName("verifiedBy");
		conf.setLabel("Verifications");
		conf.setDataType(Config.DATA_TYPE_LIST);
		conf.setChoiceList(new ArrayList<String>(Arrays.asList("Nurse","Clerk","Chart")));
		ArrayList<Config> opts = new ArrayList<Config>();
		opts.add(conf);
		return opts;
	}
	
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		ArrayList<String> rslt = new ArrayList<String>();
		Map<String,Object> finalRslt = new HashMap<String, Object>();
		if(map.get("renderer")!=null && map.get("viewdef") !=null) {
			ViewDefRenderer2 r = (ViewDefRenderer2) map.get("renderer");
			ViewDef d = (ViewDef) map.get("viewdef");
			Map<String, Object> p = (Map<String, Object>) map.get("params");
			try {
				RenderTask t = r.render(d, p);
				Collection<Map<String, Object>> dat = t.getRows();
				for(Map<String, Object> val: dat) {
					if(val.get("Status") != null && val.get("Status").toString().equalsIgnoreCase("PENDING")) {
						boolean nurse = val.get("nurseVerify")!=null;
						boolean clerk = val.get("clerkVerify")!=null;
						boolean chart = val.get("chartVerify")!=null;
						Object vb = configProperties.get("verifiedBy");
						ArrayList<String> vbl = new ArrayList<String>();
						if(vb instanceof String) {vbl.add((String) vb);}
						else if(vb instanceof String[]) {vbl = new ArrayList<String>(Arrays.asList((String[])vb));}
						boolean add = false;
						if((vbl.contains("Nurse") && !nurse) || (vbl.contains("Clerk") && !clerk) || (vbl.contains("Chart") && !chart)) {
							add = true;
						}
						if(add) {
							rslt.add(val.get("Summary")+"<br>");
						}
					}
				}
			} catch (ViewDefRenderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finalRslt.put("results",rslt);
		return finalRslt;
	}

	@Override
	public String getDescription() {
		return "TODO: Needs filter for nurse/clerk/chart verify; All orders that are missing the specified verifications.";
	}

}
