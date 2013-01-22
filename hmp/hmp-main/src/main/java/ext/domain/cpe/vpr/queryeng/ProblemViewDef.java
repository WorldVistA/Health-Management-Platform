package EXT.DOMAIN.cpe.vpr.queryeng;

import static EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria.where;
import EXT.DOMAIN.cpe.vpr.Problem;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrameTrigger.PatientEventTrigger;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.ActionColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.TemplateColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.JDSQuery;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.viewdef.QueryMapper;
import EXT.DOMAIN.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This demonstrates a concrete implementation of ViewDef that imitates the data on a basic lab tab.
 * <p/>
 * It includes some filter capabilities (all, abnormal, critical) and time range selection criteria.
 */
@Component(value="EXT.DOMAIN.cpe.vpr.queryeng.ProblemViewDef")
@Scope("prototype")
public class ProblemViewDef extends ViewDef {
	
	// filter to only fetch active and not removed problems added to the url.
	static final String FILTER_ACTIVE = "ne(removed,true)";
	
	@Autowired 
    public ProblemViewDef(OpenInfoButtonLinkGenerator linkgen, Environment env) throws SQLException, Exception {
		
		// update triggers
		addTrigger(new PatientEventTrigger<Problem>(Problem.class));
		
        // declare the view parameters
		declareParam(new ViewParam.ViewInfoParam(this, "Problems"));
		declareParam(new ViewParam.PatientIDParam());
        declareParam(new ViewParam.AsArrayListParam("filter_icd"));
//        declareParam(new ViewParam.ENUMParam("filter_status", "ACTIVE", "all", "ACTIVE", "INACTIVE", "REMOVED"));
        declareParam(new ViewParam.QuickFilterParam("qfilter_status", "", "ACTIVE", "INACTIVE", "REMOVED"));
        declareParam(new ViewParam.AsArrayListParam("qfilter_status"));
        declareParam(new ViewParam.SortParam("updated", false));
//        declareParam(new ViewParam.SimpleViewParam("filter_active",FILTER_ACTIVE));
        
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "rowactions,summary,statusName,facility";
		String requireCols = "name,value,resulted";
		String hideCols = "uid,pid,icd,selfLink";
		String sortCols = "statusName,onset,updated,location";
		String groupCols = "statusName,location,provider,facility";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));

        
    	QueryDef qry = new QueryDef();
		qry.fields().alias("facilityName", "facility");
    	qry.addCriteria(where("pid").is(":pid"));
    	qry.addCriteria(where("icdCode").in("?:filter_icd"));
        /**
         * With Removed included in statii, we want to put some query magic in place of this ol' stuff.
         */
    	//qry.addCriteria(where("statusName").in("?:qfilter_status").orOperator(where("statusName").is("'REMOVED'").and("removed").is("'true'")));
    	Query q1 = new JDSQuery("uid", qry, "/vpr/{pid}/index/problem?order=#{getParamStr('sort.ORDER_BY')}" +
                /*
                 * Case 1) Nothing selected;  Only one evaluation, check for removed = false.
                 * Case 2) REMOVED selected; Only one evaluation, check for removed = true.
                 * Case 3) All other cases; OR condition on statusName.in(vv) + removed = (qfilter.status.contains('REMOVED'))
                 */
//                "#{getParamStr('qfilter_status').contains('REMOVED')? --- case 3 ----- : just status filter alone
//
//
//
//
// '&filter=eq(removed,true)':" +
//                "'&filter=or{in(statusName,'+getParamStr('qfilter_status')+'),eq(removed,'+getParamStr('qfilter_status').contains('REMOVED')+')}'}");
                "#{getParamStr('qfilter_status').contains('REMOVED')?" +
                    "'&filter=or{in(statusName,'+getParamStr('qfilter_status')+'),eq(removed,'+getParamStr('qfilter_status').contains('REMOVED')+')}'" +
                            ":" +
                    "'&filter=in(statusName,'+getParamStr('qfilter_status')+')eq(removed,false)'}");


        addQuery(q1);
        addColumns(q1, "uid", "pid", "icdCode", "statusName", "summary", "onset", "updated", "provider", "location", "facility","acuity");

        getColumn("statusName").setMetaData("text", "Status");
        getColumn("statusName").setMetaData("width", 55);
        
		getColumn("summary").setMetaData("text", "Problems");
        getColumn("summary").setMetaData("flex", 1);

        addColumn(new HL7DTMColDef(q1, "onset"));
        getColumn("onset").setMetaData("text", "Onset Date");
        getColumn("onset").setMetaData("width", 75);
        
        addColumn(new HL7DTMColDef(q1, "updated"));
        getColumn("updated").setMetaData("text", "Date Entered");
        getColumn("updated").setMetaData("width", 75);

        getColumn("location").setMetaData("text", "Location");
        getColumn("provider").setMetaData("text", "Provider");
        getColumn("facility").setMetaData("text", "Facility");
        getColumn("acuity").setMetaData("text", "Acuity");
        getColumn("icdCode").setMetaData("text", "Icd Code");

        addQuery(new QueryMapper.PerRowAppendMapper(new Query.InfobuttonQuery("infobtnurl", "icdCode", "summary", "PROBLISTREV", "2.16.840.1.113883.6.103")));
		addColumn(new DomainClassSelfLinkColDef("selfLink", Problem.class));
        addColumn(new ActionColDef("rowactions").setMetaData("requestAction", false));

    }
}
