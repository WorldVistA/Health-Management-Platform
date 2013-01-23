package org.osehra.cpe.vpr.queryeng;

import static org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria.where;
import org.osehra.cpe.vpr.Procedure;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.ProceduresViewDef")
@Scope("prototype")
class ProceduresViewDef extends ViewDef {
	@Autowired
    public ProceduresViewDef(Environment env) {
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Procedures"));
		declareParam(new ViewParam.DateRangeParam("range", null));
		declareParam(new ViewParam.ENUMParam("filter_kind", "", "", "Procedure", "Imaging", "Consult", "Surgery").addMeta("multiple", true).addMeta("title", "Type Filter"));
        declareParam(new ViewParam.QuickFilterParam("qfilter_kind", "", "Procedure", "Imaging", "Consult", "Surgery"));
        declareParam(new ViewParam.PatientIDParam());
		String displayCols = "summary,dateTime,kind,status,service,consultProcedure,facilityName";
		String requireCols = "summary,dateTime,kind,status,service,consultProcedure,facilityName";
		String hideCols = "uid,selfLink,orderId";
		String sortCols = "summary,dateTime,kind,status,service,consultProcedure,facilityName";
		String groupCols = "kind,status,service,consultProcedure,facilityName";
		declareParam(new ViewParam.SortParam("dateTime", false));
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		declareParam(new ViewParam.AsArrayListParam("filter_kind"));
		declareParam(new ViewParam.AsArrayListParam("qfilter_kind"));
		
		Query q1 = null;
		QueryDef qry = new QueryDef();
		qry.fields().alias("typeName","TypeName").alias("typeCode","TypeCode");
		qry.fields().alias("category","Category").alias("reason","Reason").alias("orderUid","orderId");
		qry.addCriteria(where("pid").is(":pid"));
		qry.addCriteria(where("kind").in("?:filter_kind"));
		qry.addCriteria(where("kind").in("?:qfilter_kind"));
		q1 = new JDSQuery("uid",qry,"vpr/{pid}/index/procedure?order=#{getParamStr('sort.ORDER_BY')}" +
			"#{getParamStr('range.startHL7')!=null?'&filter=between(dateTime,\"'+getParamStr('range.startHL7')+'\",\"'+getParamStr('range.endHL7')+'\")':''}");
		
		addColumns(q1, "uid", "kind", "TypeName", "TypeCode", "Category", "Reason", "summary", "status", "facilityName", "orderId", "consultProcedure", "service");

		addColumn(new HL7DTMColDef(q1, "dateTime")).setMetaData("text", "Date/Time");
        getColumn("summary").setMetaData("text", "Summary").setMetaData("flex", 1);
        getColumn("consultProcedure").setMetaData("text", "Consult Procedure");
        getColumn("facilityName").setMetaData("text", "Facility");
        getColumn("kind").setMetaData("text", "Type");
        getColumn("status").setMetaData("text", "Status");
        getColumn("summary").setMetaData("text", "Summary");
        getColumn("service").setMetaData("text", "Service");

        addColumn(new DomainClassSelfLinkColDef("selfLink", Procedure.class));
		addQuery(q1);
	}
}
