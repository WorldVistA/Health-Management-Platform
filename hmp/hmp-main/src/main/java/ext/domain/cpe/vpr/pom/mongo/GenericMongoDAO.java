package org.osehra.cpe.vpr.pom.mongo;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.Result;
import org.osehra.cpe.vpr.ResultOrganizer;
import org.osehra.cpe.vpr.UidUtils;
import org.osehra.cpe.vpr.VitalSign;
import org.osehra.cpe.vpr.VitalSignOrganizer;
import org.osehra.cpe.vpr.pom.*;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class GenericMongoDAO implements IGenericPatientObjectDAO, IDataStoreDAO {
	protected static BasicDBObject EXCLUDES = new BasicDBObject("idx", 0).append("_id", 0);
	
	// mongo connection/drivers/metadata
	protected DB db;
	private Map<String, Map<String,POMIndex<?>>> indexes = new HashMap<String, Map<String,POMIndex<?>>>();
	private INamingStrategy namingStrategy = new DefaultNamingStrategy();

	public GenericMongoDAO(DB db) {
		this.db = db;
	}
	
	// protected helper methods ----------------------------------------------

	protected <T extends IPatientObject> T findOneByQuery(Class<T> clazz, DBObject q) {
		DBObject obj = getCollection(clazz).findOne(q, EXCLUDES);
		if (obj != null) {
			return (T) POMUtils.newInstance(clazz, obj.toMap());
		}
		return null;
	}
	
	/**
	 * Fetches and initalized a collection
	 * TODO: does this need to be thread safe?
	 */
	protected DBCollection getCollection(Class<? extends IPatientObject> obj) {
		DBCollection ret = db.getCollection(namingStrategy.collectionName(obj));
		if (indexes.containsKey(ret.getName())) {
			return ret; // no initalization necessary for this session
		}

		// extract index information and ensure index exists
		List<POMIndex<?>> idxlist = POMIndex.extractIndexes(obj);
		Map<String, POMIndex<?>> tmp = new HashMap<String,POMIndex<?>>();
		for (POMIndex<?> idx : idxlist) {
			tmp.put(idx.getIndexName(), idx);
			String indexField = "idx." + idx.getIndexName();
			ret.ensureIndex(new BasicDBObject(indexField, 1));
		}
		indexes.put(ret.getName(), tmp);
	
		return ret;
	}

	// IGenericPatientObjectDAO implementation ---------------------------
	@Override
	public <T extends IPatientObject> T findByUID(Class<T> clazz, String uid) {
		// Example 1: Raw Mongo client drivers
		DBObject q = new BasicDBObject("_id", uid);
		// don't return the index node, its only for the storage layer and can get large
		DBObject obj = getCollection(clazz).findOne(q, EXCLUDES);

		return (T) POMUtils.newInstance(clazz, obj.toMap());
	}

	@Override
	public <T extends IPatientObject> T findByUID(String uid) {
		return (T) this.findByUID(UidUtils.getDomainClassByUid(uid), uid);
	}

	@Override
	public <T extends IPatientObject> void save(T obj) {
		// if no PID, generate one using a counter table/collection
		if (obj instanceof Patient && obj.getPid() == null) {
			DBCollection col = db.getCollection("counters"); 
			DBObject q = new BasicDBObject("_id", "patientid"); 
			DBObject m = new BasicDBObject("$inc", new BasicDBObject("next", 1));
			String pid = col.findAndModify(q, null, null, false, m, true, true).get("next").toString();
			obj.setData("pid", pid);
		}
		
		// attempting to just hack the results in on their own, waiting for some M changes
		// to clean this up.
		if (obj instanceof ResultOrganizer) {
			List<Result> results = ((ResultOrganizer) obj).getResults();
			for (Result r : results) {
				save(r);
			}
		} else if (obj instanceof VitalSignOrganizer) {
			List<VitalSign> results = ((VitalSignOrganizer) obj).getVitalSigns();
			for (VitalSign r : results) {
				save(r);
			}
		}
		
		// to save this object to mongo, copy the UID value to _ID
		BasicDBObject data = new BasicDBObject(obj.getData(JSONViews.JDBView.class));
		data.put("_id", obj.getUid());

		// insert record
		getCollection((Class<T>) obj.getClass()).insert(data);
		
		// an experiment in using an extra single collection (for x-collection indexes + larger storage)
		data.put("collection", obj.getClass().getSimpleName());
		db.getCollection("vprobjects").insert(data);
	}

    @Override
    public <T extends IPatientObject> void delete(T obj) {
        deleteByUID(obj.getClass(), obj.getUid());
    }

    @Override
    public <T extends IPatientObject> void deleteByUID(Class<T> clazz, String uid) {
		getCollection(clazz).remove(new BasicDBObject("_id", uid));
	}

    @Override
	public <T extends IPatientObject> void deleteByPID(Class<T> clazz, String pid) {
		getCollection(clazz).remove(new BasicDBObject("pid", pid));
	}

    @Override
    public <T extends IPatientObject> int countByPID(Class<T> clazz, String pid) {
    	return (int) getCollection(clazz).count(new BasicDBObject("pid", pid));
    }

	@Override
	public <T extends IPatientObject> List<T> findAllByIndex(Class<T> clazz, String pid, String indexName, 
			String start, String end, Map<String, Object> where) {
		
		// initalize
		List<T> ret = new ArrayList<T>();
		DBCursor curs;
		String name = "idx." + indexName;
		BasicDBObject q = new BasicDBObject();
		boolean isRangeQuery = (end != null && !end.equals("") && !end.equals(start));
		DBCollection col = getCollection(clazz);
		
		// check that the index is valid
		if (!indexes.get(col.getName()).containsKey(indexName)) {
			String msg = String.format("No index named %s found in collection %s", indexName, col.getName());
			throw new IllegalArgumentException(msg);
		}
		
		// build the appropriate query and run it
		if (pid != null) q.append("pid", pid);
		if (isRangeQuery) {
			// TODO: Probably need to switch this to $elemMatch()
			curs = col.find(q.append(name, new BasicDBObject("$gte", start).append("$lte", end)), EXCLUDES);
		} else {
			curs = col.find(q.append(name, start), EXCLUDES);
		}
		
		// collect and return the results
		while (curs.hasNext()) {
			ret.add((T) POMUtils.newInstance(clazz, curs.next().toMap()));
		}
		return ret;
	}

	@Override
	public <T extends IPatientObject> Page<T> findAllByPID(Class<T> clazz,
                                                           String pid, Pageable page) {
		DBCursor curs = getCollection(clazz).find(new BasicDBObject("pid", pid), EXCLUDES);
		ArrayList<T> ret = new ArrayList<T>();
		while (curs.hasNext()) {
			ret.add((T) POMUtils.newInstance(clazz, curs.next().toMap()));
		}
		return new PageImpl<T>(ret);
	}
	
	@Override
	public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, String qry, Map<String, Object> params) {
		throw new NotImplementedException();
	}

	@Override
	public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, QueryDef qry, Map<String, Object> params) {
		DBCollection col = getCollection(clazz);
		
		// convert from DAOQuery to native mongo query, we specificically ignore fields info since we are returning full objects
		DBCursor curs = col.find(qry.buildMongoQuery(params));
		curs.skip(qry.getSkip()).limit(qry.getLimit()).sort(new BasicDBObject(qry.sort().getSortObject()));
		
		List<T> ret = new ArrayList<T>();
		while (curs.hasNext()) {
			DBObject obj = curs.next();
			ret.add((T) POMUtils.newInstance(clazz, obj.toMap()));
		}
		return ret;
	}

}
