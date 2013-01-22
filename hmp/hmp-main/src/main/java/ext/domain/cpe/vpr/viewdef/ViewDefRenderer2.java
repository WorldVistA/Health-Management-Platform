package EXT.DOMAIN.cpe.vpr.viewdef;

import EXT.DOMAIN.cpe.datetime.jackson.HealthTimeModule;
import EXT.DOMAIN.cpe.vpr.frameeng.CallEvent;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.ViewRenderAction;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry.FrameStats;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.DeferredViewDefDefColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewParam;
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask.RenderJob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.perf4j.StopWatch;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Working on a next-gen renderer and ViewDef refactoring, new Renderer responsibilities
 * 
 * - Global resource store (getResource()/setResource())
 * - Orchestrate the rendering process, but more is delegated now
 * - Validation before execution
 * - Logging + statistics
 * - Metadata generation (Mostly via JSONRenderer sublass)
 * - HTML/JSON/XML conversion
 * 
 * Major API differences:
 * - QueryMode gone (replaced with QueryMapper classes)
 * - Query definition has been expanded, QueryMappers and QueryTransformers are queries as well.
 * - ColDefs are purely metadata at this point, logic moved to Query or Filter
 * - Before: render each query individually, join the results together for return.
 * - After: Primary query is the return query, other queries append to it.
 * - Instead of specifying which fields to join addColumns(...), all fields are joined.  Use QueryDef to only select desired fields and use filters to merge/manipulate values
 * (unless custom QueryMapper does differently)
 * - Instead of a shared parameter context, parameters can be set per task (query), but are checked via parent task.
 * - ViewDefRenderer has been broken down and replaced by RenderTask/RenderContext for most use.
 * - Instead of a single rendering context (ViewDefRenderer) there is one context per query (RenderTask)
 * - Query.getFK() is gone.  No longer necessary.
 * 
 * Refactoring Motivations:
 * - ViewsOfViews are going to be very common as we build more boards
 * - ViewDef/ViewDefRender/Query were not reusable/thread-safe, seemed awkward
 * - Query joining was very limited, query mapping was awkward+limited
 * - Needed more dynamic-sism in ViewDefs
 * - ProfileViewDef has pushed the limits a bit....
 * 
 * Renderer Goals (20120809):
 * - Renderer can render multiple viewdefs at the same time (thread safe, reusable)
 * - Renderer can use a thread pool to execute nested queries in parallel (concurrent rendering)
 * - TODO: Renderer can recursively render viewdefs with other nested viewdefs. (recursive)
 * - TODO: Still want to support the ability to not run a query if certain columns are not requested
 * - Break down the rendering into a hierarchical process using RenderTasks (w/ context)
 * - Render parameters/context can be different for each query (RenderTask)
 * - The Renderer should be the main spring-aware part (instead of registering resources)   
 * 
 * ViewDef/Query Goals (20120809):
 * - TODO: Support Dynamic ViewDefs (where queries are created depending on current parameters)
 * - TODO: Get rid of the QueryMode and Query.getFK() stuff, replace with QueryMapper classes
 * - With the QueryMappers, many more join/merge/append operations are possible
 * - Get rid of messy renderer.currentpkval stuff. 
 * - 1 ViewDef instance can be rendered multiple times (don't require prototype beans anymore)
 * - Try to centralize the clinical data queries into the DAO's (DAO's should not have to return domain objects)
 * 
 * ColDef Goals (20120816):
 * - TODO: Get rid of ColDef's, move to client layer (allows more parallel processing, cleaner separation of data + display logic)
 * - Start by de-emphasizing them, use them for metadata, but don't use the getQuery()/getFK()/render() methods.
 * - Move the render() stuff into filters (SpringELColDef, etc.) or Queries (InfobuttonColDef, etc.) 
 * 
 * Other things to investigate/evaluate:
 * - InfobuttonColDef is really more of a query than a coldef?
 * - How might caching work into viewdef rendering?  
 * - How to generate server-side html for some columns/cells (similar to the view controller)
 * - Maybe the list of fields belongs in each query? (Depends on how coldefs go away)
 * - work on better logging and getting JMeter in place
 * - findViewDef() could implement some more advanced search capabilities (beyond spring)
 * - TODO: Currently the timeout is not applied to the primary query....
 * 
 * @author brian
 */
public class ViewDefRenderer2 implements ApplicationContextAware {
	
	private ExecutorService exec;
	private int timeoutMS = 0;
	private int threadCount = 0;
    private ApplicationContext ctx;

	
	public ViewDefRenderer2() {
		// timeout not supported w/o parallel rendering
		this(0, 0);
	}
	
	public ViewDefRenderer2(int timeoutMS, int threadCount) {
		this.timeoutMS = timeoutMS;
		this.threadCount = threadCount;
		if (this.threadCount > 0) {
			exec = Executors.newFixedThreadPool(threadCount);
		}
	}
	
