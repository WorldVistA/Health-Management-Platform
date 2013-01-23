package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.vpr.pom.PatientEvent;
import org.osehra.cpe.vpr.queryeng.Query;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;

import java.util.Map;

public interface IFrameExecContext {
	
	/*
	// view specific stuff:
	public abstract ViewDef getViewDef();
	public abstract Query getQuery();
	public abstract ViewDefRenderer2 getRenderer();

	// frame specific stuff
	public abstract PatientEvent getEvent();
	public abstract Frame getFrame();
	public abstract IFrameExecContext getParentContext();
	 */

	// these set the context params
	public abstract void setParam(String key, int val);
	public abstract void setParam(String key, Object val);
	public abstract void setParams(Map<String, Object> params);
	public abstract void setParams(String prefix, Map<String, Object> params);
	public abstract Map<String, Object> getParams();
	public abstract <T> T getParam(Class<T> clazz, String key);
	public abstract Object getParamObj(String key);
	public abstract String getParamStr(String key);
	public abstract int getParamInt(String key);
	
	// task mgmt
	public RenderTask addSubTask(IFrameExecContext task);

}
