package EXT.DOMAIN.cpe.vpr.viewdef;

import EXT.DOMAIN.cpe.vpr.queryeng.Query;
import EXT.DOMAIN.cpe.vpr.queryeng.Table;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask.RowRenderSubTask;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine;

/**
 * mapper strategies
 * 0) PrimaryQueryMapper: source becomes target row (optional copy==AppendMapper)
 * 1) AppendMapper: append rows to existing results (turns out to be the same as RIGHT OUTER JOIN)
 * 2) MergeQueryMapper: merge two table rows w/ (JOIN, UNION, etc)
 * 3) PerRowAppendMapper: execute query once per source row, append additional columns/fields to row (may only return 1 row)
 * 4) PerRowInvertMapper: execute query once per source row, row to column inversion
 * 5) PerRowSubTableMapper: execute query once per source row, results become nested table (nested ViewDefQuery)
 * 6) DefaultTableMapper: Backwards-compatible, if results are null/empty then PrimaryQueryMapper, else MergeQueryMapper; Honors QueryMode param
 * 
 * TODO: Should the QueryMapper hold the queries it maps instead of registering a mapper onto each query?
 * TODO: How to decide if a query should be run?
 * -- could the mapper evaluate the desired field list and not run if none of the fields exist?
 * -- requires that fields be registered in the query?
 * TODO: Do we need to support complex joins (more than one field)?
 * TODO: How do we declare dependencies in a multi-threaded environment (IE one query must finish before another can begin?)
 * @author brian
 */
public abstract class QueryMapper extends Query {
	protected Query q;
	
	public QueryMapper(Query q) {
		super(q.getPK(), null);
		this.q = q;
	}
	
	protected QueryMapper(Query q, String pk) {
		super(pk, null);
		this.q = q;
	}

	public abstract static class QueryTransformer extends QueryMapper {
		public QueryTransformer(Query q) {
			super(q);
		}
		
		@Override
		public void exec(RenderTask task) throws Exception {
			this.q.exec(task);
			for (String pkval : task.getPKValues()) {
				Map<String, Object> row = new HashMap<String, Object>(task.getRow(pkval));
				row = mapRow(row, task);
				if (row == null) {
					task.remove(task.getRow(pkval));
				} else {
					task.clearRow(pkval);
					task.appendRow(pkval, row);
				}
			}
			
		}
		
		public abstract Map<String, Object> mapRow(Map<String, Object> row, RenderTask task);
	}
	
	public static class FieldPrefixTransformer extends QueryTransformer {
		private String prefix;
		public FieldPrefixTransformer(Query q, String prefix) {
			super(q);
			this.prefix = prefix;
		}

		@Override
		public Map<String, Object> mapRow(Map<String, Object> row, RenderTask task) {
			Iterator<String> itr = row.keySet().iterator();
			Map<String, Object> map = new HashMap<String, Object>();
			while (itr.hasNext()) {
				String key = itr.next();
				map.put(this.prefix + key, row.get(key));
				itr.remove();
			}
			row.putAll(map);
			return row;
		}
	}
	
	public static class FieldAliasTransformer extends QueryTransformer {
		private HashMap<String, String> fieldmap;

		public FieldAliasTransformer(Query q) {
			super(q);
			this.fieldmap = new HashMap<String,String>();
		}
		
		public FieldAliasTransformer alias(String from, String to) {
			this.fieldmap.put(from, to);
			return this;
		}

		@Override
		public Map<String, Object> mapRow(Map<String, Object> row, RenderTask task) {
			for (String key : this.fieldmap.keySet()) {
				if (row.containsKey(key)) {
					row.put(this.fieldmap.get(key), row.get(key));
					row.remove(key);
				}
			}
			return row;
		}

	}
	
	public static class DefaultQueryMapper extends QueryMapper {
		public DefaultQueryMapper(Query q) {
			super(q);
		}

