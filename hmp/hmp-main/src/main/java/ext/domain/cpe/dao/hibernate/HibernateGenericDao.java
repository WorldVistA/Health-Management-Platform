package EXT.DOMAIN.cpe.dao.hibernate;

import EXT.DOMAIN.cpe.dao.GenericDao;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class HibernateGenericDao implements GenericDao {

    protected SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> long count(Class<T> domainClass) {
        return ((Integer) sessionFactory.getCurrentSession().createCriteria(domainClass).setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public <T, ID extends Serializable> T findOne(Class<T> domainClass, ID id) {
        return (T) getSession().get(domainClass, id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T findByUid(Class<T> domainClass, String uid) {
        Criteria criteria = getSession().createCriteria(domainClass);
        criteria.add(Restrictions.eq("uid", uid));
        return (T) criteria.uniqueResult();
    }

    @Transactional(readOnly = true)
    public <T> List<T> getAll(Class<T> domainClass, List<Long> ids) {
        return getAll(domainClass, ids.toArray(new Long[ids.size()]));
    }

    @Transactional(readOnly = true)
    public <T> List<T> getAll(Class<T> domainClass, Long... ids) {
        Criteria criteria = getSession().createCriteria(domainClass);

        for (Long id : ids) {
            criteria.add(Restrictions.idEq(id));
        }

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> domainClass) {
        return getSession().createCriteria(domainClass).list();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<T> list(Class<T> domainClass, Pageable pageable) {
        Criteria c = getSession().createCriteria(domainClass);
        PaginationUtils.setPaginationCriteria(c, pageable);
        List<T> items = c.list();

        //  new criteria, set the row count projection
        c = getSession().createCriteria(domainClass);
        c.setProjection(Projections.rowCount());
        Integer total = (Integer) c.uniqueResult();

        return new PageImpl(items, pageable, total);
    }

    @Override
    @Transactional
    public <T extends IPatientObject> void save(T item) {
        save((Class<T>) item.getClass(), item, Collections.singletonMap("flush", true));
    }

    @Transactional
    public <T extends IPatientObject> T save(Class<T> domainClass, T item, Map args) {
        getSession().saveOrUpdate(item);
        if (args != null && ((Boolean) args.get("flush") == true)) {
            getSession().flush();
        }
        return item;
    }

    @Override
    @Transactional
    public <T extends IPatientObject> void delete(T item) {
        delete((Class<T>) item.getClass(), item, true);
    }

    @Transactional
    public <T> void delete(Class<T> domainClass, T item, boolean flush) {
        getSession().delete(item);

        if (flush) {
            getSession().flush();
        }
    }

    @Override
    @Transactional
    public <T, ID extends Serializable> void delete(Class<T> domainClass, ID id) {
        delete((IPatientObject) findOne(domainClass, id));
    }

    @Override
    @Transactional
    public <T> void deleteAll(Class<T> domainClass) {
        getSession().createQuery("delete from " + domainClass).executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T find(Class<T> domainClass, T domainObjectExample) {
        List<T> items = findAllWhere(domainClass, domainObjectExample);
        if (items.isEmpty()) return null;
        return items.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findAllWhere(Class<T> domainClass, T domainObject) {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(this.sessionFactory);
        return (List<T>) hibernateTemplate.findByExample(domainObject);
    }
}