	/**
	 * Number of seperate threads this renderer will use.  May be zero to indicate that all
	 * rendering will be done in the current thread.
	 */
	public int getThreadCount() {
		return this.threadCount;
	}
	
	public long getTimeoutMS() {
		return this.timeoutMS;
	}
	
	protected void validate(RenderJob job) throws ViewDefRenderException {
		// ensure there is at least 1 query
		if (!job.getViewDef().validate()) {
			throw new ViewDefRenderException("Invalid ViewDef state", job.getViewDef());
		}
		
		// TODO: Validate params
	}
	
	// Resource management ----------------------------------------------------
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	public ViewDef findViewDef(String view) {
		// if the view is an exact bean name
		if (ctx != null && ctx.containsBean(view)) {
			return ctx.getBean(view, ViewDef.class);
		}
		return null;
	}
    
    public <T> T getResource(Class<T> clazz) {
        // try to look it up from spring
        try {
        	if (clazz.equals(ViewDefRenderer2.class)) {
        		return (T) this;
        	} else if (ctx != null && clazz.equals(ApplicationContext.class)) {
        		return (T) ctx;
        	} else if (ctx != null) {
        		return ctx.getBean(clazz);
        	}
        } catch (NoSuchBeanDefinitionException ex) {
        	// bean not found
        }
        
        // TODO: throw custom exception? 
        throw new RuntimeException("No resource of type " + clazz.getName() + " found in Spring Context");
    }
	
	// Actual rendering -----------------------------------------------------------------
	
    public RenderTask render(ViewDef def) throws ViewDefRenderException {
    	return render(def, null);
    }
    
	public RenderTask render(ViewDef def, Map<String, Object> params) throws ViewDefRenderException {
		long start = System.currentTimeMillis();
    	long timeoutAt = System.currentTimeMillis() + timeoutMS;
    	StopWatch watch = new StopWatch("view.render." + def.getClass().getSimpleName());
    	
        try {
        	FrameRegistry registry = ctx.getBean(FrameRegistry.class);
        	FrameStats stats = registry.getFrameStats(def);
        	
        	CallEvent<Map<String, Object>> evt = new CallEvent<Map<String, Object>>(def, params);
        	FrameTask task = new FrameTask(ctx, def, evt, def.getTriggers().get(0));
        	task.setParams(params);
        	task.exec();
        	stats.run(System.currentTimeMillis() - start);
        	return task.getAction(ViewRenderAction.class).getResults();
        	
        	/*
        	// first run the primary query in the current thread
            RenderJob primaryctx = new RenderJob(this, def, primary, exec);
            validate(primaryctx);
            primaryctx.setParams(params);
            primaryctx.calcParams();
            primaryctx.call();
            for (Query q : queries) {
            	// the rest of the queries can be run in parallel on other threads
            	if (q != primary) {
            		primaryctx.addSubTask(new RenderTask(primaryctx, q)).start();
            	}
            }
            primaryctx.blockTillDone(timeoutAt);
            
        	// return the results
        	return primaryctx;
        } catch (TimeoutException ex) {
        	String msg = "Timeout while rendering viewdef: " + def.getClass();
        	throw new ViewDefRenderException(msg, ex, def);
        } catch (InterruptedException ex) {
        	String msg = "Interrupted while rendering viewdef: " + def.getClass();
        	throw new ViewDefRenderException(msg, ex, def);
        	 */
		} catch (Exception ex) {
        	String msg = "Exception rendering viewdef: " + def.getClass();
        	throw new ViewDefRenderException(msg, ex, def);
		} finally {
			watch.stop();
		}
	}
	
    /**
     * TODO: Add an option for pretty print?
     */
    public static class JSONViewRenderer2 extends ViewDefRenderer2 {
    	protected static ObjectMapper MAPPER = new ObjectMapper().registerModule(new HealthTimeModule());
    	static {
    		MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    	}
    	

        public JSONViewRenderer2() {
            super();
        }
        
        public JSONViewRenderer2(int timeoutMS, int threadCount) {
            super(timeoutMS, threadCount);
        }
        
        public String renderToString(ViewDef vd, Map<String, Object> params) throws Exception {
        	return renderToJSON(vd, params).toString();
        }
        
        public JsonNode renderToJSON(ViewDef vd, Map<String, Object> params) throws Exception {
        	long startAt = System.currentTimeMillis();
        	
        	// run the actuall queries
        	RenderTask tab = render(vd, params);
            
            // pack all results into a JSON object and return

            ObjectNode ret = MAPPER.createObjectNode();

            ret.put("total", tab.size());
            ret.put("data", MAPPER.convertValue(tab, JsonNode.class));
            
            // TOOD: What else needs to be in the metadata?
            ret.put("metaData", renderMetaData(tab));
            ret.put("queryMS", System.currentTimeMillis()-startAt);
            
            return ret;
        }
        
