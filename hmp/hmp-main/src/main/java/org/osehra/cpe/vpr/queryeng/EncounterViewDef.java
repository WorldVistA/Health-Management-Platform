package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.Encounter;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.EncounterViewDef")
@Scope("prototype")
public class EncounterViewDef extends ViewDef {
	public EncounterViewDef() {
		
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Encounters"));
		declareParam(new ViewParam.SortParam("dateTime", false));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.AsArrayListParam("filter.kind"));
		declareParam(new ViewParam.AsArrayListParam("filter.stop_code"));
		declareParam(new ViewParam.DateRangeParam("range", "2000..NOW"));
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "dateTime,arrivalDateTime,dischargeDateTime,kind,typeName,service,specialty,location,stopCodeName,appointmentStatus,reason";
		String requireCols = "dateTime,kind,typeName,dateTime";
		String hideCols = "uid,typeCode,reasonCode,selfLink";
		String sortCols = "dateTime,arrivalDateTime,dischargeDateTime";
		String groupCols = "kind,typeName,location,stopCodeName";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		QueryDef qry = new QueryDef();
		qry.fields().include("kind", "uid", "typeName", "dateTime", "service", "specialty");
		qry.fields().include("stopCodeName", "locationName", "roomBed", "reasonName", "reasonCode");
		qry.fields().alias("locationName", "location").alias("reasonName", "reason").alias("reasonCode", "reasonUid");
		// JDS is missing: typeCode, duration, appointmentStatus, 
		qry.addCriteria(QueryDefCriteria.where("pid").is(":pid"));
		qry.addCriteria(QueryDefCriteria.where("kind").in("?:filter.kind"));
		qry.addCriteria(QueryDefCriteria.where("stopCodeName").in("?:filter.stop_code"));
		Query primary = new JDSQuery("uid", qry, "vpr/{pid}/index/encounter?order=#{getParamStr('sort.ORDER_BY')}" +
			"#{getParamStr('range.startHL7')!=null?'&filter=between(dateTime,\"'+getParamStr('range.startHL7')+'\",\"'+getParamStr('range.endHL7')+'\")':''}");

		addQuery(primary);
		
		addColumns(primary, "uid", "kind", "typeName", "typeCode", "duration", "service", "specialty");
		addColumns(primary, "stopCodeName", "location", "roomBed", "reason", "reasonCode");
		
		addColumn(new HL7DTMColDef(primary, "dateTime")).setMetaData("text", "Date/Time");
		addColumn(new DomainClassSelfLinkColDef("selfLink", Encounter.class));
	}
}
