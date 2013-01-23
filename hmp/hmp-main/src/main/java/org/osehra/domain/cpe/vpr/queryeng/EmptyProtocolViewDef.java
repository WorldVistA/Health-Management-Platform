package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.format.HL7DateTimeFormat;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.QueryColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.EmptyProtocolViewDef")
public class EmptyProtocolViewDef extends ProtocolViewDef {
	
	public EmptyProtocolViewDef() {
		declareParam(new ViewParam.ViewInfoParam(this, "Empty Protocol"));
		declareParam(new ViewParam.PatientIDParam());
		
		QueryDef qry = new QueryDef();
		Query q1 = new JDSQuery("focus", qry, "/vpr/{pid}/last/vs-type?range=HEIGHT") {
			@Override
			protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
				Map<String, Object> ret = new HashMap<String, Object>();
				PointInTime obs = HL7DateTimeFormat.parse((String) row.get("observed"));
				ret.put("uid", row.get("uid"));
				ret.put("focus", "Height");
				ret.put("status", getOverdueStatus(obs, Integer.MAX_VALUE));
				ret.put("relevant_data", row.get("summary"));
				ret.put("last_done", row.get("observed"));
				ret.put("guidelines", "21+yo: once after age 21");
				return ret;
			}
		};
		addQuery(new ForceSingleRowQuery(q1));
		
		
		addColumns(q1, "focus", "status", "relevant_data");
		getColumn("status").setMetaData("width", 75);
		getColumn("relevant_data").setMetaData("width", 150);
		addColumn(new HL7DTMColDef(q1, "last_done")).setMetaData("width", 85);
		addColumn(new QueryColDef(q1, "guidelines")).setMetaData("width", 250);
	}
}
