package EXT.DOMAIN.cpe.vpr.pom.hibernate;

import EXT.DOMAIN.cpe.dao.hibernate.PaginationUtils;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.Result;
import EXT.DOMAIN.cpe.vpr.UidUtils;
import EXT.DOMAIN.cpe.vpr.pom.*;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefWalker.MatchQueryWalker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.hibernate.criterion.Restrictions.*;

public class GenericHibMapDAO implements IGenericPatientObjectDAO, IDataStoreDAO {

    private SessionFactory sessionFactory;
    private boolean enablePropertiesTable;
    private boolean enableIndexTable;

    public GenericHibMapDAO(SessionFactory sessionFactory, boolean enableIndexTable, boolean enablePropertiesTable) {
        this.sessionFactory = sessionFactory;
        this.enableIndexTable = enableIndexTable;
        this.enablePropertiesTable = enablePropertiesTable;
    }

    // protected helper methods for working with hibernate entity maps  --------------

    protected Session curSess() {
        return sessionFactory.getCurrentSession();
    }

    protected Criteria getBlankCritiera(Class<? extends IPatientObject> clazz) {
        return curSess().createCriteria(getCollectionName(clazz));
    }

    protected String getCollectionName(Class<? extends IPatientObject> clazz) {
        return ClassUtils.getShortNameAsProperty(clazz);
    }

    protected <T extends IPatientObject> T mapToResult(Class<T> clazz, Map<String, Object> data) {
        if (data == null) return null;
        data.remove("idx"); // junk that should not be read from the database

        // use the full JSON document if it exists
        if (data.containsKey("json")) {
            return POMUtils.newInstance(clazz, (String) data.get("json"));
        }
        return POMUtils.newInstance(clazz, data);
    }

    protected <T extends IPatientObject> T findOneByCriteria(Class<T> clazz, Criteria c) {
        List results = c.list();
        if (results != null && results.size() > 0) {
            Map<String, Object> data = (Map<String, Object>) results.get(0);
            // TODO: handle projection to just JSON string
            return mapToResult(clazz, data);
        }
        return null;
    }

    protected <T extends IPatientObject> Page<T> findAllByCriterion(Class<T> clazz, Criterion criterion, Pageable pageable) {
        Criteria criteria = getBlankCritiera(clazz);
        criteria.add(criterion);
        PaginationUtils.setPaginationCriteria(criteria, pageable);

        List<T> results = criteria.list();
        List<T> ret = mapToResults(clazz, results);

        //  new criteria, set the row count projection
        criteria = getBlankCritiera(clazz);
        criteria.add(criterion);
        criteria.setProjection(Projections.rowCount());
        Integer total = (Integer) criteria.uniqueResult();

        return new PageImpl(ret, pageable, total);
    }

    protected <T extends IPatientObject> List<T> mapToResults(Class<T> clazz, List<T> results) {
        List<T> ret = new ArrayList<T>();
        for (int i = 0; results != null && i < results.size(); i++) {
            Map<String, Object> data = (Map<String, Object>) results.get(i);
            ret.add(mapToResult(clazz, data));
        }
        return ret;
    }

