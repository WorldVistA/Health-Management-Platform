package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.feed.atom.Link;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.frameeng.CallEvent;
import org.osehra.cpe.vpr.frameeng.FrameJob;
import org.osehra.cpe.vpr.frameeng.FrameRegistry;
import org.osehra.cpe.vpr.frameeng.FrameRunner;
import org.osehra.cpe.vpr.frameeng.IFrame;
import org.osehra.cpe.vpr.frameeng.IFrameEvent;
import org.osehra.cpe.vpr.frameeng.IFrameEvent.InvokeEvent;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.viewdef.QueryMapper.PerRowAppendMapper;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.RenderTask.RowRenderSubTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.FileCopyUtils;


/**
 * Queries extend the basic Table data type to define how, when, where to execute a query.
 * <p/>
 * Queries can be run: once, once per row, once per cell, or never (controlled via QueryMode param).
 * Queries source could be: SQL, DomainObject.list(), SOLR index, Web Service, RPC, static data set, etc.
 * Queries map whatever the source is into a tabular form via setCell() addRow(), etc.
 * Queries must have a primary key column declared (typically in constructor) and all data sources must be able to identify rows by their PK value.
 * Queries should use the ViewDef.getParam(...) to determine the exact query to run.
 * Queries are generally lazily executed, since its possible the user doesn't even want to render some columns.
 * Queries have an optional template engine capability (see evalQueryString()). This makes dynamic queries much easier. Some Queries might have their own template capability (ie: JDBCQuery)
 * Queries will typically consist of simple data types (int, string, etc.), but could contain complex results (like domain objects) for more sophisticated rendering.
 * Queries have a fullSize() in addition to size().  The optional fullSize() value would be the number of rows available (if known) vs the number of rows rendered (size()).
 * <p/>
 * TODO: Its peculiar how the queries really only have 1 reference in ViewDef, are they really necessary to register?  Maybe just the primary one?
 * TODO: query needs a way to limit the number of rows it parses to row.limit, but also declares the total rows.
 * TODO: Need to have an exception handling mechanism and declare an exception in the exec method.
 * TODO: There might be some sort of filter mechanism that can be added here.
 * -- Case: something that can dynamically modify SQL/HQL query (add a where clause etc.)
 * -- Case: Something that might do terminology filtering in java.
 * TODO: DynamicQueryParam could use some additional context vals (ViewDef, etc.)
 */
public abstract class Query extends Table {
    private static final Logger log = LoggerFactory.getLogger(Query.class);

    private String pk;
    private String qrystr;
    private ExpressionParser parser;
    private Expression expr;
    
    // these are all deprecated
    private String fk;
    private QueryMode qm;
    protected int fullsize = -1;
    private boolean firstExec = false;
    private String lastExecPK;    

    public enum QueryMode {
        ONCE, // exec is invoked once and only once. Primary queries must use this mode.
        PER_ROW, // exec is invoked once per primary key value.  The results may be used to render multiple cells.
        PER_CELL, // exec is invoked once per each cell rendered.
        NEVER // exec is never invoked.  Useful for static rendering that does not come from a query.
    }

    public Query(String pk, String qrystr) {
        this(pk, qrystr, QueryMode.ONCE);
    }

    @Deprecated
    public Query(String pk, String qrystr, QueryMode qm) {
    	super(pk);
    	this.pk = pk;
        this.qrystr = qrystr;
        this.qm = qm;
    }
    
    public String getPK() {
    	return this.pk;
    }
    
    protected String evalQueryString(RenderTask renderer, String querystr) {
        // only initialize the expression parser the first time
        if (parser == null) {
            parser = new SpelExpressionParser();
        }
        
        if(expr==null) {
            expr = parser.parseExpression(querystr, new TemplateParserContext());
        }

        return (String) expr.getValue(renderer, String.class);
    }

    public abstract void exec(RenderTask task) throws Exception;

