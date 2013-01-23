package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.queryeng.Query.StaticQuery;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ViewDefRenderer is main class that is responsible for rendering ViewDefs by executing
 * queries, collecting and collating their results, and rendering/marshaling it into the desired format.
 * 
 * The default ViewDefRenderer simply collects all query data and returns it it as a StaticQuery object (a simple tabular object form).
 * Other ViewDefRenderer implementations are expected to extend this class and render/marshal the data into other forms
 * like JSON, XML, HTML, etc.
 *
 * ViewDefRenderer also provides a rendering context that is passed into Queries and ColDefs and should eventually permit
 * ViewDef's to be thread safe, in that multiple ViewDefRenderers could be rendering a single ViewDef at once.
 * Some of the things stored in this rendering context might be: SessionFactories/Connections, Effective user params, 
 * rendered results-in-progress, etc. 
 * 
 * ViewDefRenderer provides an opportunity for instrumenting/augmenting the rendering process. Currently the default only provides
 * basic logging and render time logging.  Other implementations could do more like more graceful error handing 
 * and more granular/robust logging.  It could also attempt to execute queries in parallel if desired.
 * 
 * It should be possible to define custom ViewDefRenderer's for specific ViewDefs if custom rendering is required.
 * 
 * It should be possible to have renderer cached server side per user in a session context, which might make faster 'next page' rendering? 
 *
 * TODO: Implement a timeout mechanism that can be passed into the queries to abort if its taking too long.
 * TODO: Implement the timer stats
 * TODO: Implement a more formal linking mechanism (maybe enhance addQuery(srcq, linktoq, linktoqfield, querymode/mergestrategy)) 
 * TODO: Accessing rendered results in progress is a messy: Ex: String pid = renderer.results.getCell(renderer.resultspkval, "vprid");
 */
@Deprecated
public class ViewDefRenderer extends RenderTask implements ApplicationContextAware {
    protected Logger log = LoggerFactory.getLogger(ViewDefRenderer.class);

    // TODO:Make these private.
    private ViewDef view;
    public Query primary;
    public Query results;
    public String pkname;
    public String resultspkval;
    public String querypkval;
    public ColDef curcol;
    protected long renderStartAtMS;
    protected long renderEndAtMS;
    protected long renderTimeMS;
    protected int queryExecCount;
    
    private String mimeType;
    
    // trying dynamic resources registration (JDBC, HQL, SOLR, RPC, etc.)
    protected HashSet<Object> resources = new HashSet<Object>();
    private ApplicationContext ctx;
    
    public ViewDefRenderer(ViewDef vd) {
        this(vd, "text");
    }
    
    public ViewDefRenderer(ViewDef vd, String mimeType) {
    	super(null, vd, null);
        assert vd != null;
        this.view = vd;
        this.mimeType = mimeType;
        this.setParams(vd.getParamDefaultVals());
    }
    
    public ViewDef getView() {
        return this.view;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    

    // Resource management/registration functions ---------------------------------------------
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx; 
	}
    
    public void addResource(Object obj) {
        if (obj != null) resources.add(obj);
        if (obj instanceof ApplicationContext) {
        	ctx = (ApplicationContext) obj;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResource(Class<T> clazz) {
        for (Object o : resources) {
            if (clazz.isAssignableFrom(o.getClass())) {
                return (T) o;
            }
        }
        
        // try to look it up from spring if available
        try {
        	if (ctx != null) {
        		return ctx.getBean(clazz);
        	}
        } catch (NoSuchBeanDefinitionException ex) {
        	// bean not found
        }
        
        // TODO: throw custom exception? 
        throw new RuntimeException("No resource of type " + clazz.getName() + " registered in RenderContext");
    }
    
    /**
     * TODO: Should return meaningful errors/exceptions not just true/false.
     * 
     * @return
     */
    protected boolean validate() {
        // validate
        if (!view.validate()) {
            return false;
        }
        
        // validate all the params
        for (ViewParam p : view.getParamDefs()) {
            if (!p.validate(this)) {
                return false;
            }
        }
        
        return true;
    }
    
    public void addResult(String key, Object val) {
        results.setCell(resultspkval, key, val);
    }
    
    protected boolean addRow(Map<String,Object> row, int idx) {
    	return this.getQuery().addRow(row, idx); // reroute to the current query
    }
    
	public boolean appendRow(String pkval, Map<String, Object> row, int rowidx) {
		// reroute to the current query
		return this.getQuery().appendRow(pkval, row, rowidx);
	}
    
    public Map<String, Object> getRow(String pkval) {
    	return this.getQuery().getRow(pkval);
    }
    
	public Map<String, Object> getRowIdx(int idx) {
    	return this.getQuery().getRowIdx(idx); // reroute to the current query
	}
    
    public int size() {
    	return this.getQuery().size(); // reroute to the current query
    };
    
    public Object render() {
        return renderToQuery();
    }

    public Query renderToQuery() {
    	this.validate();
    	
        ViewDef def = this.view;
        int start = getParamInt("row.start");
        int count = getParamInt("row.count");
        
        // setup the render context (primary query, viewdef, results-in-progress, etc)
        renderStartAtMS = System.currentTimeMillis();
        queryExecCount = 0;
        this.view = def;
        this.primary = def.getPrimaryQuery();
        this.pkname = primary.getPK();
        this.results = new StaticQuery(pkname);
        this.querypkval = null;
        this.resultspkval = null;
        this.curcol = null;
        
        // execute the primary query, record its size
        this.q = primary;
        if (primary.exec(this)) {
            queryExecCount++;
        }
        this.results.fullsize = primary.size();

        // loop through all the rows of the primary query (up to our page size limit)
        for (int i=start; i < primary.size() && results.size() < count; i++) {
            
            // set the resultspkval in the render context for each row
            Object tmp = primary.getCellIdx(i, pkname);
            if (tmp == null) {
                continue;
            }
            resultspkval = tmp.toString();
            
            // loop through each displayable column
            for(ColDef col : def.getColumns()) {
                // TODO: (RE)IMPLEMENT THE COLUMN DISPLAY FILTER HERE
                // TODO: Need to figure out how to link template-ish columns to their actuall data columns first.
                // get this columns query and foreign key so we can match it up with the primary query
                String fk = null;
                querypkval = resultspkval;
                Query q = col.getQuery();
                if (q != null) {
                    fk = q.getFK();
                }
                if (fk != null) {
                    Object obj = primary.getCellIdx(i, fk);
                    if (obj != null) {
                        querypkval = obj.toString();
                    }
                }
                
                // if there is a query, call exec to give it an opportunity to run if it wants to (lazy load, etc.)
                // it may not run at all depending on the query mode.
                this.q = q;
                if (q != null && q.exec(this)) {
                    queryExecCount++;
                }
                
                // now actually render the column, it will store the result(s) into this.results
                col.render(this);
            }
            
        }
        renderEndAtMS = System.currentTimeMillis();
        renderTimeMS = renderEndAtMS - renderStartAtMS;
        log.debug("QUERY DEF: Render time (ms): {}; QueryCount: {}", renderTimeMS, queryExecCount);

        return results;
    }
}
