package org.osehra.cpe.vpr.queryeng;


import org.osehra.cpe.vpr.RosterService
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef
import org.osehra.cpe.vpr.queryeng.ColDef.TemplateColDef
import org.osehra.cpe.vpr.queryeng.Query.SOLRFacetQuery
import org.osehra.cpe.vpr.queryeng.Query.ViewDefQuery
import org.osehra.cpe.vpr.queryeng.query.RosterPatientQuery
import org.osehra.cpe.vpr.viewdef.QueryMapper
import org.osehra.cpe.vpr.viewdef.QueryMapper.FieldPrefixTransformer
import org.osehra.cpe.vpr.viewdef.QueryMapper.NestedViewDefQueryMapper
import org.osehra.cpe.vpr.viewdef.QueryMapper.PerRowAppendMapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'org.osehra.cpe.vpr.queryeng.RosterViewDef')
@Scope("prototype")
@HMPAppInfo(value="org.osehra.cpe.multipatientviewdef", title="Default Roster View")
public class RosterViewDef extends ViewDef {
	@Autowired
	public RosterViewDef(RosterService rosterSvc, IPatientDAO patientDao, AlertViewDef alertvd) {
		
		declareParam(new ViewParam.ViewInfoParam(this, "List Patients", null));
		declareParam(new ViewParam.DateRangeParam("recent", "2010..NOW"));
		declareParam(new ViewParam.SessionParams());
		declareParam("roster.ien","");
		
		// TODO: Add last 4 of SSN column (or add to pt_name)
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = 'pt_name,dob,results,orders,vitals,meds,docs,problems,encounters,factors,alerts';
		String requireCols = "pt_name";
		String hideCols = "total_result,total_order,total_vital,total_medication,total_document,total_problem,total_encounter,total_factor,total_procedure,total_observation,";
		hideCols += "recent_result,recent_order,recent_vital,recent_medication,recent_document,recent_problem,recent_encounter,recent_factor,recent_procedure,recent_observation,";
		hideCols += "givenNames,familyName,name,pid"
		String sortCols = null; // no sorting allowed yet
		String groupCols = null;
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
			
		// primary query is a simple RosterService call executed once
		Query primary = new RosterPatientQuery(rosterSvc, patientDao);
		//addColumns(primary, "pid", "icn", "name", "gender", "dfn", "uid", "age", "ssn", "sensitive", "died");
		addQuery(primary);
		addColumn(new HL7DTMColDef(primary, "updated"));
		addColumn(new HL7DTMColDef(primary, "dob")).setMetaData([text: "DOB", width: 75]);
		addColumn(new TemplateColDef("pt_name", '<span <tpl if=\"!(pid &gt; 0)\">title="Patient not in VPR" class="hmp-pt-not-loaded"</tpl><tpl if="pid &gt; 0">class="hmp-pt-loaded"</tpl>>{name} ({age}yo {gender})</span>').setMetaData([text: 'Patient', width: 200]));
		
		// alerts column
//		addQuery(new NestedViewDefQueryMapper("alerts", alertvd, [pid: '0']));
		
		// then get the total counts for each domain from SOLR
		Query tot = new SOLRFacetQuery("pid", "pid:(#{getParentRowVal('pid')?:0})", "domain");
		addQuery(new PerRowAppendMapper(new FieldPrefixTransformer(tot, "total_")));
		
		// then get the recent counts for each domain from SOLR
		String q2 = "pid:(#{getParentRowVal('pid')?:0}) AND datetime:[#{getParamStr('recent.startHL7')} TO #{getParamStr('recent.endHL7')}]";
		Query recent = new SOLRFacetQuery("pid", q2, "domain");
		addQuery(new PerRowAppendMapper(new FieldPrefixTransformer(recent, "recent_")));
		
		Query q3 = addQuery(new PerRowAppendMapper(new QueryMapper.GSPTemplateTransformer("alerts", "/frame/alertsummary", new ViewDefQuery("alert_data", "org.osehra.cpe.vpr.queryeng.AlertViewDef"))));
		addColumns(q3, "alerts");
		
        // these are the actual columns that are intended to be displayed
        addColumn(new TemplateColDef("results", '<tpl if="total_result &gt; 0">{total_result}</tpl> <tpl if="recent_result &gt; 0">({recent_result})</tpl>').setMetaData([text: 'Results', onclick: 'onptclick', width: 50]));
        addColumn(new TemplateColDef("orders", '<tpl if="total_order &gt; 0">{total_order}</tpl> <tpl if="recent_order &gt; 0">({recent_order})</tpl>').setMetaData([text: 'Orders', width: 50]));
        addColumn(new TemplateColDef("vitals", '<tpl if="total_vital_sign &gt; 0">{total_vital_sign}</tpl> <tpl if="recent_vital_sign &gt; 0">({recent_vital_sign})</tpl>').setMetaData([text: 'Vitals', width: 50]));
        addColumn(new TemplateColDef("meds", '<tpl if="total_medication &gt; 0">{total_medication}</tpl> <tpl if="recent_medication &gt; 0">({recent_medication})</tpl>').setMetaData([text: 'Meds', width: 50]));
        addColumn(new TemplateColDef("docs", '<tpl if="total_document &gt; 0">{total_document}</tpl> <tpl if="recent_document &gt; 0">({recent_document})</tpl>').setMetaData([text: 'Docs', width: 50]));
//        addColumn(new TemplateColDef("problems", '<tpl if="total_problem &gt; 0">{total_problem}</tpl> <tpl if="recent_problem &gt; 0">({recent_problem})</tpl>').setMetaData([text: 'Problems', width: 50]));
		addColumn(new TemplateColDef("problems", '{probvdhtml}').setMetaData([text: 'Problems', width: 50]));
        addColumn(new TemplateColDef("encounters", '<tpl if="total_encounter &gt; 0">{total_encounter}</tpl> <tpl if="recent_encounter &gt; 0">({recent_encounter})</tpl>').setMetaData([text: 'Encounters', width: 50]));
        addColumn(new TemplateColDef("factors", '<tpl if="total_factor &gt; 0">{total_factor}</tpl> <tpl if="recent_factor &gt; 0">({recent_factor})</tpl>').setMetaData([text: 'Factors', width: 50]));
		addColumn(new TemplateColDef("procedures", '<tpl if="total_procedure &gt; 0">{total_procedure}</tpl> <tpl if="recent_procedure &gt; 0">({recent_procedure})</tpl>').setMetaData([text: 'Procedures', width: 50]));
		addColumn(new TemplateColDef("observations", '<tpl if="total_observation &gt; 0">{total_observation}</tpl> <tpl if="recent_observation &gt; 0">({recent_observation})</tpl>').setMetaData([text: 'Observations', width: 50]));
	}
}
