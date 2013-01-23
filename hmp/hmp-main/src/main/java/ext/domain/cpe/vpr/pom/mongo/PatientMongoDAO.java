package org.osehra.cpe.vpr.pom.mongo;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.vistasvc.CacheMgr;
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType;
import org.osehra.cpe.vpr.vistasvc.ICacheMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PatientMongoDAO implements IPatientDAO {

	private GenericMongoDAO dao;
	private ICacheMgr<Patient> cache = new CacheMgr<Patient>("patients", CacheType.DISK);
	public PatientMongoDAO(GenericMongoDAO dao) {
		this.dao = dao;
	}

	// core IPatientDAO implementation --------------------------------------
	
	@Override
	public Patient findByIcn(String icn) {
		Patient ret = cache.fetch(icn);
		if (ret == null) {
			ret = dao.findOneByQuery(Patient.class, new BasicDBObject("icn", icn));
			cache.storeUnlessNull(icn, ret);
			cache.storeUnlessNull(ret == null ? null : ret.getPid(), ret);
		}
		return ret;
	}
	
	@Override
	public Patient findByVprPid(String id) {
		Patient ret = cache.fetch(id);
		if (ret == null) {
			ret = dao.findOneByQuery(Patient.class, new BasicDBObject("pid", id));
			cache.storeUnlessNull(id, ret);
		}
		return ret;
	}

	@Override
	public Patient findByAnyPid(String pid) {
		Patient ret = cache.fetch(pid);
		if (ret == null) {
			List<Patient> results = findAllByIndex(null, "patient-ids", pid, null, null);
			if (results != null && results.size() > 0) {
				return cache.store(pid, results.get(0));
			}
		}
		return null;
	}
	
	@Override
	public Patient findByLocalID(String systemOrCode, String dfn) {
		return findByAnyPid(systemOrCode + ";" + dfn);
	}

    @Override
    public Page<Patient> findAll(Pageable pageable) {
		DBCursor curs = dao.getCollection(Patient.class).find(new BasicDBObject(), dao.EXCLUDES);
		ArrayList<Patient> ret = new ArrayList<Patient>();
		while (curs.hasNext()) {
			ret.add((Patient) POMUtils.newInstance(Patient.class, curs.next().toMap()));
		}
		return new PageImpl<Patient>(ret);
    }

    @Override
	public List<String> listPatientIds() {
    	DBCollection c = dao.getCollection(Patient.class);
    	DBCursor curs = c.find(new BasicDBObject(), new BasicDBObject("pid", 1));
    	List<String> ret = new ArrayList<String>();
    	while (curs.hasNext()) {
    		DBObject row = curs.next();
    		ret.add((String) row.get("pid"));
    	}
    	return ret;
	}

	@Override
	public int count() {
		return listPatientIds().size();
	}
	
	// Simple methods delegated to parent ---------------------------------------

	@Override
	public void deleteByUID(String uid) {
		cache.remove(uid);
		dao.deleteByUID(Patient.class, uid);
	}

	@Override
	public void deleteByPID(String pid) {
		cache.removeAll();
		// delete all the data for this patient
		Set<String> collections = dao.db.getCollectionNames();
		for (String c : collections) {
			DBCollection col = dao.db.getCollection(c);
			col.remove(new BasicDBObject("pid", pid));
		}
	}

	@Override
	public Patient findByUID(String uid) {
		return dao.findByUID(Patient.class, uid);
	}

	@Override
	public Page<Patient> findAllByPID(String pid, Pageable page) {
		return dao.findAllByPID(Patient.class, pid, page);
	}

	@Override
	public List<Patient> findAllByIndex(String pid, String indexName,
			String start, String end, Map<String, Object> where) {
		return dao.findAllByIndex(Patient.class, pid, indexName, start, end, where);
	}

	@Override
	public Patient save(Patient pat) {
		cache.remove(pat.getIcn());
		cache.remove(pat.getPid());
		dao.save(pat);
		return pat;
	}
}
