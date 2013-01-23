package org.osehra.cpe.vpr.pom.jds;

import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.IPOMObject;
import org.osehra.cpe.vpr.pom.IPOMObjectDAO;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class JdsPOMObjectDAO<T extends IPOMObject> extends JdsDaoSupport implements IPOMObjectDAO<T> {

    private IGenericPOMObjectDAO genericDao;
    private Class<T> type;

    public JdsPOMObjectDAO(Class<T> type, IGenericPOMObjectDAO genericDao) {
        this.genericDao = genericDao;
        this.type = type;
    }

    @Override
    public T save(T entity) {
        genericDao.save(entity);
        return entity;
    }

    @Override
    public long count() {
        return genericDao.count(type);
    }

    @Override
    public void delete(String uid) {
        genericDao.deleteByUID(type, uid);
    }

    @Override
    public void delete(T entity) {
        genericDao.delete(entity);
    }

    @Override
    public void deleteAll() {
        genericDao.deleteAll(type);
    }

    @Override
    public T findOne(String uid) {
        return genericDao.findByUID(type, uid);
    }

    @Override
    public List<T> findAll() {
        return genericDao.findAll(type);
    }


    @Override
    public List<T> findAll(Sort sort) {
        // TODO: implement me
        throw new NotImplementedException();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return genericDao.findAll(type, pageable);
    }
}
