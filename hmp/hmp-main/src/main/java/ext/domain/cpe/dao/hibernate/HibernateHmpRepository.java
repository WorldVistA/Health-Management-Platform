package EXT.DOMAIN.cpe.dao.hibernate;

import EXT.DOMAIN.cpe.dao.HmpRepository;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public class HibernateHmpRepository<T, ID extends Serializable> implements HmpRepository<T, ID> {

    protected Class<T> clazz;
    protected SessionFactory sessionFactory;

    public HibernateHmpRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public T save(T entity) {
        getSession().save(entity);
        getSession().flush();
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public T findOne(ID id) {
        return (T) getSession().get(clazz, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getSession().createCriteria(clazz).list();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return ((Integer) getSession().createCriteria(clazz).setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @Override
    @Transactional
    public void delete(ID id) {
        delete(findOne(id));
    }

    @Override
    @Transactional
    public void delete(T entity) {
        getSession().delete(entity);
        getSession().flush();
    }

    @Override
    @Transactional
    public void deleteAll() {
        throw new NotImplementedException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Sort sort) {
        Criteria c = getSession().createCriteria(clazz);
        PaginationUtils.setSortCriteria(c, sort);
        return c.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        Criteria c = getSession().createCriteria(clazz);
        PaginationUtils.setPaginationCriteria(c, pageable);
        List<T> items = c.list();

        //  new criteria, set the row count projection
        c = getSession().createCriteria(clazz);
        c.setProjection(Projections.rowCount());
        Integer total = (Integer) c.uniqueResult();

        return new PageImpl(items, pageable, total);
    }
}
