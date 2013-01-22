package EXT.DOMAIN.cpe.vpr.pom;


import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface IGenericPatientObjectDAO extends Repository<IPatientObject, String> {

	// save/delete
	<T extends IPatientObject> void save(T obj);
    <T extends IPatientObject> void delete(T obj);
	<T extends IPatientObject> void deleteByUID(Class<T> clazz, String uid);
	<T extends IPatientObject> void deleteByPID(Class<T> clazz, String pid);

    // count
    <T extends IPatientObject> int countByPID(Class<T> clazz, String pid);

	// simple finder
	<T extends IPatientObject> T findByUID(Class<T> clazz, String uid);
	
	<T extends IPatientObject> T findByUID(String uid);
	
	// query finders
	<T extends IPatientObject> Page<T> findAllByPID(Class<T> clazz, String pid, Pageable page);
	/**
	 * @deprecated - use findAllByQuery() instead
	 */
	<T extends IPatientObject> List<T> findAllByIndex(Class<T> clazz, String pid, String indexName, String start, String end, Map<String, Object> where);
	<T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, QueryDef qry, Map<String, Object> params);
	<T extends IPatientObject> List<T> findAllByQuery(Class<T> clazz, String qry, Map<String, Object> params);
}
