package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.vpr.pom.PatientEvent;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.util.HashMap;
import java.util.Map;

public class SimpleFrameExecContext implements IFrameExecContext {
	private IFrameExecContext parent;
	private Map<String, Object> params = new HashMap<String, Object>();
	
	public SimpleFrameExecContext(IFrameExecContext parent) {
		this.parent = parent;
	}
	
	// these set the context params
    /* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#setParam(java.lang.String, int)
	 */
    @Override
	public void setParam(String key, int val) {
        setParam(key, "" + val);
    }
    
    /* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#setParam(java.lang.String, java.lang.Object)
	 */
    @Override
	public void setParam(String key, Object val) {
        params.put(key, val);
    }
    
    /* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#setParams(java.util.Map)
	 */
    @Override
	public void setParams(Map<String,Object> params) {
        setParams("", params);
    }
    
    /* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#setParams(java.lang.String, java.util.Map)
	 */
    @Override
	public void setParams(String prefix, Map<String,Object> params) {
    	if (params == null) return;
        for (String key : params.keySet()) {
            Object val = params.get(key);
            if (val == null) {
                continue;
            }
            
            // if the value is a map, traverse it as well and use a dot notation
            // this is how grails params work
            if (val instanceof Map) {
                setParams(prefix + key + ".", (Map<String, Object>) val);
            }
            
            setParam(prefix + key, val);
        }
    }
    
    /* (non-Javadoc)
   	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#getParams()
   	 */
       @Override
   	public Map<String, Object> getParams() {
       	Map<String, Object> ret = new HashMap<String, Object>();
       	if (parent != null) {
       		ret.putAll(parent.getParams());
       	}
       	ret.putAll(params);
           return ret;
       }	
       
       /* (non-Javadoc)
   	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#getParam(java.lang.Class, java.lang.String)
   	 */
       
       @Override
   	@SuppressWarnings("unchecked")
   	public <T> T getParam(Class<T> clazz, String key) {
       	Object ret = getParamObj(key);
       	if (ret == null) {
       		return null;
       	} else if (clazz.isInstance(ret)) {
       		return (T) ret;
       	} else if (clazz.equals(String.class)) {
       		return (T) getParamStr(key);
       	} else if (clazz.equals(Integer.class)) {
       		return (T) (Integer) getParamInt(key);
       	} else {
       		// TODO: Plug in spring conversion?
       		throw new IllegalArgumentException("Unrecognized return type: " + clazz);
       	}
       }
       
       /* (non-Javadoc)
   	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#getParamObj(java.lang.String)
   	 */
       @Override
   	public Object getParamObj(String key) {
       	if (params.containsKey(key)) {
       		// first look for the param in our own context
       		return params.get(key);
       	} else if (parent != null) {
       		// they look for it in the parent context
       		return parent.getParamObj(key);
       	} else {
       		// not found, return null
       		return null;
       	}
       }
       
       /* (non-Javadoc)
   	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#getParamStr(java.lang.String)
   	 */
       @Override
   	public String getParamStr(String key) {
           Object val = getParamObj(key);
           if (val != null) {
               return val.toString();
           }
           return null;
       }
       
       /* (non-Javadoc)
   	 * @see org.osehra.cpe.vpr.viewdef.RenderContext#getParamInt(java.lang.String)
   	 */
       @Override
   	public int getParamInt(String key) {
           Object val = getParamObj(key);
           if (val == null) {
               // null returns -1
               return -1;
           }
           
           // if its a int already, just return it.
           if (val instanceof Integer) {
               return ((Integer) val).intValue();
           }
           
           // otherwise try parsing the string.
           try {
               int ret = Integer.parseInt(val.toString());
               return ret;
           } catch (NumberFormatException ex) {
               // otherwise return -1
               return -1;
           }
       }


	public PatientEvent getEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFrameExecContext getParentContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderTask addSubTask(IFrameExecContext task) {
		// TODO Auto-generated method stub
		return null;
	}

}
