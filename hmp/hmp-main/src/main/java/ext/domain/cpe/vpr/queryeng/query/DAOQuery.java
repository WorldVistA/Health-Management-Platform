package EXT.DOMAIN.cpe.vpr.queryeng.query;

import java.util.List;
import java.util.Map;

import EXT.DOMAIN.cpe.vpr.frameeng.IFrameExecContext;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.queryeng.Query;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDefRenderer;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.viewdef.ViewDefRenderer2;

/**
 * <p>DAOQuery uses a QueryDef and the active {@link IGenericPatientObjectDAO} to fetch full domain object
 * for including into ViewDefs.  If all you need is a small subset of fields, it may be more appropriate
 * to use {@link JDSQuery} which bypasses the DAO's and Domain objects.
 * 
 * <p>DAOQuery should work equally well no matter what DAO is active (Relational, JDS/Cache, JDS/Mongo)
 * 
 * <p>Theadvantage here is that you can use the {@link #mapRow(RenderTask, Map)} to map business logic from the domain objects
 * into the query results.
 * 
 * <p>To map domain objects into rows, you must implement {@link #objToRow(IPatientObject, RenderTask)}.
 * The default implementation simply calls {@link IPOMObject.getData()}
 */
public class DAOQuery<T extends IPatientObject> extends Query {
	private QueryDef qry;
	private Class<T> clazz;
	
	public DAOQuery(String pk, QueryDef qry, Class<T> clazz) {
		super(pk, null);
		this.qry = qry;
		this.clazz = clazz;
	}
	
	@Override
	public void exec(RenderTask task) throws Exception {
		IGenericPatientObjectDAO dao = task.getResource(IGenericPatientObjectDAO.class);
		
		// TODO: Problem: start/limit not set (and we don't want to set them in this.qry)
		// TODO: What to do with sort/fields/aliases?
		List<T> ret = dao.findAllByQuery(this.clazz, this.qry, task.getParams());
		for (T item : ret) {
			Map<String, Object> row = objToRow(item, task);
			if (row != null) {
				task.add(mapRow(task, row));
			}
		}
	}

	protected Map<String, Object> objToRow(T item, RenderTask task) {
		return item.getData();
	}
}