    @Override
    public <T extends IPatientObject> int countByPID(Class<T> clazz, String pid) {
        return ((Integer) getBlankCritiera(clazz).add(eq("pid", pid)).setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    // primary query methods --------------------------------------------------------

    @Transactional(readOnly = true)
    public <T extends IPatientObject> T findByUID(Class<T> clazz, String uid) {
        //crit.setProjection(Projections.property("json")); // this would be more efficient, but returns a list of strings
        Criteria crit = getBlankCritiera(clazz).add(eq("uid", uid));
        return findOneByCriteria(clazz, crit);
    }

	@Override
	public <T extends IPatientObject> T findByUID(String uid) {
		return (T) this.findByUID(UidUtils.getDomainClassByUid(uid), uid);
	}

    @Transactional(readOnly = true)
    public <T extends IPatientObject> T findByPID(Class<T> clazz, String pid) {
        //crit.setProjection(Projections.property("json")); // this would be more efficient, but returns a list of strings
        Criteria crit = getBlankCritiera(clazz).add(eq("pid", pid));
        return findOneByCriteria(clazz, crit);
    }

    @Transactional(readOnly = true)
    public <T extends IPatientObject> Page<T> findAllByPID(Class<T> clazz,
                                                           String pid, Pageable page) {
        return findAllByCriterion(clazz, eq("pid", pid), page);
    }

    /**
     * This returns results from a named index using the range.
     * <p/>
     * - PID is optional (but recommended)
     * - if end range is missing, then start is treated as equals
     * <p/>
     * TODO: Throw an error if its an invalid index name?
     * TODO: This is going to be a very high traffic area, optimize it
     */
    @Transactional(readOnly = true)
    public <T extends IPatientObject> List<T> findAllByIndex(Class<T> clazz, String pid, String indexName,
                                                             String start, String end, Map<String, Object> where) {

        if (!enableIndexTable) {
            throw new IllegalArgumentException("Indexing has not been enabled on this DAO");
        } else if (pid == null && start == null) {
            throw new IllegalArgumentException("You must specify start+end query range, pid+start range, or at least a pid");
        }

        // construct the appropriate query, only fetch the UID field
        Criteria crit = curSess().createCriteria("VPRIndexEntry").add(eq("index_name", indexName));
        crit.setProjection(Projections.property("uid"));
        if (pid != null) {
            crit.add(eq("pid", pid));
        }
        if (end != null) {
            // range query
            crit.add(ge("index_value", start));
            crit.add(le("index_value", end));
        } else if (start != null) {
            // single value query
            crit.add(eq("index_value", start));
        }

        // execute the query
        List<String> results = crit.list();

        // then loop though those matching records and load them individually
        List<T> ret = new ArrayList<T>();
        for (String uid : results) {
            if (where == null) {
                ret.add(findByUID(clazz, uid));
            } else {
                // if where properties are specified, then perform a different query
                // TODO: this should probably be migrated to a server side query as this is not as efficient one-by-one.
                // TODO: or maybe use IN() to do chunks of evaluations...
                crit = getBlankCritiera(clazz).add(eq("uid", uid));
                for (String key : where.keySet()) {
                    crit.add(eq(key, where.get(key)));
                }
                T obj = findOneByCriteria(clazz, crit);
                if (obj != null) {
                    ret.add(obj);
                }
            }
        }
        return ret;
    }

    // primay save/delete/update methods ---------------------------------------------

    @Transactional
    public <T extends IPatientObject> void save(T obj) {
        String entityName = getCollectionName(obj.getClass());
        Session s = curSess();

        // fetch the current record (if any)
        String uid = obj.getUid();
        String pid = obj.getPid();
        Criteria q = s.createCriteria(entityName).add(eq("uid", uid));
        Map<String, Object> data = (Map<String, Object>) q.uniqueResult();
        if (data == null) {
            data = new HashMap<String, Object>();
        } else {
            pid = (String) data.get("pid");
            //Delete existing object instead of merging
            curSess().delete(data);
            data.clear();
        }

        // assign a new PID from our sequence table if none exists
        if (obj instanceof Patient) {
            if (pid == null) {
                pid = s.save("VPRPatientIDs", new HashMap<String, Object>()).toString();
            }

            ((Patient) obj).setData("pid", pid);
        }

        // update it with the new/current data
        data.putAll(obj.getData(JSONViews.JDBView.class));
        data.put("json", obj.toJSON(JSONViews.JDBView.class));
        data.put("pid", pid);

        // save index if requested
        if (enableIndexTable) {
            // delete any existing index entries
            // TODO:This implementation is not completely correct, but it will work for now.
            //s.createQuery("DELETE FROM VPRIndexEntry WHERE uid=?").setString(0, obj.getUid()).executeUpdate();
            //s.flush();

            // insert each new one
            List<Map<String, Object>> idxdata = obj.getIDX();
            int counter = 0;
            for (Map<String, Object> m : idxdata) {

                // for now the map can only have 1 entry, morph it
                String key = m.keySet().iterator().next();
                Map<String, Object> save = new HashMap<String, Object>();
                save.put("uid", obj.getUid());
                save.put("pid", pid);
                save.put("idx", counter++);
                save.put("index_name", key);
                save.put("index_value", m.get(key));

                save = merge("VPRIndexEntry", save);
                s.save("VPRIndexEntry", save);
            }
        }

        // FIXME: giant hack to get results to merge in session - replace with two separate extracts for individual results and accessions rather thanr relying on cascade
//        if (obj instanceof ResultOrganizer) {
//            mergeResults(data);
//        }

        // save the actual record
        s.saveOrUpdate(entityName, data);
        s.flush();
    }

    // FIXME: giant hack to get results to merge in session - replace with two separate extracts for individual results and accessions rather thanr relying on cascade
    private void mergeResults(Map<String, Object> data) {
        String resultEntityName = getCollectionName(Result.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("results");
        if (results == null) return;
        List<Map<String, Object>> mergedResults = new ArrayList<Map<String, Object>>(results.size());
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> r = merge(resultEntityName, results.get(i));
            mergedResults.add(r);
        }
        data.put("results", mergedResults);
    }

    // work around for hibernate not recognizing newly generated Map as same entity in session and its own merge() being too tricky across associations
    private Map<String, Object> merge(String entityName, Map<String, Object> data) {
        ClassMetadata metadata = sessionFactory.getClassMetadata(entityName);
        Serializable id = metadata.getIdentifier(data, EntityMode.MAP);
        Map<String, Object> existingDataInSession = (Map<String, Object>) curSess().get(entityName, id);
        // identity of Map in session is the Java reference
        if (existingDataInSession != null) {
            existingDataInSession.clear();
            existingDataInSession.putAll(data);
        } else {
            existingDataInSession = data;
        }
        return existingDataInSession;
    }

    @Transactional
    public <T extends IPatientObject> void delete(T obj) {
        deleteByUID(obj.getClass(), obj.getUid());
    }

    @Transactional
    public <T extends IPatientObject> void deleteByUID(Class<T> clazz, String uid) {
        String entityName = getCollectionName(clazz);
        Map<String, Object> data = (Map<String, Object>) curSess().createCriteria(entityName).add(eq("uid", uid)).uniqueResult();
        curSess().delete(entityName, data);
        curSess().flush();
    }

    @Override
    public <T extends IPatientObject> void deleteByPID(Class<T> clazz, String pid) {
        String entityName = getCollectionName(clazz);
        Map<String, Object> data = (Map<String, Object>) curSess().createCriteria(entityName).add(eq("pid", pid)).uniqueResult();
        curSess().delete(entityName, data);
        curSess().flush();
    }
    
    
	@Override
	public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, String qry, Map<String, Object> params) {
		throw new NotImplementedException();
	}


    /**
     * converting from DAOQUery to SQL is hard, for now only the index stuff is translated, the rest is
     * filtered middle-tier
     */
	@Override
	public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, QueryDef qry, Map<String, Object> params) {
		Map<String, Object> querydata = qry.getQueryObject(params);
		MatchQueryWalker matcher = new MatchQueryWalker(querydata);
		
		// gather the main index data from the query
		String pid = (String) querydata.get("pid");
		String indexName = null;
		String start = null;
		String end = null;
		if (qry.getIndexCriteria() != null) {
			indexName = qry.getIndexCriteria().getKey();
			Object val = qry.getIndexCriteria().buildCriteriaObject(params).get(indexName);
			if (val instanceof Map) {
				start = (String) ((Map) val).get("$gte");
				end = (String) ((Map) val).get("$lte");
			} else {
				start = val.toString();
			}
		}
		
		// execute the query and filter the results before returning them
		List<T> ret = findAllByIndex(clazz, pid, indexName, start, end, null);
		for (T item : ret) {
			Map data = item.getData();
			if (!matcher.matches(data)) {
				ret.remove(item);
			}
		}
		
		return ret;
	}

}