		@Override
		public void exec(RenderTask ctx) throws Exception {
			Query src = this.q;
			ViewDef vd = ctx.getViewDef();
			
			if (src == vd.getPrimaryQuery()) {
				// this is the primary/first query
				src.exec(ctx);
			} else {
				// Using the QueryMode to determine the strategy,
				// TODO: Retire Query mode
				QueryMode qm = src.getQueryMode();
				if (qm == QueryMode.ONCE) {
					new JoinQueryMapper(src).exec(ctx);
				} else if (qm == QueryMode.PER_ROW) {
					new PerRowAppendMapper(src).exec(ctx);
				} else {
					throw new RuntimeException("Unable to handle '" + qm.name() + "' as a default.");
				}
			}
		}
	}
	
	// append mapper is idential to RIGHT OUTER JOIN
	public static class AppendMapper extends JoinQueryMapper {
		public AppendMapper(Query q) {
			super(q, null, true);
		}
	}

	public static class JoinQueryMapper extends QueryMapper {
		private String fkey;
		private boolean rightOuterJoin = false;

		public JoinQueryMapper(Query q) {
			super(q);
		}
		
		public JoinQueryMapper(Query q, String targetField) {
			super(q);
			this.fkey = targetField;
		}
		
		public JoinQueryMapper(Query q, String targetField, boolean rightOuterJoin) {
			super(q);
			this.fkey = targetField;
			this.rightOuterJoin = rightOuterJoin;
		}
		
		@Override
		public void exec(RenderTask ctx) throws Exception {
			Query src = this.q;
			src.exec(ctx);
			
			if (rightOuterJoin) {
				rightOuterJoin(ctx);
			} else {
				leftOuterJoin(ctx);
			}
			
		}
		
		private void leftOuterJoin(RenderTask ctx) {
			// this is LEFT OUTER JOIN
			Table parentResults = ctx.getParentContext();
			this.fkey = (this.fkey == null) ? parentResults.getPK() : this.fkey;
			for (int i=0; i < parentResults.size(); i++) {
				Map<String, Object> row = parentResults.getRowIdx(i);
				Object pkval = row.get(this.fkey);
				if (pkval == null) continue;
				
				// TODO: Error if no pkval? (invalid FK)
				Map<String, Object> mergerow = ctx.getRow(pkval.toString());
				if (mergerow == null) continue;
				parentResults.appendRow((String) row.get(parentResults.getPK()), mergerow);
			}
		}
		
		private void rightOuterJoin(RenderTask ctx) {
			// this is RIGHT OUTER JOIN
			Table parentResults = ctx.getParentContext();
			this.fkey = (this.fkey == null) ? parentResults.getPK() : this.fkey;
			for (int i=0; i < ctx.size(); i++) {
				Object pkval = ctx.getCellIdx(i, this.fkey);
				
				Map<String, Object> row = parentResults.getRow(pkval.toString());
				if (row != null) {
					row.putAll(ctx.getRowIdx(i));
				} else {
					parentResults.add(ctx.getRowIdx(i));
				}
			}
		}
	}

	public static class PerRowAppendMapper extends QueryMapper {
		
		public PerRowAppendMapper(Query q) {
			super(q);
		}
		
		protected void mapRow(RowRenderSubTask ctx) throws Exception {
			if (ctx.size() == 1) {
				Map<String, Object> row = ctx.getRowIdx(0);
				ctx.getParentContext().appendRowIdx(ctx.getRowIdx(), row);
			} else if (ctx.size() > 1) {
				throw new UnsupportedOperationException("PerRowAppendMapper can only handle queries that return 1 row for now.");
			}
		}
		
		@Override
		public void exec(RenderTask ctx) throws Exception {
			// if a row index was specified, just run that row
			if (ctx instanceof RowRenderSubTask) {
				this.q.exec(ctx);
				mapRow((RowRenderSubTask) ctx);
				return;
			}
			
			// otherwise, loop through parent results, create a sub-task to execute for each row
			RenderTask parentctx = ctx.getParentContext();
			for (int i=0; i < parentctx.size(); i++) {
				RenderTask subtask = new RowRenderSubTask(parentctx, this, i);
				ctx.addSubTask(subtask);
				subtask.start();
			}
		}
	}

	public static class PerRowSubTableMapper extends PerRowAppendMapper {
		private String field;

		public PerRowSubTableMapper(String field, Query q) {
			super(q);
			this.field = field;
		}
		
