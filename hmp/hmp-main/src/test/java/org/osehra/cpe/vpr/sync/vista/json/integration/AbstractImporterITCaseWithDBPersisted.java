package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractImporterITCaseWithDBPersisted<T extends IPatientObject> extends AbstractImporterITCaseWithDB<T> {
	
	public AbstractImporterITCaseWithDBPersisted(VistaDataChunk chunk) {
		super(chunk);
	}
	
	protected String getJDBCUrl()
	{
		return "jdbc:h2:tcp://localhost/~/test;INIT=create schema if not exists VPR";
	}

	@Before
	public void before() throws HibernateException {
		// only do this once
		if (fact == null) {
			setupMemoryDB();
		}
		
		// initalize session/transaction
		s = fact.getCurrentSession();
		tx = s.beginTransaction();
	}
	
	@After
	public void after() {
		tx.commit();
	}
}
