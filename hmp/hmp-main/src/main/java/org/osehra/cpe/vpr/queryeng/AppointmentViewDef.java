package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.AppointmentViewDef")
@Scope("prototype")
public class AppointmentViewDef extends ViewDef {

    @Autowired
	public AppointmentViewDef(Environment environ) throws Exception {
		
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Appointments"));
		declareParam(new ViewParam.SortParam("dateTime", false));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.DateRangeParam("range", "+3w"));
		
		// list of fields that are not displayable as columns and a default user column set/order
		
		String displayCols = "dateTime,kind,facilityName,typeName,categoryName,stopCodeName,appointmentStatus,locationName,checkIn,checkOut,comments";
		String requireCols = "dateTime,typeName,kind";
		String hideCols = "uid";
		String sortCols = "dateTime,kind,facilityName,typeName,categoryName,stopCodeName,appointmentStatus,locationName,checkIn,checkOut,comments";
		String groupCols = "kind,facilityName,typeName,categoryName,stopCodeName,appointmentStatus,locationName";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		QueryDef qry = new QueryDef();
		Query q1 = new JDSQuery("uid", qry, "/vpr/{pid}/index/appointment?order=#{getParamStr('sort.ORDER_BY')}" +		
	        	"#{getParamStr('range.startHL7')!=null?'&filter=between(dateTime,\"'+getParamStr('range.startHL7')+'\",\"'+getParamStr('range.endHL7')+'\")':''}");
		addColumns(q1, "uid", "icn", "pid", "display", "typeCode", "result", "units", "low", "high", "interpretation", "accessionUid", "summary", "Facility");
		getColumn("display").setMetaData("text", "Test").setMetaData("flex", 1);
		addQuery(q1);
		
		addColumns(q1, "dateTime","kind","facilityName","typeName","categoryName","stopCodeName","appointmentStatus","locationName","checkIn","checkOut","comments");
		getColumn("dateTime").setMetaData("text","Appt. Time");
		getColumn("kind").setMetaData("text","Kind");
		getColumn("facilityName").setMetaData("text","Facility");
		getColumn("typeName").setMetaData("text","Type");
		getColumn("categoryName").setMetaData("text","Category");
		getColumn("stopCodeName").setMetaData("text","Stop Code");
		getColumn("appointmentStatus").setMetaData("text","Status");
		getColumn("locationName").setMetaData("text","Location");
		getColumn("checkIn").setMetaData("text","Check-in Time");
		getColumn("checkOut").setMetaData("text","Check-out Time");
		getColumn("comments").setMetaData("text","Reason for Visit");
	}
}
