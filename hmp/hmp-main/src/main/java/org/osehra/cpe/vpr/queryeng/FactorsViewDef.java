package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.HealthFactor;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.FactorsViewDef")
@Scope("prototype")
public class FactorsViewDef extends ViewDef{

	@Autowired
	public FactorsViewDef(Environment env)  throws SQLException, Exception {
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Health Factors"));
        declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.AsArrayListParam("filter.typeCodes"));
		
		String displayCols = "Summary,DateTime,Comments,Facility";
		String requireCols = "Summary,DateTime,Comments,Facility";
		String hideCols = "uid,selfLink";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		// Relevant Health Factors
		QueryDef qry = new QueryDef();
		qry.fields().alias("summary", "Summary").alias("recorded","DateTime").alias("comment", "Comments").alias("facilityName", "Facility");
		Query q1 = new JDSQuery("uid", qry, "/vpr/{pid}/index/healthfactor?order=desc");

		addQuery(q1);
		addColumns(q1, "uid", "Summary", "DateTime", "Comments", "Facility");

        getColumn("Summary").setMetaData("text", "Description");
        getColumn("Summary").setMetaData("flex", 1);

        addColumn(new HL7DTMColDef(q1, "DateTime")).setMetaData("text", "Onset Date");
        getColumn("DateTime").setMetaData("width", 75);

        getColumn("Comments").setMetaData("text", "Comments");
        getColumn("Facility").setMetaData("text", "Facility");
        addColumn(new DomainClassSelfLinkColDef("selfLink", HealthFactor.class));
	}
}

