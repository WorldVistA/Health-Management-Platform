package org.osehra.cpe.vpr.pom.hibernate;

import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObjectDAO;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public class PatientObjectHibMapDAO<T extends IPatientObject> implements IPatientObjectDAO<T> {

    private IGenericPatientObjectDAO genericDao;
    private Class<T> clazz;

    public PatientObjectHibMapDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Required
    public void setGenericDao(IGenericPatientObjectDAO genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public T save(T obj) {
        genericDao.save(obj);
        return obj;
    }

    @Override
    public void deleteByUID(String uid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteByPID(String pid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public T findByUID(String uid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Page<T> findAllByPID(String pid, Pageable page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<T> findAllByIndex(String pid, String indexName, String start, String end, Map<String, Object> where) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
