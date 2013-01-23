package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.Medication;
import org.osehra.cpe.vpr.queryeng.ColDef.ActionColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria;
import org.osehra.cpe.vpr.viewdef.QueryMapper;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.MedsViewDef")
@Scope("prototype")
public class MedsViewDef extends ViewDef {
    @Autowired
    public MedsViewDef(OpenInfoButtonLinkGenerator linkgen, Environment env) throws CompilationFailedException, MalformedURLException, ClassNotFoundException, IOException {
        // declare the view parameters
        declareParam(new ViewParam.ViewInfoParam(this, "Medications"));
        declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.DateRangeParam("range", null));
        declareParam(new ViewParam.AsArrayListParam("filter.typeCodes"));
        declareParam(new ViewParam.ENUMParam("filter_kind", null, "O", "I", "N").addMeta("multiple", true).addMeta("title", "Type filter"));
        declareParam(new ViewParam.AsArrayListParam("filter_kind"));
        declareParam(new ViewParam.ENUMParam("filter_status", null, "ACTIVE", "PENDING", "DISCONTINUED", "EXPIRED").addMeta("multiple", true).addMeta("title", "Status filter"));
        declareParam(new ViewParam.AsArrayListParam("filter_status"));
        declareParam(new ViewParam.AsArrayListParam("filter_class"));
        declareParam(new ViewParam.AsArrayListParam("filter_class_code"));
        declareParam(new ViewParam.QuickFilterParam("qfilter_status", "", "ACTIVE", "PENDING", "DISCONTINUED", "EXPIRED"));
        declareParam(new ViewParam.AsArrayListParam("qfilter_status"));
        declareParam(new ViewParam.SortParam("overallStart", false));
        declareParam(new ViewParam.SortParam("overallStop", false));

        // list of fields that are not displayable as columns and a default user column set/order
        String displayCols = "rowactions,summary,vaStatus,kind,overallStart,overallStop,facility";
        String requireCols = "summary,facility,overallStart,overallStop";
        String hideCols = "uid,pid,selfLink,dosages,vaType";
        String sortCols = "overallStart,kind,overallStop,medStatusName,vaStatus";
        String groupCols = "vaStatus,kind,medStatusName,ingredientName,drugClassName";
        declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));

		QueryDef qry = new QueryDef();
		qry.namedIndexRange("med-time", ":range.startHL7", ":range.endHL7");
		qry.fields().alias("patient", "pid").alias("name", "ingredientName");
		qry.fields().include("uid", "pid", "summary", "overallStart", "overallStop", "facility", "kind", "vaStatus", "medStatusName", "ingredientName", "drugClassName","vaType","dosages");
		qry.addCriteria(QueryDefCriteria.where("pid").is(":pid"));
		/*
		 * The following code is in place just so we can merge two different "in" criterium 
		 * correctly. The current QueryDefCriteria mechanisms can't handle this for the same key value (column)
		 */
		qry.addCriteria(QueryDefCriteria.where("vaStatus").in(new QueryDefCriteria.SpelRef(){
			@Override
			public boolean filterOut(Map<String, Object> params) {
				return false;
			}
			@Override
			public Object evaluateWithParams(Map<String, Object> params) {
				Object qcoll = params.get("qfilter_status");
				Object fcoll = params.get("filter_status");
				Vector<String> rslt = new Vector<String>();
				String[] filter = {"vaStatus","ACTIVE", "PENDING", "DISCONTINUED", "EXPIRED"};
				for(int i = 1; i<filter.length; i++)
				{
					if(qcoll instanceof List && qcoll!=null && ((List<String>)qcoll).size()>0 && !((List<String>)qcoll).contains(filter[i]))
					{
						continue;
					}
					if(fcoll instanceof List && fcoll!=null && ((List<String>)fcoll).size()>0 && !((List<String>)fcoll).contains(filter[i]))
					{
						continue;
					}
					rslt.add(filter[i]);
				}
				return rslt;
			}
		}));
		qry.addCriteria(QueryDefCriteria.where("vaType").in("?:filter_kind"));
		qry.addCriteria(QueryDefCriteria.where("products[].drugClassName").in("?:filter_class"));
		qry.addCriteria(QueryDefCriteria.where("products[].drugClassCode").in("?:filter_class_code"));
		Query q1 = new JDSQuery("uid", qry, "vpr/{pid}/index/medication?order=#{getParamStr('sort.ORDER_BY')}"+
				"#{getParamStr('range.startHL7')!=null?'&filter=between(overallStart,\"'+getParamStr('range.startHL7')+'\",\"'+getParamStr('range.endHL7')+'\")':''}");
		
		addQuery(q1);
		addColumns(q1, "uid", "pid", "summary", "overallStart", "overallStop", "facility", "kind", "vaStatus", "medStatusName", "ingredientName", "drugClassName","vaType","dosages");
		
		getColumn("summary").setMetaData("text", "Description");
		getColumn("summary").setMetaData("minWidth", 200);
		getColumn("summary").setMetaData("flex", 1);
        addColumn(new HL7DTMColDef(q1, "overallStart"));
        getColumn("overallStart").setMetaData("text", "Start Date").setMetaData("width", 75);

        addColumn(new HL7DTMColDef(q1, "overallStop")).setMetaData("detailfield", "infobtnurl");
        getColumn("overallStop").setMetaData("text", "Stop Date").setMetaData("width", 75);

        getColumn("kind").setMetaData("text", "Type");
        getColumn("vaStatus").setMetaData("text", "VA Status");
        getColumn("medStatusName").setMetaData("text", "HITSP Status");
        getColumn("facility").setMetaData("text", "Facility");
        
        addQuery(new QueryMapper.PerRowAppendMapper(new Query.FrameQuery("uid", "viewdefactions", Medication.class)));
		addQuery(new QueryMapper.PerRowAppendMapper(new Query.InfobuttonQuery("infobtnurl", "[TBD]", "ingredientName", "MLREV", "2.16.840.1.113883.6.88")));
		
		
        addColumn(new DomainClassSelfLinkColDef("selfLink", Medication.class)).setMetaData("detailloader", "html");
        addColumn(new ActionColDef("rowactions"));
    }
}

