package org.osehra.cpe.vpr.pom.jds;

import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.DomainNameUtils;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.UidUtils;
import org.osehra.cpe.vpr.pom.*;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

public class JdsGenericPatientObjectDAO extends JdsDaoSupport implements IGenericPatientObjectDAO, IDataStoreDAO {

    private INamingStrategy namingStrategy = new DefaultNamingStrategy();

    @Override
    public <T extends IPatientObject> void save(T item) {
        if (item instanceof Patient) {
            Patient patient = (Patient) item;
            URI vprPtUri = jdsTemplate.postForLocation("/vpr", patient);
            String[] pieces = vprPtUri.getPath().split("/");
            String pid = pieces[2];
            patient.setData("pid", pid);
            logger.debug("saved patient pid {}", pid);
        } else {
            Assert.hasText(item.getPid(), "[Assertion failed] - 'pid' must have text; it must not be null, empty, or blank");
            jdsTemplate.postForLocation("/vpr/" + item.getPid(), item);
            logger.debug("saved {} with uid {}", getDomain(item.getClass()), item.getUid());
        }
    }
    
    @Override
    public <T extends IPatientObject> void delete(T obj) {
        jdsTemplate.delete("/vpr/" + obj.getPid() + "/" + obj.getUid());
    }

    @Override
    public <T extends IPatientObject> void deleteByUID(Class<T> clazz, String uid) {
        jdsTemplate.delete("/vpr/uid/" + uid);
    }

    @Override
    public <T extends IPatientObject> void deleteByPID(Class<T> clazz, String pid) {
        if (Patient.class.isAssignableFrom(clazz)) {
            jdsTemplate.delete("/vpr/" + pid);
        } else {
            throw new NotImplementedException();
        }
    }

    @Override
    public <T extends IPatientObject> int countByPID(Class<T> clazz, String pid) {
        if (Patient.class.isAssignableFrom(clazz)) {
            throw new NotImplementedException();
        } else {
            JsonCCollection<Map<String, Object>> json = jdsTemplate.getForJsonC("/vpr/" + pid + "/count/domain");
            Set<String> domains = DomainNameUtils.getDomainsForClass(clazz);
            for (Map<String, Object> domainCount: json.getItems()) {
                if (domains.contains(domainCount.get("topic").toString())) {
                    Integer count = (Integer) domainCount.get("count");
                    return count;
                }
            }
            //throw new IllegalArgumentException("Unknown domain class '" + clazz + "'");
            return 0;//Instead of throwing an exception return zero - jds collections are dynamic.
        }
    }

    @Override
    public <T extends IPatientObject> T findByUID(Class<T> clazz, String uid) {
        if (Patient.class.isAssignableFrom(clazz)) {
            Patient patient = jdsTemplate.getForObject("/vpr/uid/" + uid, Patient.class);
            return (T) patient;
        } else {
            T item = jdsTemplate.getForObject("/vpr/uid/" + uid, clazz);
            return item;
        }
    }

    @Override
    public <T extends IPatientObject> Page<T> findAllByPID(Class<T> clazz, String pid, Pageable page) {
//    	if(page == null)
//    		throw new IllegalArgumentException("method requires argument of type " + Pageable.class + " not to be null");
    	
//    	QueryDef qry = new QueryDef(getDomain(clazz));
//    	
//    	int startRange = page.getOffset();
//    	int endRange = page.getOffset() + page.getPageSize();
//		qry.namedIndexRange(getDomain(clazz), String.valueOf(startRange), String.valueOf(endRange));
//    	qry.addCriteria(new QueryDefCriteria(getDomain(clazz));
//    	qry.addCriteria(QueryDefCriteria.where("pid").is(pid));
//    	qry.skip(page.getOffset());
//    	qry.limit(page.getPageSize());
//    	
//    	//pid is required to build url.
//    	HashMap<String,Object> params = new HashMap<String,Object>();
//    	params.put("pid", pid);
//    	
//   	List<T> ret = findAllByQuery(clazz, qry, params );
//    	/vpr/" + MOCK_PID + "/index/result?range=0..100&start=0&limit=100")).thenReturn(jsonc);
//    	String startRange = String.valueOf(page.getOffset());
//    	String endRange = String.valueOf(page.getOffset() + page.getPageSize());
//   			
//        T item = jdsTemplate.getForObject("/vpr/" + pid +"/index/" + getDomain(clazz) + "?" + );
    	List<T> ret  = findAllByUrl(clazz, "/vpr/" + pid +"/index/" + getDomain(clazz), new HashMap<String, Object>());
    	return new PageImpl<T>(ret, page, ret.size());
    }

    private String getDomain(Class clazz) {
        return namingStrategy.collectionName(clazz);
    }

    @Override
    public <T extends IPatientObject> List<T> findAllByIndex(Class<T> clazz, String pid, String indexName, String start, String end, Map<String, Object> where) {
    	HashMap<String,Object> params = new HashMap<String,Object>();
    	if(where !=null && where.size()>0){
    		params.putAll(where);
    	}
    	params.put("pid", pid);
    	QueryDef qryDef = new QueryDef();//no collection use index instead
    	qryDef.namedIndexRange(indexName, start, end);
    	return findAllByQuery(clazz, qryDef, params);
    }

    public <T> void delete(Class<T> domainClass, T item) {
        if (item instanceof IPatientObject)
            delete((IPatientObject) item);
        else
            throw new IllegalArgumentException("'item' must implemented" + IPatientObject.class);
    }

    @Override
    public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, String qry, Map<String, Object> params) {
    	return findAllByUrl(clazz, qry, params);
    }
    
	@Override
	public <T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, QueryDef qry, Map<String, Object> params) {
		// make the HTTP request
		String url = qry.toURL(params, qry.getSkip(), qry.getLimit());
        return findAllByUrl(clazz, url, params);
	}

	private  <T extends IPatientObject> List<T> findAllByUrl(Class<T> clazz, String url, Map<String, Object> params) {
		JsonCCollection<Map<String,Object>> response = jdsTemplate.getForJsonC(url, params);
        List<Map<String,Object>> items = (response != null) ? response.getItems() : null;
		
        // process and return the results
        ArrayList<IPatientObject> ret = new ArrayList<IPatientObject>();
		if (items != null) {
			for (Map<String, Object> item : items) {
				ret.add(POMUtils.newInstance(clazz, item));
			}
		}
		return (ArrayList<T>) ret;
	}

	@Override
	public <T extends IPatientObject> T findByUID(String uid) {
		return (T) this.findByUID(UidUtils.getDomainClassByUid(uid), uid);
	}
}
