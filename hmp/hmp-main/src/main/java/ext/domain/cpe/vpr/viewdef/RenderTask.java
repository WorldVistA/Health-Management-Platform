package org.osehra.cpe.vpr.viewdef;

import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.queryeng.Query;
import org.osehra.cpe.vpr.queryeng.Table;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.queryeng.ViewParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.perf4j.StopWatch;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Primary query's RenderTask serves as the return value
 * 
 * Serves as primary execution mechanism (synchronous, async)
 * 
 * TODO: Maybe make an interface RenderContext (w/o the task management methods) and RenderTask is main implementation?
 * - PrimaryTask adds getTotalSize() and maybe throws exception on getParentContext() and maybe calcParams() is only done in primary
 * - RowSubTask adds getRowIdx()/getRowKey() 
 * TODO: Maybe primary task is subclass of RenderTask? (or RenderJob vs RenderTask?)
 * TODO: How do subtasks share params? (Probably need to clone them?)
 * TODO: Should the subtasks look for params in their parent context? (if any)
 * 
 * @author brian
 */
public class RenderTask extends Table implements Callable<RenderTask> {
	private static ExpressionParser parser = new SpelExpressionParser();
	
	protected ViewDef def;
	protected Query q; // TODO:Switch to private when #1 is retired
	protected ExecutorService exec;
	
	private FrameTask task;
	private RenderTask parent;
	private List<RenderTask> subtasks = new ArrayList<RenderTask>();
	private Future<RenderTask> taskFuture;

	
	public RenderTask(FrameTask task, ViewDef def, Query q) {
		super(q.getPK());
		assert task != null;
		this.q = q;
		this.task = task;
		this.def = def;
	}
	
	public RenderTask(RenderTask parent, Query q) {
		this(parent.task, parent.def, q);
		this.parent = parent;
		this.exec = parent.exec;
	}

	
	// Primary getters --------------------------------------------------------
	
	public ViewDef getViewDef() {
		return def;
	}
	
	public Query getQuery() {
		return q;
	}
	
	public RenderTask getParentContext() {
		return this.parent;
	}
	
	public FrameTask getParentTask() {
		return this.task;
	}
	
    public String evalString(String querystr) {
        return parser.parseExpression(querystr, new TemplateParserContext()).getValue(this, String.class);
    }
	
	// get/set parameters -----------------------------------------------------
	
    public void calcParams() {
        for (ViewParam p : getViewDef().getParamDefs()) {
            setParams(p.calcParams(this));
        }
    }
    
    public void setParam(String key, Object val) {
    	this.task.setParam(key, val);
    }
    
    public void setParams(Map<String, Object> params) {
    	this.task.setParams(params);
    }
    
	public Map<String, Object> getParams() {
		return this.task.getParams();
    }	
    
	public <T> T getParam(Class<T> clazz, String key) {
		return this.task.getParam(clazz, key);
    }
    
	public Object getParamObj(String key) {
		return this.task.getParamObj(key);
    }
    
	public String getParamStr(String key) {
		return this.task.getParamStr(key);
    }
    
	public int getParamInt(String key) {
		return this.task.getParamInt(key);
    }
    
    // resource management ----------------------------------------------------

    public Object getResource(String bean) {
    	if (this.task != null) {
    		return task.getResource(bean);
    	}
    	return null;
    }

	
    public <T> T getResource(Class<T> clazz) {
    	if (this.task != null) {
    		return task.getResource(clazz);
    	}
    	return null;
    }
    
    public <T> T getResource(Class<T> clazz, String name) {
    	if (this.task != null) {
    		return task.getResource(clazz, name);
        }
    	return null;
	}
    
	// task management --------------------------------------------------------
	
	public RenderTask addSubTask(RenderTask task) {
		this.subtasks.add(task);
		return task;
	}
	
	public List<RenderTask> getSubTasks() {
		return this.subtasks;
	}
	
	/**
	 * Runs/invokes/starts this task.  If the execution service is available,
	 * then this will return immediately and run in the background, otherwise it will
	 * run now and block until its done.
	 * @throws Exception 
	 */
	public synchronized void start() throws Exception {
		if (exec == null) {
			this.call();
		} else {
			taskFuture = exec.submit(this);
		}
	}
	
	public synchronized void blockTillDone(long timeoutAt) throws InterruptedException, ExecutionException, TimeoutException {
		if (taskFuture != null) {
			taskFuture.get(getTimeoutMS(timeoutAt), TimeUnit.MILLISECONDS);
		}
		for (RenderTask subtask : subtasks) {
			subtask.blockTillDone(timeoutAt);
		}
	}

	/**
	 * Actually runs the task, maybe asyncronously.  Called from start(), should not be called
	 * directly.
	 */
	@Override
	public RenderTask call() throws Exception {
		StopWatch watch = new StopWatch("view.query." + getQuery().getClass().getSimpleName());
		calcParams();
		getQuery().exec(this);
		watch.stop();
		return this;
	}
	
	public static class RowRenderSubTask extends RenderTask {
		private int rowidx;
		
		public RowRenderSubTask(RenderTask parentTask, Query q, int rowidx) {
			super(parentTask, q);
			assert rowidx >= 0;
			this.rowidx = rowidx;
		}
		
	    // current row/parent row -------------------------------------------------
		
		public int getRowIdx() {
			return this.rowidx;
		}
	    
	    public String getParentRowKey() {
	    	if (getParentContext() == null || getRowIdx() <= -1) {
	    		return null;
	    	}
	    	Object obj = getParentContext().getCellIdx(getRowIdx(), getParentContext().getPK());
	    	if (obj == null) return null;
	    	return obj.toString();
	    }
	    
	    public String getParentRowPK() {
	    	if (getParentContext() == null || getRowIdx() <= -1) {
	    		return null;
	    	}
	    	return getParentContext().getPK();
	    }
	    
	    public Object getParentRowVal(String key) {
	    	if (getParentContext() == null || getRowIdx() <= -1) {
	    		return null;
	    	}
	    	return getParentContext().getCellIdx(getRowIdx(), key);
	    }
	    
	    public Map<String, Object> getParentRow() {
	    	if (getParentContext() == null || getRowIdx() <= -1) {
	    		return null;
	    	}
	    	return getParentContext().getRowIdx(getRowIdx());
	    }
	    
	}
	
	public static class RenderJob extends RenderTask {
		public RenderJob(FrameTask task, ViewDef def, Query q, ExecutorService exec) {
			super(task, def, q);
			this.exec = exec;
		}
		
		@Override
		public RenderTask getParentContext() {
			throw new UnsupportedOperationException("RenderJob has no parent context.");
		}
	}
	
	private static final long getTimeoutMS(long timeoutAt) {
		return Math.max(timeoutAt - System.currentTimeMillis(), 1);
	}
}
