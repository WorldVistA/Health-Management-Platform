package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.vpr.queryeng.editor.EditorOption;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.WorkingDiagnosis")
@Scope("prototype")
public class WorkingDiagnosis extends ViewDefDefColDef {

    public WorkingDiagnosis() {
    	super(null);
    }
    
    public WorkingDiagnosis(Map<String, Object> vals) {
		super(vals);
	}
    
    @Autowired
	public WorkingDiagnosis(Environment env) {
		super();
		fieldName = "Working Diagnosis";
	}
    
	@Override
	public String getType() {
		return ViewDefDefColDef.JSON;
	}

	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.WorkingDiagnosisViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		return "diagnosis";
	}

	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.JSON;
	}

	@Override
	public String getName() {
		return "Working Diagnosis";
	}

	@Override
	public String getDescription() {
		return "Allow entering the current diagnosis, or reason for the admission.";
	}
	
	@Override
	public EditorOption getEditOpt() {
		EditorOption eo = new EditorOption("diagnosis","text");
		Map<Object, Object> submitOpts = new HashMap<Object, Object>();
		submitOpts.put("type","singleCellOrganism");
		submitOpts.put("url", "/diagnosis/submitDiagnosis");
		/*
		 * What to do with the String value I am going to be editing;
		 * Also what to do with Form data later;
		 * Maybe data types get complex;
		 * Maybe a data type tree that lets me know what parms I should expect on submitting to the POST URL.
		 */
		eo.setSubmitOpts(submitOpts);
		return eo;
	}

}
