package org.osehra.cpe.vpr.queryeng;


import org.osehra.cpe.vpr.pom.jds.JdsOperations;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.WorkingDiagnosisViewDef")
@Scope("prototype")
public class WorkingDiagnosisViewDef extends ViewDef {

    /**
     * @param jdsTemplate
     * @param environment
     */
    @Autowired
    public WorkingDiagnosisViewDef(JdsOperations jdsTemplate, Environment environment) {
        // declare the view parameters
        declareParam(new ViewParam.ViewInfoParam(this, "Working Diagnosis"));
        declareParam(new ViewParam.PatientIDParam());
		
		String displayCols = "diagnosis";
		String requireCols = "diagnosis";
		String hideCols = "uid";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
        // Relevant Immunizations
        Query q1 = new JDSQuery("uid", "/vpr/{pid}/index/diagnosis");
        addColumns(q1, "uid", "diagnosis");

        getColumn("diagnosis").setMetaData("text", "Diagnosis");
		
        addQuery(q1);
    }
}

