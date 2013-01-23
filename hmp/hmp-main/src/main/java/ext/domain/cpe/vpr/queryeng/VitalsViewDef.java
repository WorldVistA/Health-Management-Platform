package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.TemplateColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria;

import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.VitalsViewDef")
public class VitalsViewDef extends ViewDef {
	
	public VitalsViewDef() {
		declareParam(new ViewParam.ViewInfoParam(this, "Vital Signs"));
		declareParam(new ViewParam.ENUMParam("filter_kind", null, "WEIGHT","PAIN","PULSE","PULSE OXIMETRY","HEIGHT","RESPIRATION","BLOOD PRESSURE","TEMPERATURE").addMeta("multiple", true).addMeta("title", "Type filter"));
        declareParam(new ViewParam.AsArrayListParam("filter_kind"));
		declareParam(new ViewParam.DateRangeParam("range", null));
        declareParam(new ViewParam.PatientIDParam());
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "observed,typeName,value,range";
		String requireCols = "observed,typeName,value";
		String hideCols = "uid,result,units,selfLink";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		QueryDef qry = new QueryDef();
		qry.fields().include("uid","observed","typeName","result","units");
		qry.addCriteria(QueryDefCriteria.where("typeName").in("?:filter_kind"));
		JDSQuery primary = new JDSQuery("uid", qry, "vpr/{pid}/last/vs-type?order=observed DESC" +
				"#{getParamStr('range.startHL7')!=null?'&filter=between(observed,\"'+getParamStr('range.startHL7')+'\",\"'+getParamStr('range.endHL7')+'\")':''}");
		addQuery(primary);
		addColumns(primary, "uid","typeName","result","units");
		addColumn(new HL7DTMColDef(primary, "observed")).setMetaData("text", "Observed");
		getColumn("typeName").setMetaData("text", "Type Name");
		addColumn(new TemplateColDef("value", "{result} {units}")).setMetaData("text", "Result");
	}
}
