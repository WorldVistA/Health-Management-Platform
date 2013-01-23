package org.osehra.cpe.vpr.queryeng.dynamic.columns;

import org.osehra.cpe.auth.AuthController;
import org.osehra.cpe.vpr.HMPApp;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.queryeng.HMPAppInfo;
import org.osehra.cpe.vpr.queryeng.dynamic.ViewDefDef;
import org.osehra.cpe.vpr.queryeng.editor.EditorOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ViewDefDefColDef extends AbstractPOMObject implements HMPApp, Comparable<ViewDefDefColDef> {

    protected static Logger log = LoggerFactory.getLogger(AuthController.class);
    
	public final static String GSP = "GSP";
	public final static String JSON = "JSON";
	public final static String HTML = "HTML";
	
	public final static String SUMMARY_LIST = "LIST";
	public final static String SUMMARY_TOTAL = "TOTAL";
	public final static String SUMMARY_CSV = "CSV";
	public final static String SUMMARY_AVERAGE = "AVG";

	@SuppressWarnings("unchecked")
	public ViewDefDefColDef(Map<String, Object> vals) {
		super(vals);
		if(vals!=null) { // I had thought this would flow thru with the default setData functionality, but alas, it does not.
			if(vals.get("configProperties")!=null) {
				this.setConfigProperties((Map<String, Object>) vals.get("configProperties"));
			}
			if(vals.get("viewdefFilters")!=null) {
				this.setViewdefFilters((Map<String, Object>) vals.get("viewdefFilters"));
			}
		}
	}
	
	public void setData(Map<String, Object> vals) {
		super.setData(vals);
	}
	
	public ViewDefDefColDef() {
		super(null);
	}
	
	String description = "";

	String type = "";
	String viewdefName;
	String viewdefCode;
	Map<String, Object> viewdefFilters = new HashMap<String, Object>();
	Map<String, Object> configProperties = new HashMap<String, Object>();
	EditorOption editOpt = null;

	public EditorOption getEditOpt() {
		return editOpt;
	}

	String summaryType; // Enumeration? Class?

	String fieldName;
	String fieldDataIndex; // Can be different things depending on what Type of column this is.
	@JsonIgnore
	public String dataIndex;
	public Integer sequence = 0;
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public abstract String getType();
	public abstract String getViewdefCode();

	public abstract String getFieldDataIndex();
	/** Override me */
	public abstract String getSummaryType();
	public abstract String getName();

	public abstract String getDescription();

	public Map<String, Object> getViewdefFilters() {
		return viewdefFilters;
	}

	public void setViewdefFilters(Map<String, Object> viewdefFilters) {
		this.viewdefFilters = viewdefFilters;
	}

	public Map<String, Object> getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Map<String, Object> configProperties) {
		this.configProperties = configProperties;
	}

	@JsonIgnore
	public List<Config> getViewdefFilterOptions() {
		return new ArrayList<Config>();
	}
	
	@JsonIgnore
	public List<Config> getConfigOptions() {
		return new ArrayList<Config>();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public Map<String, Object> getAppInfo() {
		HashMap<String, Object> ret = new HashMap<String, Object>();

		// get the annotation, use it to fill in any values not declared in the param
		HMPAppInfo annotation = getClass().getAnnotation(HMPAppInfo.class);
		String name = getName();
		if (name == null && annotation != null) {
			name = annotation.title();
		}
		
		// return the results
		ret.put("type", (annotation != null) ? annotation.value() : "org.osehra.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef");
		ret.put("name", ((name == null || name.equals("")) ? getClass().getName() : name));
		ret.put("code", getClass().getName());
		return ret;
	}
	
	public Map<String, Object> postProcessViewDefRenderedDataBeforeSendingToGSP(Map<String, Object> map, Map<String, Object> configProperties) {
		return map;
	}

	protected boolean poorManFuzzySearch(ArrayList<String> filterz, String type) {
		// Poor man's fuzzy search
		for(String s: filterz) {
			if(type.toLowerCase().contains(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	protected ArrayList<String> configPropertyToArray(String string, Map<String, Object> params) {
		ArrayList<String> filterz=null;
		Object fobj = params.get(string);
		if(fobj!=null && !fobj.toString().trim().equals("")) {
			String[] flist = fobj.toString().split("\\s*,\\s*");
			filterz = new ArrayList<String>(Arrays.asList(flist));
		}
		return filterz;
	}

	@Override
	public int compareTo(ViewDefDefColDef o) {
		int rslt = this.sequence.compareTo(o.sequence);
		if(rslt==0) {rslt = this.fieldName.compareTo(o.fieldName);}
		return rslt;
	}
}
