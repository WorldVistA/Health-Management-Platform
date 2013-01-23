package org.osehra.cpe.dao;

import org.osehra.cpe.vpr.pom.IDataStoreDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericDao extends IDataStoreDAO {
    <T> long count(Class<T> domainClass);

    <T, ID extends Serializable> T findOne(Class<T> domainClass, ID id);

    <T> T findByUid(Class<T> domainClass, String uid);

    <T> List<T> list(Class<T> domainClass);
    <T> Page<T> list(Class<T> domainClass, Pageable pageable);

    <T, ID extends Serializable> void delete(Class<T> domainClass, ID id);
    <T> void deleteAll(Class<T> domainClass);

    <T> T find(Class<T> domainClass, T domainObjectExample);
    <T> List<T> findAllWhere(Class<T> domainClass, T domainObject);
}
