package org.osehra.cpe.vpr.queryeng;

import static org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria.where;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDef.QueryFieldTransformer.ReplaceTransformer;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component(value="org.osehra.cpe.vpr.queryeng.LabTrendViewDef")
@Scope("prototype")
public class LabTrendViewDef extends ViewDef {
	
	public LabTrendViewDef() throws IOException {
		// declare the view parameters
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.ViewInfoParam(this, "Lab Trend"));
		declareParam(new ViewParam.DateRangeParam("range", "N-1Y")); // defaults to past year
		declareParam(new ViewParam.RequiredParam("filter_typeCodes"));
		declareParam(new ViewParam.SimpleViewParam("filter_typeCodes"));
		declareParam(new ViewParam.AsArrayListParam("filter_typeCodesAry", "filter_typeCodes"));
		
		QueryDef qry1 = new QueryDef();
		qry1.fields().transform("interpretationCode", new ReplaceTransformer("urn:hl7:observation-interpretation:",""));
		Query q1 = new JDSQuery("observed", qry1, "vpr/{pid}/index/lab-lnc-code?range={filter_typeCodes}&filter=between(observed,\"{range.startHL7}\",\"{range.endHL7}\")") {
			@Override
			protected Map<String, Object> mapRow(RenderTask task, Map<String, Object> row) {
				List<String> list = (List<String>) task.getParamObj("filter_typeCodesAry");
				String type = row.get("typeCode").toString();
				String obs = row.get("observed").toString();
				Object units = row.get("units");
				Object result = row.get("resultNumber");
				Object interp = row.get("interpretationCode");
				int id = list.indexOf(type);
				if (id < 0 || result == null) {
					return null;
				}
				
				// TODO: Add the detail rows back in here as: "detail_" + name, row
				String detail = result + " " + ((units != null) ? units : "");
				String interpStr = null;
				if (interp != null) {
					// TODO: Hack, this should be a CSS style
					interpStr = interp.toString().replace("urn:hl7:observation-interpretation:", "");
					detail += " <span style='color: red; font-weight: bold;'>" + interpStr + "</span>";
				}
				return buildRow("observed", obs, id, result, id + "_detail", detail, id + "_units", units, id + "_interpret", interpStr);
			}
		};
		addColumn(new ColDef.HL7DTMColDef(q1, "observed"));
		addQuery(q1);
		
		// OLD Query based on typeName, not used anymore
		QueryDef qry = new QueryDef();
		qry.fields().alias("typeName", "name").alias("typeCode", "type").alias("displayName", "display")
			.alias("interpretationCode", "interpretation").alias("resultStatusCode", "status");
		
		// TODO: Would like to do: coalesce(c.display_name,c.type_name) as "display"
		qry.addCriteria(where("categoryCode").in("?:qfilter.catCodes"));
		qry.addCriteria(where("typeCode").in("?:qfilter.typeCodes"));
//		qry.addCriteria(where("typeName").in("?:qfilter.typeNames"));
		//qry.addCriteria(where("interpretationCode").ne(null)); // TODO: how to handle this?
		// TODO: Issue trying to get this to work at Bay Pines: typeName for A1C is HBA1C vs HEMOGOBIN A1C for test account.
		//       Also, need to use the proper index for this (/index/lab-type), also would be nice to use loinc code instead.
		
		Query qOLD = new JDSQuery("observed", qry, "vpr/{pid}/index/laboratory") {
			Map<String, String> nameToIdMap = new HashMap<String, String>();
			@Override
			public void exec(RenderTask renderer) throws Exception {
				// insert a column for each lab value requested
				List<String> types = (List<String>) renderer.getParamObj("filter_typeNames");
				nameToIdMap.clear();
				String lastName = null;
				for (String type : types) {
					String[] vals = type.split("\\^");
					String id = vals[0];
					if(vals.length>1) {
						String name = vals[1];
						lastName = name;
						nameToIdMap.put(name, id);
					} else if (lastName!=null && id!=null && !id.equals("")) {
						/*
						 * UGLY HACK WARNING
						 * 
						 * This is an ugly case where the lab name contains a comma, resulting in a false array element.
						 * This traces all the way back to the HttpRequest parameters, so I don't see a way around this. 
						 * 
						 * When this happens, the hack is to go back to the last processed name and concat with the current name,
						 * because they were really supposed to be one name in the first place.
						 * 
						 */
						nameToIdMap.put(lastName + "," + id, nameToIdMap.get(lastName));
						nameToIdMap.put(lastName, null);
						lastName = lastName + "," + id;
					}
				}
				for(String name: nameToIdMap.keySet()) {
					String id = nameToIdMap.get(name);
					addColumn(new ColDef.QueryColDef(this, id)).setMetaData("hidden", true);
					addColumn(new ColDef.QueryColDef(this, id + "_detail")).setMetaData("text", name).setMetaData("hidden", false);
					addColumn(new ColDef.QueryColDef(this, id + "_units")).setMetaData("text", name).setMetaData("hidden", true);
					addColumn(new ColDef.QueryColDef(this, id + "_interpret")).setMetaData("text", name).setMetaData("hidden", true);
				}
				
				if (types.size() == 0) {
					return;
				}
				
				super.exec(renderer);
			}
			
			@Override
			protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
				String name = row.get("name").toString();
				String obs = row.get("observed").toString();
				Object units = row.get("units");
				Object result = row.get("result");
				Object interp = row.get("interpretation");
				
				// convert result to int?
				try {
					if (result != null) result = Float.parseFloat(result.toString());
				} catch (NumberFormatException ex) {
					// not a number?
				}
				
				// TODO: Add the detail rows back in here as: "detail_" + name, row
				String detail = result + " " + ((units != null) ? units : "");
				String interpStr = null;
				if (interp != null) {
					// TODO: Hack, this should be a CSS style
					interpStr = interp.toString().replace("urn:hl7:observation-interpretation:", "");
					detail += " <span style='color: red; font-weight: bold;'>" + interpStr + "</span>";
				}
				String id = nameToIdMap.get(name);
				return id!=null?buildRow("observed", obs, id, result, id + "_detail", detail, id + "_units", units, id + "_interpret", interpStr):null;
			}
		};

	}
}
