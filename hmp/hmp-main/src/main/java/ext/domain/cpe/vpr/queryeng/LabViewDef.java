package org.osehra.cpe.vpr.queryeng;

import static org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria.client;
import static org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria.server;
import org.osehra.cpe.vpr.Result;
import org.osehra.cpe.vpr.queryeng.ColDef.ActionColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.TemplateColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDef.QueryFieldTransformer.HTMLEscapeTransformer;
import org.osehra.cpe.vpr.queryeng.query.QueryDef.QueryFieldTransformer.ReplaceTransformer;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.LabViewDef")
public class LabViewDef extends ViewDef {
	
	public LabViewDef() throws IOException {
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Lab Results"));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.SortParam("observed", false));
		declareParam(new ViewParam.DateRangeParam("range", "-1y"));
		declareParam(new ViewParam.ENUMParam("filter.catCodes", "", "urn:va:lab-category:CH", "urn:va:lab-category:MI", "urn:va:lab-category:CY", "urn:va:lab-category:EM", "urn:va:lab-category:SP", "urn:va:lab-category:AU")
			.addMeta("multiple", true).addMeta("title", "Categories")
			.addMeta("displayVals", new String[] {"CH", "MI", "CY", "EM", "SP", "AU"}));
		declareParam(new ViewParam.AsArrayListParam("filter.typeCodes"));
		declareParam(new ViewParam.AsArrayListParam("filter.typeNames"));
		declareParam(new ViewParam.AsArrayListParam("filter.catCodes"));
		
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "rowactions,display,observed,value,ref_range,Facility";
		String requireCols = "display,observed,value,";
		String hideCols = "uid,icn,pid,result,units,low,high,selfLink,trendLink";
		String sortCols = "specimen,resulted,observed";
		String groupCols = "specimen,Facility";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		// define the query
		QueryDef qry = new QueryDef();
		qry.namedIndexRange("lab-time", ":range.startHL7", ":range.endHL7");
		qry.fields().include("uid","summary", "result", "statusName", "specimen", "typeCode", "typeName", "summary");
		qry.fields().include("resulted","observed", "units", "facilityName", "high", "low");
		qry.fields().include("categoryName", "interpretationCode");
		qry.fields().alias("typeName", "display").alias("facilityName", "Facility").alias("statusName", "Status");
		qry.fields().transform("summary", new HTMLEscapeTransformer());
		qry.fields().transform("interpretationCode", new ReplaceTransformer("urn:hl7:observation-interpretation:", ""));
		qry.addCriteria(server("categoryCode").in("?:filter.catCodes"));
		qry.addCriteria(server("typeCode").in("?:filter.typeCodes"));
		qry.addCriteria(server("typeNames").in("?:filter.typeNames"));
		
		// create the query and columns
		Query q1 = new JDSQuery("uid", qry);
		addColumns(q1, "uid", "icn", "pid", "display", "result", "units", "low", "high", "Facility", "Status", "categoryName", "specimen");
		getColumn("display").setMetaData("text", "Test").setMetaData("flex", 1);
		addQuery(q1);
		
		addColumn(new TemplateColDef("value", "<span title='{summary}'>{result} {units}</span> <em style=\"color: red; font-weight: bold;\">{interpretationCode}</em>"));
		getColumn("value").setMetaData("text", "Results");
		getColumn("value").setMetaData("width", 90);

		addColumn(new TemplateColDef("ref_range", "{low}-{high}"));
		getColumn("ref_range").setMetaData("text", "Range");
		getColumn("ref_range").setMetaData("width", 65);
		
		getColumn("Status").setMetaData("width", 75);
		getColumn("categoryName").setMetaData("text", "Cat").setMetaData("width", 25);
		
		getColumn("specimen").setMetaData("text", "Specimen");
		getColumn("specimen").setMetaData("width", 65);
		
		addColumn(new HL7DTMColDef(q1, "observed")).setMetaData("text", "Observed").setMetaData("width", 100);
		addColumn(new HL7DTMColDef(q1, "resulted")).setMetaData("text", "Resulted").setMetaData("width", 100);
		
		addColumn(new DomainClassSelfLinkColDef("selfLink", Result.class));
		addColumn(new ActionColDef("rowactions"));
	}
}
