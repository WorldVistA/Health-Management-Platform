package org.osehra.cpe.vpr.queryeng;

import static org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria.where;
import org.osehra.cpe.vpr.Observation;
import org.osehra.cpe.vpr.VprConstants;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.TemplateColDef;
import org.osehra.cpe.vpr.queryeng.Query.HQLQuery;
import org.osehra.cpe.vpr.queryeng.Query.QueryMode;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.ObservationsViewDef")
@Scope("prototype")
public class ObservationsViewDef extends ViewDef {
	@Autowired 
	public ObservationsViewDef(OpenInfoButtonLinkGenerator linkgen, Environment env) {
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Clinical Observations"));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.SortParam("observed", true));

         // list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "typeName,Value,Flag,observed,BodySite,methodName,Location,Facility";
		String requireCols = "typeName,Value,interpretation";
		String hideCols = "uid,selfLink,methodCode,result,units";
		String sortCols = "typeName,observed";
		String groupCols = "typeName,interpretation,methodName";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));

		Query q1 = null;
		if (env.acceptsProfiles(VprConstants.JSON_DATASTORE_PROFLE, VprConstants.MONGO_DATASTORE_PROFLE)) {
			QueryDef qry = new QueryDef();
			qry.fields().alias("facilityName", "Facility").alias("comment", "Comment").alias("bodySiteName","BodySite")
				.alias("locationName", "Location").alias("qualifierText","Qualifier");
			qry.addCriteria(where("pid").is(":pid"));
			q1 = new JDSQuery("uid",qry,"vpr/{pid}/index/observation?order=#{getParamStr('sort.ORDER_BY')}");
			
		} else {
			String hql = "SELECT o.uid as uid, o.facilityName as Facility, " +
			"o.typeCode as typeCode, o.typeName as typeName, " +
			"o.result as result, o.units as units, o.interpretation as interpretation, o.observed as observed, " +
			"o.resulted as resulted, o.resultStatus as resultStatus, o.methodCode as methodCode, o.methodName as methodName, " +
			"o.bodySiteCode as bodySiteCode, o.bodySiteName as BodySite, o.location as Location, o.comment as Comment, " +
			"o.vaStatus as Status, o.qualifierText as Qualifier " +
			"FROM observation o WHERE o.pid = :pid "+
			"ORDER BY #{getParamStr('sort.ORDER_BY')}";
			
		    q1 = new HQLQuery("uid", hql, QueryMode.ONCE);
		}
		
		addColumns(q1, "uid", "Facility", "typeName", "interpretation", "result", "units", 
			"methodCode", "methodName", "BodySite", "Location", "Comment", "Status", "Qualifier");
		
		addColumn(new TemplateColDef("Value", "{result} {units}")).setMetaData("width", 75);
		getColumn("typeName").setMetaData("text", "Name").setMetaData("flex", 1);
		getColumn("methodName").setMetaData("text", "Method");
		//getColumn("interpretation").setMetaData("text", "Flag").setMetaData("width", 30);
		addColumn(new TemplateColDef("Flag", "<tpl if=\"(interpretation == \'N\')==false\"><div style=\"float: right; color: red; font-weight: bold;\">" +
				"{interpretation}</div></tpl>"))
				.setMetaData("width", 30);

		addColumn(new HL7DTMColDef(q1, "observed")).setMetaData("text", "Observed");
        addColumn(new DomainClassSelfLinkColDef("selfLink", Observation.class));

		addQuery(q1);
	}
}

