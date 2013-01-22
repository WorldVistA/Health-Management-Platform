package EXT.DOMAIN.cpe.vpr.queryeng.dynamic;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.RosterService;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.TemplateColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.Query;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewParam;
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.RosterPatientQuery;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.dynamic.PatientPanelViewDef")
@Scope("prototype")
public class PatientPanelViewDef extends ViewDef {

    @Autowired
    public PatientPanelViewDef(RosterService rosterSvc, IPatientDAO patientDao, ApplicationContext ctx) {
        this(rosterSvc, patientDao, new TreeSet<ViewDefDefColDef>(), ctx);
    }
	public PatientPanelViewDef(RosterService rosterSvc, IPatientDAO patientDao, TreeSet<ViewDefDefColDef> colDefsByTargetColName, ApplicationContext ctx) {

		StringBuilder colnames = new StringBuilder("pt_img,pt_name,dob");
		int coldex = 0;
		for(ViewDefDefColDef cd: colDefsByTargetColName) {
			colnames.append(",");
			cd.dataIndex = "dyncol-"+(coldex++);
			colnames.append(cd.dataIndex);
		}
		String displayCols = colnames.toString();
		String requireCols = "pt_name";
		String hideCols = "";
		String sortCols = "";
		String groupCols = "";

		declareParam(new ViewParam.ViewInfoParam(this, "List Patients", null));
		declareParam(new ViewParam.DateRangeParam("recent", "2010..NOW"));
		declareParam(new ViewParam.SessionParams());
		declareParam("roster.ien","");
		
		// TODO: Add last 4 of SSN column (or add to pt_name)
		
		// list of fields that are not displayable as columns and a default user column set/order
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
			
		// primary query is a simple RosterService call executed once
		Query primary = new RosterPatientQuery(rosterSvc, patientDao) {
        	
        	protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
        		Map<String, Object> ret = super.mapRow(renderer, row);
        		String rslt = "";
        		if(ret.containsKey("gender") && (!ret.get("gender").equals("")) && ret.containsKey("dob"))
        		{
    				PointInTime pit = new PointInTime(ret.get("dob").toString());
    				int age = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) - pit.getYear();
    				String gender = ret.get("gender").toString();
        			rslt = (gender.endsWith("M"))?"Male":gender.endsWith("F")?"Female":gender;
        			rslt = age + "yo " + rslt;
        		}
        		if(ret.containsKey("ssn") && ret.get("ssn").toString().length()>4) {
        			String ssn = ret.get("ssn").toString();
        			ret.put("ssn4", ssn.substring(ssn.length()-4));
        		}
        		ret.put("pt_dem", rslt);
                if(!ret.containsKey("pid")) {
        			ret.put("pid", 0);// XTemplates fail on missing PID even when trying to compensate for it in the if statement.
        		} 
                ret.put("pt_img", "<img height='32' width='32' src='/vpr/v1/" + ret.get("pid") + "/photo'/>");
                
        		return ret;
        	}
        };

		addQuery(primary);
		addColumns(primary, "pt_img");
		getColumn("pt_img").setMetaData("text","Photo");
		addColumn(new HL7DTMColDef(primary, "updated"));
		addColumn(new HL7DTMColDef(primary, "dob").setMetaData("text","DOB").setMetaData("width",75));
		addColumn(new TemplateColDef("pt_name", 
				"<span " +
					"<tpl if=\"!(pid &gt; 0)\">title=\"Patient not in VPR\" class=\"hmp-pt-not-loaded\"</tpl>" +
					"<tpl if=\"pid &gt; 0\">class=\"hmp-pt-loaded\"</tpl>>" +
						"<table border='0' width='100%'><tr><td colspan='2'>{name}</td></tr><tr><td>{pt_dem}</td><td align='right'>XXX-XX-{ssn4}</td></tr></table>" +
				"</span>").setMetaData("text","Patient").setMetaData("width",200));//[text: 'Patient', width: 200]));
		// Now, add custom columns to the rest o' this ViewDef.
		Iterator<ViewDefDefColDef> citer = colDefsByTargetColName.iterator();
		while(citer.hasNext()) {
			ViewDefDefColDef cdef = citer.next();
			
			if(cdef != null) {
				String view = cdef.getViewdefCode();
				if (ctx.containsBean(view)) {
					ColDef cd = addColumn(new ColDef.DeferredViewDefDefColDef(cdef, "pid", cdef.dataIndex)).setMetaData("text",cdef.getFieldName());
					if(cdef.getEditOpt()!=null) {
						cd.setMetaData("editOpt", cdef.getEditOpt());
					}
				}
			}
		}
	}
}
