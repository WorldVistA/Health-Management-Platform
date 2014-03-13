package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.dynamic.columns.DiabetesAnalysisVDDCD")
@Scope("prototype")
public class DiabetesAnalysisVDDCD extends ViewDefDefColDef {

	public DiabetesAnalysisVDDCD() {
		super(null);
	}
	
	public DiabetesAnalysisVDDCD(Map<String, Object> vals) {
		super(vals);
	}
	
	@Autowired
	public DiabetesAnalysisVDDCD(Environment env) {
		super();
		fieldName = "Diabetes Analysis";
	}
	
	@Override
	public String getType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getViewdefCode() {
		return "org.osehra.cpe.vpr.queryeng.LabViewDef";
	}

	@Override
	public String getFieldDataIndex() {
		return "/rollup/diabetes_analysis";
	}

	@Override
	public String getSummaryType() {
		return ViewDefDefColDef.GSP;
	}

	@Override
	public String getName() {
		return "Diabetes Analysis";
	}

	@Override
	public String getDescription() {
		return "TODO: This column is still in alpha.";
	}
}