		protected void mapRow(RowRenderSubTask ctx) throws Exception {
			ctx.getParentContext().appendRowIdx(ctx.getRowIdx(), Table.buildRow(this.field, ctx));
		}
	}
	
    
    /**
     * Experiemental: letting you use GSP's as template rendering.  May be helpful in
     * renderering nested viewdefs into simple strings before sending to browser...
     * 
     * @author brian
     */
    public static class GSPTemplateTransformer extends QueryMapper {
    	private static SimpleTemplateEngine eng = new SimpleTemplateEngine();
    	private Template tpl;
		private String field;
		private String view;

		public GSPTemplateTransformer(String field, String view, Query q) {
			super(q, field);
			this.field = field;
			this.view = view;
//			this.tpl = eng.createTemplate(new URL(view));
		}


		@Override
		public void exec(RenderTask task) throws Exception {
			if (tpl == null) {
				GroovyPagesTemplateEngine eng = (GroovyPagesTemplateEngine) task.getResource("groovyPagesTemplateEngine2");
				tpl = eng.createTemplate(this.view);
			}
			
			if (tpl == null) {
				throw new RuntimeException("Unable to resolve view: " + this.view);
			}
			
			// TODO: Run this in a sub render task??
			
			// execute the query
			this.q.exec(task);
			
			// build the inputs into the GSP
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("params", task.getParams());
			map.put("rows", new ArrayList<Map<String, Object>>(task.getRows()));

			// execute the template, store results in the specified field
			StringWriter writer = new StringWriter();
			tpl.make(map).writeTo(writer);
			task.clear();
			task.add(Table.buildRow(this.field, writer.toString().trim()));
		}
    }

    /**
     * Similar to NestedViewDefQueryMapper, but returns a placeholder value/identifier to be returned immediately,
     * then batches up the nested queries into a different RenderJob and starts it, but uses the
     * ui.notify topic mechanism to send/transmit/push the results to the browser....
     * 
     * 
     * @author brian
     *
     */
    public static class DeferredViewDefQueryMapper extends Query {

		public DeferredViewDefQueryMapper(String pk, ViewDef vd) {
			super(pk, null);
		}

		@Override
		public void exec(RenderTask task) throws Exception {
			// TODO Auto-generated method stub
		}
    	
    }
    
	/**
 	 * Query that execute a nested ViewDef and embeds the results as a sub-table located in the specified field.
 	 * 
 	 * Uses an emedded PerRowSubTableMapper to repeat the ViewDef execution for each row.
 	 * @author brian
 	 */
     public static class NestedViewDefQueryMapper extends Query {
     	private Map<String, Object> params;
 		private ViewDef vd;
 		private String field;
     	
     	public NestedViewDefQueryMapper(String field, ViewDef vd) {
     		this(field, vd, null);
     	}
     	
     	public NestedViewDefQueryMapper(String field, ViewDef vd, Map<String, Object> extraParams) {
     		super(vd.getPrimaryQuery().getPK(), vd.getPrimaryQuery().getQueryString());
     		this.vd = vd;
     		this.field = field;
     		this.params = extraParams;
 		}
     	
     	/**
     	 * User can override this method to caclulate/compute additional view params if needed.
     	 */
    	protected Map<String, Object> getViewParams(RowRenderSubTask renderer) {
     		Map<String, Object> params = new HashMap<String, Object>();
     		params.putAll(renderer.getParams());
     		if (this.params != null) {
     			params.putAll(this.params);
     		}
    		params.putAll(renderer.getParentRow());
     		return params;
     	}
     	
     	
 		@Override
 		public void exec(RenderTask task) throws Exception {
 			if (task instanceof RowRenderSubTask) {
 				// if getViewParams() returns null, then take that as a sign to skip this row.
				Map<String, Object> params = getViewParams((RowRenderSubTask) task);
 				if (params == null) {
 					return;
 				}
 				
				// init + render the nested view and store its results in the specified field
				vd.init(null);
 				RenderTask results = task.getResource(ViewDefRenderer2.class).render(vd, params);
 				
 				// copy results
 				for (int i = 0; i < results.size(); i++) {
 					task.add(mapRow(task, results.getRowIdx(i)));
 				}
 			} else {
 				PerRowSubTableMapper rowMapper = new PerRowSubTableMapper(this.field, this);
 				rowMapper.exec(task);
 			}
 		}
     }    
}


