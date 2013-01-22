package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.frameeng.Frame;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.IFrameActionExec;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.PatientAction;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.ViewRenderAction;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrameTrigger.CallTrigger;
import EXT.DOMAIN.cpe.vpr.pom.PatientEvent;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.QueryColDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.SessionCallback;


/**
 * ViewDef contains everything needed to define and render a complex data view for a user.
 * 
 * ViewDef contains User Parameters, Filters, Queries and Column Definitions
 * ViewDef is rendered (via the ViewDefRenderer helper class) into some data format (JSON, XML, Objects, etc).
 * 
 * The basic usage pattern is:
 * 1) User params are set/configured via setParams(...)
 * 2) The render() method (using a ViewDefRenderer helper class) is called to execute one or more queries.
 * 3) The defined columns are used by the ViewDefRenderer to merge and map the query results into a single result set.
 * 4) Filters are applied, which can further filter/manipulate the results. 
 * 5) The ViewDefRenderer then typically marshals the results into JSON, XML, HTML, etc.
 * 
 * ViewDef is intended to be completely state-less, all relevant parameters must be supplied at runtime by a view service
 * that would track user preferences, parameters, and permissions.
 *   
 * ViewDef is currently not thread safe, a new instance is expected to be created for each request/run/render, however
 * soon multiple ViewDefRenderer(s) should be able to render from the same ViewDef simultaneously. 
 * 
 * First query defined in queries must be the primary query.  The primary query is the one that determines which rows are available 
 * and is always executed first.
 * 
 * The list of defined columns do not necessarily map 1-to-1 with the resulting fields/columns in the rendered results.
 */
public abstract class ViewDef extends Frame {
	
	private List<Query> queries = new ArrayList<Query>();
	private List<ColDef> columns = new ArrayList<ColDef>();
	private Map<String, ColDef> columnidx = new HashMap<String, ColDef>();
	private CallTrigger trigCall;
	
	public ViewDef() {
		// declare default params (but do not re-declare them if they already exist)
		if (getParamDefs(ViewParam.PaginationParam.class).size() == 0) 
			declareParam(new ViewParam.PaginationParam());
		if (getParamDefs(ViewParam.ColumnsParam.class).size() == 0) 
			declareParam(new ViewParam.ColumnsParam(this));
		if (getParamDefs(ViewParam.ViewInfoParam.class).size() == 0) 
			declareParam(new ViewParam.ViewInfoParam(this));
		
		trigCall = addTrigger(new CallTrigger(this));
	}
	
	/*
	 * QUERY + COLUMN DECLARE/GET/SET METHODS
	 */
	protected Query addQuery(Query q) {
		assert q != null;
		if (isInitalized()) {
			throw new IllegalStateException("Cannot add queries after a frame has been initalized.");
		}
		this.queries.add(q);
		return q;
	}
	
	public List<Query> getQueries(RenderTask renderer) {
		return this.queries;
	}
	
	public Query getPrimaryQuery() {
		if (this.queries.size() == 0) {
			return null;
		}
		return this.queries.get(0);
	}
	
	protected ColDef addColumn(ColDef c) {
		assert c.getKey() != null;
		this.columns.add(c);
		this.columnidx.put(c.getKey(), c);
		return c;
	}
	
	protected void addColumns(ColDef[] cols) {
		for (ColDef col : cols) {
			addColumn(col);
		}
	}
	
	/**
	 * Convenience method to map multiple query fields at once into columns of the view def
	 * (Using QueryColDef's).  Example:
	 * 
	 * <pre>
	 * addColumns(q1, "field1", "field2", "field3");
	 * </pre>
	 * Is the same as:
	 * <pre>
	 * addColumn(new QueryColDef(q1, "field1"));
	 * addColumn(new QueryColDef(q1, "field2"));
	 * addColumn(new QueryColDef(q1, "field3"));
	 * </pre>
	 *  
	 * Replaces the MapperField ColDef from before
	 */
	protected void addColumns(Query q, String... fields) {
		addColumns(q, Arrays.asList(fields));
	}
	
	protected void addColumns(Query q, List<String> fields) {
		for (String f : fields) {
			addColumn(new QueryColDef(q, f));
		}
	}
	
	public List<ColDef> getColumns() {
		return this.columns;
	}

	public ColDef getColumn(String key) {
		return this.columnidx.get(key);
	}
	
	/*
	 * RUNTIME METHODS
	 */
	public boolean validate() {
		// check that queries have been defined
		if (queries.size() == 0) {
			return false;
		}
		
		return true;
	}
	
