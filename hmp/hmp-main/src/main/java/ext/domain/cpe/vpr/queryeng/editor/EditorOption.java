package org.osehra.cpe.vpr.queryeng.editor;

import java.util.Map;

public class EditorOption {
	String fieldName;
	String dataType;
	Map<Object, Object> submitOpts = null;
	
	public Map<Object, Object> getSubmitOpts() {
		return submitOpts;
	}
	public void setSubmitOpts(Map<Object, Object> submitOpts) {
		this.submitOpts = submitOpts;
	}
	public EditorOption() {
		
	}
	public EditorOption(String fn, String dt) {
		fieldName = fn;
		dataType = dt;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