    /**
     * Can be overloaded to further process the row before its added.  IE converting data types, filtering, etc.
     *
     * @param row
     * @return
     */
    protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
        return row;
    }

    public String getQueryString() {
        return this.qrystr;
    }

    // deprecated stuff -------------------------------------------------------
    
    /**
     * Will only actually execute the query (via delegation to doExec()) when the query mode designates it should run.
     */
    @Deprecated
    public boolean exec(ViewDefRenderer renderer) {
        long startAt = System.currentTimeMillis();
        try {
            String resultspkval = renderer.resultspkval;
            if (qm == QueryMode.NEVER) {
                return false;
            } else if (qm == QueryMode.ONCE && !firstExec) {
                renderer.calcParams();
                init();
                firstExec = true;
                exec((RenderTask) renderer);
            } else if (qm == QueryMode.PER_ROW && resultspkval != null && !resultspkval.equals(lastExecPK)) {
                renderer.calcParams();
                init();
                log.debug("LOG: Executing query (for PK Value of: {})", resultspkval);
                lastExecPK = resultspkval;
                exec((RenderTask) renderer);
            } else if (qm == QueryMode.PER_CELL) {
                renderer.calcParams();
                init();
                exec((RenderTask) renderer);
            } else {
                return false;
            }
        } catch (Exception ex) {
        	ex.printStackTrace(); // ??? why not print exceptions. They are, after all, exceptional.
            log.error("QUERY ERROR: Type=" + getClass().getSimpleName() + "; Mode=" + qm + "; Time (ms)=" + (System.currentTimeMillis() - startAt) + "; Size=" + size() + "; QueryStr=" + getQueryString());
            throw new RuntimeException("Error processing query", ex);
        }
        log.debug("QUERY LOG: Type=" + getClass().getSimpleName() + "; Mode=" + qm + "; Time (ms)=" + (System.currentTimeMillis() - startAt) + "; Size=" + size() + "; QueryStr=" + getQueryString());
        return true;
    }
    
    
    @Deprecated
    public String getFK() {
        return this.fk;
    }

    @Deprecated
    public void setFK(String fk) {
        this.fk = fk;
    }

    @Deprecated
    public QueryMode getQueryMode() {
        return this.qm;
    }

    @Deprecated
    public int getFullSize() {
        if (fullsize < 0) {
            return size();
        }
        return fullsize;
    }

    /**
     * StaticQuery is in essence the table class.
     */
    public static class StaticQuery extends Query {
        private List<Map<String, Object>> data;
        
		public StaticQuery(String pk) {
            super(pk, null, QueryMode.NEVER);
        }

        public StaticQuery(String pk, List<Map<String, Object>> data) {
            super(pk, null, QueryMode.NEVER);
            this.data = data;
        }

        @Override
        public void exec(RenderTask task) {
        	if (data != null) task.addAll(this.data);
        }
    }

    /**
     * HQL query, currently not intended to return complete domain objects, just a 'SELECT x,y,z FROM domain' type query.
     * <p/>
     * Also, all select clause items must be aliased (ie: SELECT x as x, y as y....)
     * <p/>
     * TODO: If/How can we use the domain object queries (withType, etc.)?
     * TODO: HQL seems to be very picky about passing the correct data type into the query (IE wont convert Integer to Long, etc)
     */
    public static class HQLQuery extends Query {
        public HQLQuery(String pk, String querystr, QueryMode qm) {
            super(pk, querystr, qm);
        }
        
        public HQLQuery(String pk, InputStream sql) throws IOException {
        	super(pk, FileCopyUtils.copyToString(new InputStreamReader(sql)), QueryMode.ONCE);
        }

        @Override
        public void exec(RenderTask renderer){
            Session s = renderer.getResource(SessionFactory.class).getCurrentSession();

            String qs = evalQueryString(renderer, getQueryString());
            org.hibernate.Query q = s.createQuery(qs);
            q.setProperties(renderer.getParams());
            q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

            List<Map> ret = q.list();
            for (Map m : ret) {
            	renderer.add(mapRow(renderer, m));
            }
        }
    }

    public static class SOLRTextQuery extends Query {
        private SolrServer solr;

        public SOLRTextQuery(String pk, SolrServer solrServer, String qrystr, QueryMode qm) {
            super(pk, qrystr);
            this.solr = solrServer;
        }

        @Override
        public void exec(RenderTask renderer) {
            String newQry = evalQueryString(renderer, getQueryString());
            SolrQuery solrParams = new SolrQuery(newQry);
            String sortCol = renderer.getParamStr("sort.col"); 
            String sortDir = renderer.getParamStr("sort.dir");
            if (sortCol != null && sortDir != null) {
            	solrParams.addSortField(sortCol, ORDER.valueOf(sortDir.toLowerCase()));
            }
            try {
                QueryResponse resp = solr.query(solrParams);
                SolrDocumentList docs = resp.getResults();

                for (SolrDocument doc : docs) {
                	renderer.add(mapRow(renderer, doc));
                }

            } catch (Exception ex) {
                // Lazily rethrowing for now.
                throw new RuntimeException(ex);
            }
        }
    }

    public static class SOLRFacetQuery extends Query {
        private String facetField;
		private SolrServer solr;

        @Deprecated
        public SOLRFacetQuery(String pk, SolrServer solr, String solrQry, String facetField, QueryMode qm) {
        	super(pk, solrQry, qm);
        	this.facetField = facetField;
        	this.solr = solr;
        }
        
        public SOLRFacetQuery(String pk, String solrQry, String facetField) {
            super(pk, solrQry, QueryMode.PER_ROW);
            this.facetField = facetField;
        }
        
        @Override
        public void exec(RenderTask renderer) throws SolrServerException {
        	SolrServer solr = (this.solr != null) ? this.solr : renderer.getResource(SolrServer.class);
            String newQry = evalQueryString(renderer, getQueryString());
            SolrQuery solrParams = new SolrQuery(newQry);
            solrParams.setFacet(true);
            solrParams.setRows(0);
            solrParams.setFacetMinCount(1);
            solrParams.addFacetField(this.facetField);
            mapResults(renderer, solr.query(solrParams));
        }
        
        protected void mapResults(RenderTask task, QueryResponse resp) {
            FacetField ff = resp.getFacetFields().get(0);
            if (ff == null || ff.getValues() == null) {
                return;
            }
            
            // Experiment for PER_ROW mappings: Results are columns not rows.
            if (task instanceof RowRenderSubTask) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(getPK(), ((RowRenderSubTask) task).getParentRowVal(getPK()));
                for (Count c : ff.getValues()) {
                    map.put(c.getName(), c.getCount());
                }
                task.add(mapRow(task, map));
            } else {
                for (Count c : ff.getValues()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(this.facetField, c.getName());
                    map.put("count", c.getCount());
                    task.add(mapRow(task, map));
                }
            }
        }
    }
    
	public static class InfobuttonQuery extends Query {
		private String searchContext;
		private String searchCodeSet;
		private String codeField;
		private String textField;

		public InfobuttonQuery(String resultField, String codeField,
				String textField, String searchContext, String searchCodeSet) {
			super(resultField, null);
			this.searchContext = searchContext;
			this.searchCodeSet = searchCodeSet;
			this.textField = textField;
			this.codeField = codeField;
		}

		@Override
		public void exec(RenderTask task) throws Exception {
			if (!(task instanceof RowRenderSubTask)) {
				// infobuttonQuery is intended to be run per row, dispatch in a PerRowAppendQuery
				new PerRowAppendMapper(this).exec(task);
				return;
			}
			
			// render for an individual row
			RowRenderSubTask rowtask = (RowRenderSubTask) task;
			IPatientDAO dao = task.getResource(IPatientDAO.class);
			OpenInfoButtonLinkGenerator gen = task.getResource(OpenInfoButtonLinkGenerator.class);
			
			// get the fields from the current result row, and the patient record
			Object searchText = rowtask.getParentRowVal(this.textField);
			Object searchCode = rowtask.getParentRowVal(this.codeField);
			
			// try to find the PID in the parent row, or params
			Object pid = rowtask.getParentRowVal("pid");
			if (pid == null) {
				pid = task.getParamObj("pid");
			}
			if (pid == null) {
				return; // cant find a PID, skip..
			}
			Patient pat = dao.findByVprPid(pid.toString());
			
			// build the parameters for the link generator
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (pat != null) {
				map.put("age", pat.getAge());
				map.put("gender", pat.getGenderCode());
			}
			map.put("context", searchContext);
			map.put("searchCodeSet", searchCodeSet);
			map.put("searchText", searchText);
			map.put("searchCode", searchCode);
			
			// TODO: hacky fix the search code+gender code to remove the URN:XXX: values so infobuttons work
			Object code = map.get("searchCode");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("searchCode", code.toString().split(":")[2]);
			}
			code = map.get("gender");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("gender", code.toString().split(":")[3]);
			}
			
			// run the link generator, if it returns a value, add it to the results.
			Link link = gen.generateLinkFromMap(map);
			if (link != null) {
				task.add(Table.buildRow(getPK(), link.getHref()));
			}
		}
	}
	
	public static class FrameQuery extends Query {
		private Class<? extends IPatientObject> clazz;

		public FrameQuery(String pk, String entryPoint, Class<? extends IPatientObject> clazz) {
			super(pk, entryPoint);
			this.clazz = clazz;
		}

		@Override
		public void exec(RenderTask task) throws Exception {
			if (task instanceof RowRenderSubTask) {
				IGenericPatientObjectDAO dao = task.getResource(IGenericPatientObjectDAO.class);
				FrameRunner runner = task.getResource(FrameRunner.class);
				String uid = ((RowRenderSubTask) task).getParentRowKey();
				IPatientObject obj = (this.clazz != null) ? dao.findByUID(this.clazz, uid) : null;
				IFrameEvent<?> evt = new InvokeEvent<IPatientObject>(getQueryString(), obj);
				FrameJob docket = runner.exec(evt);
				task.appendVal(uid, "actions", docket.getActions());
			} else {
				throw new IllegalArgumentException("FrameExecMapper must be nested inside a PerRow**Mapper");
			}
		}
	}
	
	public static class ViewDefQuery extends Query {
		private Map<String, Object> params;
		private String field;

		public ViewDefQuery(String field, String viewid) {
			this(null, field, viewid, null);
		}
		
		public ViewDefQuery(String pk, String field, String viewid, Map<String, Object> params) {
			super((pk == null) ? "viewid" : pk, viewid);
			this.params = params;
			this.field = field;
		}
		
		@Override
		public void exec(RenderTask task) throws Exception {
			ViewDefRenderer2 vdr = task.getResource(ViewDefRenderer2.class);
			FrameRegistry registry = task.getResource(FrameRegistry.class);
			IFrame frame = registry.findByID(getQueryString());

			Map<String, Object> row = null;
			if (task instanceof RowRenderSubTask) {
				row = ((RowRenderSubTask) task).getParentRow();
				if (frame == null) {
					// if query string is a field name then look up the viewID from the field
					frame = registry.findByID((String) row.get(getQueryString()));
				}
			}
			
			if (frame instanceof ViewDef) {
				// prep params
				Map<String, Object> newparams = new HashMap<String, Object>();
				if (params != null) newparams.putAll(params);
				if (row != null) newparams.putAll(row);
				if (!newparams.containsKey("pid")) {
					newparams.put("pid", 0);
				}
				RenderTask task2 = vdr.render((ViewDef) frame, newparams);
				if (task2.size() > 0) {
					if (this.field == null) {
						task.addAll(task2);
					} else {
						task.appendVal(getQueryString(), this.field, task2);
					}
				}
			}
		}
	}
	
	/**
	 * This query reads JSON documents from a file directory/filter
	 */
	public static class JSONFileQuery extends Query {
		private File[] files;
		public static FileFilter JSON_FILES = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".json");
			}
		};
		
		public JSONFileQuery(String pk, File[] files) {
			super(pk, null);
			this.files = files;
		}

		@Override
		public void exec(RenderTask task) throws Exception {
			for (File file : files) {
				Map<String, Object> row = POMUtils.parseJSONtoMap(new FileInputStream(file));
				if (row != null && row.size() > 0) {
					task.add(this.mapRow(task, row));
				}
			}
		}
	}
	
}
