package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.Query.QueryMode;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * First crack at a driver for recent events view.
 * 
 * TODO: This really needs to be based on events, (new order placed for x), etc.
 * TODO: the whole dateRange thing is very sketchy.
 * TODO: How to track/record the "last viewed" date.  What is the last viewed date?
 * TODO: What about future orders/meds?  Should probably be more along the lines of >= startDate (skip the end date)
 * TODO: The filter.kind needs more thought too.
 * TODO: Need to apply sorting to SOLR.
 * TODO: DateTime column needs to have dynamic precision.
 * 
 * @author brian
 */
@Component(value="org.osehra.cpe.vpr.queryeng.RecentViewDef")
@Scope("prototype")
public class RecentViewDef extends ViewDef {
    @Autowired
	public RecentViewDef(SolrServer solrServer) throws Exception {
		
		// declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this));
		declareParam(new ViewParam.SortParam("datetime", false, ""));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.ENUMParam("filter.kind", "", "order", "problem", "etc."));
		declareParam(new ViewParam.QuickFilterParam("qfilter.kinds", "", "Order","Laboratory","Vital Sign","Medication, Inpatient","Infusion","Medication, Outpatient","Visit","Microbiology","Progress Note","Imaging","Consult","Problem","Unknown","Admission","Procedure","Consult Report","Allergy / Adverse Reaction","Discharge Summary","Immunization","Appointment","Medication, Non-VA","Clinical Observation","Health Factor"));
		declareParam(new ViewParam.ORedListParam("qfilter.kinds", "kind"));
		declareParam(new ViewParam.DateRangeParam("datetime", "One Year"));
		
		// list of fields that are not displayable as columns and a default user column set/order
		
		String displayCols = "datetime,domain,kind,summary";
		String requireCols = "datetime,domain,kind,summary";
		String hideCols = "uid";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));

        String q = "pid:(#{getParamStr('pid')}) AND datetime:[#{getParamStr('datetime.startHL7')} TO #{getParamStr('datetime.endHL7')}] " +
        		"#{getParamStr('qfilter.kinds')}";
        Query primary = new Query.SOLRTextQuery("uid", solrServer, q, QueryMode.ONCE);
		declareParam(new ViewParam.ColumnValuesArrayParam("qfilter.kind", primary, "kind"));
		declareParam(new ViewParam.ColumnValuesListParam("kindORStr", primary, "kind", " OR ", ""));
        addColumns(primary, "uid", "domain", "kind", "summary");
        getColumn("domain").setMetaData("text","Domain");
        getColumn("kind").setMetaData("text","Kind");
        
		addColumn(new HL7DTMColDef(primary, "datetime")).setMetaData("text", "Date/Time");
		getColumn("summary").setMetaData("text", "Summary");
		getColumn("summary").setMetaData("flex", 1);
		addQuery(primary);
	}
}