	public void exec(FrameTask task) throws FrameExecException {
		long timeoutAt = System.currentTimeMillis() + 5000;
        try {
        	
        	// if the trigger is not the built in call trigger, then its probably 
        	// an extra trigger for update subscriptions
        	if (!trigCall.isTriggerOf(task)) {
        		execViewSub(task);
        		return;
        	}
        	
    		// run the primary query sync in the current thread
        	Query primary = getPrimaryQuery();
    		RenderTask rendertask = new RenderTask(task, this, primary);
			if (!validate()) {
				throw new FrameExecException("Invalid ViewDef state", this);
			}
			rendertask.call();
			
    		// append the rest of the queries as subTasks
    		for (Query q : getQueries(rendertask)) {
    			if (q != primary) {
    				rendertask.addSubTask(new RenderTask(rendertask, q)).start();
    			}
    		}
    		
    		// run the rest of the queries in parallel, and p
    		rendertask.blockTillDone(timeoutAt);
    		task.addAction(new ViewRenderAction(rendertask));
    		postProcessHook(rendertask);
        } catch (TimeoutException ex) {
        	String msg = "Timeout while rendering viewdef: " + getClass();
        	throw new FrameExecException(msg, ex, this);
        } catch (InterruptedException ex) {
        	String msg = "Interrupted while rendering viewdef: " + getClass();
        	throw new FrameExecException(msg, ex, this);
		} catch (Exception ex) {
        	String msg = "Exception rendering viewdef: " + getClass();
        	throw new FrameExecException(msg, ex, this);
		}
	}
	
	public void execViewSub(FrameTask task) {
		// should have a patient context
		if (task.getTriggerEvent() instanceof PatientEvent) {
			task.addAction(new ViewDefUpdateAction(this, (PatientEvent<?>) task.getTriggerEvent()));
		}
	}
	
	protected void postProcessHook(RenderTask task) {
		// default does nothing....
	}
	
	/**
	 * TODO: Should evolve this into a more generic UINotifyAction, so it can be reused in
	 * ping, tasks, etc?
	 */
	public static class ViewDefUpdateAction extends PatientAction implements IFrameActionExec {
		private ViewDef def;
		private PatientEvent<?> evt;
		private Topic topic;

		public ViewDefUpdateAction(ViewDef def, PatientEvent<?> evt) {
			super(evt.getPID());
			this.def = def;
			this.evt = evt;
		}

		@Override
		public void exec(FrameJob job) {
			JmsTemplate tpl = job.getResource(JmsTemplate.class);
			if (topic == null) {
				tpl.execute(new SessionCallback<Object>() {
					@Override
					public Object doInJms(Session session) throws JMSException {
						topic = session.createTopic("ui.notify");
						return null;
					}
				});
			}
			
			tpl.send(topic, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					Message msg = session.createObjectMessage(evt);
					msg.setStringProperty("type", "viewdef");
					msg.setStringProperty("viewdef.id", def.getID());
					msg.setStringProperty("viewdef.name", def.getName());
					msg.setStringProperty("pid", getPid());
					msg.setStringProperty("uid", evt.getUID());
					msg.setObjectProperty("changes", evt.getChanges());
					return msg;
				}
			});
		}
	}
	
	/**
	 * In a dynamic view def, the set of queries to run is constructed at runtime based on current parameter values.
	 * 
	 * Declaring parameters should still be done in the constructor
	 * 
	 * If the only thing that is dynamic is the queries and their definition, then this can be reused, if you
	 * have a need for custom columns, filters, etc then this will not be thread safe and probably can't be reused and
	 * a new instance must be created prior to each rendering.
	 * 
	 * @author brian
	 */
	public static class BoardViewDef extends ViewDef {
		
		private ViewDef base;

		public BoardViewDef(String name, ViewDef base, List<ColDef> cols) {
			setName(name);
			setID("dynamicviewdef:" + name); // TODO: Include user DUZ prefix to guarantee uniqueness?
			this.base = base; // TODO: Need to copy anything from base? (config, triggers, queries, etc?)
			
			// columns are merged from the base viewdef + the specified cols
			addColumns((ColDef[]) this.base.getColumns().toArray());
			addColumns((ColDef[]) cols.toArray());
		}
		
		@Override
		public void exec(FrameTask task) throws FrameExecException {
			// delegate to the base viewdef
			base.exec(task);
		}
		
	}
}
