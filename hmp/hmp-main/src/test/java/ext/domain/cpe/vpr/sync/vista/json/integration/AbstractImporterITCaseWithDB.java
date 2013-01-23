package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.hibernate.GenericHibMapDAO;
import org.osehra.cpe.vpr.pom.hibernate.PatientHibMapDAO;
import org.osehra.cpe.vpr.sync.vista.ImportException;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.junit.After;
import org.junit.Before;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AbstractImporterITCaseWithDB<T extends IPatientObject> extends AbstractImporterITCase<T> {

    // factory/dao to test with
    protected static SessionFactory fact;
    protected Session s;
    protected Transaction tx;
    protected IGenericPatientObjectDAO genericDao;

    public AbstractImporterITCaseWithDB(VistaDataChunk chunk) {
        super(chunk);
    }

    // DB/transaction initalization methods -------------------------------------

    @Before
    public void before() throws HibernateException {
        // only do this once
        if (fact == null) {
            setupMemoryDB();
        }

        // initalize session/transaction
        s = fact.getCurrentSession();
        tx = s.beginTransaction();

        genericDao = createGenericDao();
    }

    protected GenericHibMapDAO createGenericDao() {
        return new GenericHibMapDAO(fact, true, false);
    }

    @After
    public void after() {
        tx.rollback();
    }

    protected <T extends IPatientObject> void assertSave(T entity) {
        try {
            genericDao.save(entity);
        } catch (Exception e) {
            throw new ImportException(getChunk(), e);
        }
    }

    protected String getJDBCUrl() {
        return "jdbc:h2:mem:test;INIT=create schema if not exists VPR";
    }

    protected void setupMemoryDB() throws HibernateException {
        // create a new hibernate session factory (using our alternative configuration/mappings)
        URL x = PatientHibMapDAO.class.getResource("hibernate.cfg.xml");
        Configuration cfg = new Configuration().setNamingStrategy(new ImprovedNamingStrategy()).configure(x);

        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.connection.driver_class", "org.h2.Driver");
        props.put("hibernate.connection.url", getJDBCUrl());
        props.put("hibernate.connection.username", "sa");
        props.put("hibernate.connection.password", "");
        props.put("hibernate.current_session_context_class", "thread");
        cfg.addProperties(props);

        cfg.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
        fact = cfg.buildSessionFactory();

        // generate schema
        SchemaExport hbm2ddl = new SchemaExport(cfg);
        hbm2ddl.create(false, true);
    }

    // helper functions for doing some basic DB testing/fetching -----------------------------

    protected int countTableRows(String tableName) {
        Session s = fact.getCurrentSession();
        Object o = s.createSQLQuery("SELECT count(*) FROM " + tableName).uniqueResult();
        return Integer.parseInt(o.toString());
    }

    protected Map<String, Object> getTableRow(String tableName, int rowIdx) {
        Session s = fact.getCurrentSession();
        SQLQuery q = s.createSQLQuery("SELECT * FROM " + tableName);
        q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List l = q.list();
        if (rowIdx <= l.size()) {
            Object o = l.get(rowIdx - 1);
            return (Map<String, Object>) o;
        }
        return null;
    }

}