        public JsonNode renderMetaData(RenderTask task) {
            ObjectNode ret = MAPPER.createObjectNode();
            
            // to enable sorting and grouping, a SortParam must be declared
            // and 1 or more sortable/groupable columns must be declared in ColumnsParam.
            Set<ViewParam> sort = task.getViewDef().getParamDefs(ViewParam.SortParam.class);
            String sortCols = task.getParamStr("col.sortable");
            String groupCols = task.getParamStr("col.groupable");
            
            if (sort.size() > 0 && sortCols != null && sortCols.length() > 0) {
                ret.put("sortable", true);
            } else {
                ret.put("sortable", false);
            }
            if (sort.size() > 0 && groupCols != null && groupCols.length() > 0) {
                ret.put("groupable", true);
            } else {
                ret.put("groupable", false);
            }
            
            ArrayNode params = MAPPER.createArrayNode();
            ret.put("defaults", MAPPER.convertValue(task.getParams(), JsonNode.class));
            renderFieldAndColumnData(ret, task);
            for (ViewParam p : task.getViewDef().getParamDefs()) {
                params.add(MAPPER.convertValue(p.getMetaData(task), JsonNode.class));
            }
            ret.put("params", params);
            return ret;
        }

        private void renderFieldAndColumnData(ObjectNode json, RenderTask task) {
            ViewDef def = task.getViewDef();
            Set<String> fielddata = new HashSet<String>();
            HashMap<String, Map<String,Object>> coldata = new HashMap<String, Map<String,Object>>();

            String suppressList = task.getParamStr("col.suppress");
            String userDisplayList = task.getParamStr("col.display");
            String sortList = task.getParamStr("col.sortable");
            String groupList = task.getParamStr("col.groupable");
            String requireList = task.getParamStr("col.require");

            fielddata.addAll(task.getColumns());
            fielddata.addAll(StringUtils.commaDelimitedListToSet(requireList));
            fielddata.addAll(StringUtils.commaDelimitedListToSet(suppressList));
            fielddata.addAll(StringUtils.commaDelimitedListToSet(userDisplayList));

            for(ColDef col : def.getColumns()) {
                String key = col.getKey();
                fielddata.add(key);

                // any column in col.suppress gets completely excluded
                if (listContains(suppressList, key)) {
                    continue;
                }

                // get the column metadata (if declared)
                Map<String,Object> metadata = new HashMap<String,Object>();
                metadata.put("text", key);
//                metadata.put("dataIndex", key);
                // TODO: look for a flex value too?
                int width = task.getParamInt("col." + key + ".width");
                if (width > 0) {
                    metadata.put("width", width);
                }

                // any columns listed in col.display (user level preferences) are shown as hidden=true|false
                // TODO: sorely need to stop using CSL and make it a more structured parameter
                metadata.put("hidden", false);
                metadata.put("sortable", false);
                metadata.put("groupable", false);
                metadata.put("hideable", true);
                if (!listContains(userDisplayList, key)) {
                    metadata.put("hidden", true);
                }
                if (listContains(sortList, key)) {
                    metadata.put("sortable", true);
                }
                if (listContains(groupList, key)) {
                    metadata.put("groupable", true);
                }
                if (listContains(requireList, key)) {
                	metadata.put("hideable", false);
                }

                /*
                 * DeferredGSPColDef stuff - everything the UI needs to summon cell data later on.
                 */
                if(col instanceof DeferredViewDefDefColDef) {
                	Map<String, Object> deferredMap = new HashMap<String, Object>();
                	deferredMap.put("keyCol", ((DeferredViewDefDefColDef)col).keyCol);
                	ViewDefDefColDef vdcd = ((DeferredViewDefDefColDef)col).cdef;
                	deferredMap.putAll(vdcd.getData());
                	deferredMap.put("appInfo", ((DeferredViewDefDefColDef)col).cdef.getAppInfo());
                	metadata.put("deferred", deferredMap);
                }


                metadata.putAll(col.getColumnMetaData(def));
                coldata.put(key, metadata);
            }

            // Sort in the col.display order
            ArrayNode columnJson = MAPPER.createArrayNode();
            StringTokenizer st = new StringTokenizer(userDisplayList, ",");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (coldata.containsKey(key)) {
                    columnJson.add(MAPPER.convertValue(coldata.get(key), JsonNode.class));
                    coldata.remove(key);
                }
            }

            // anything still left gets added to the end
            for (String key : coldata.keySet()) {
            	columnJson.add(MAPPER.convertValue(coldata.get(key), JsonNode.class));
            }

            json.put("fields", MAPPER.convertValue(fielddata, ArrayNode.class));
            json.put("columns", columnJson);
        }

        private static boolean listContains(String list, String val) {
            if (list == null) {
                return false;
            }
            
            // TODO: this is a poor mans list parser
            String l = list.trim();
            if (l.equals(val) || l.contains(val + ",") || l.endsWith("," + val) || l.endsWith(", " + val)) {
                return true;
            }
            return false;
        }

    }
    
    
}
