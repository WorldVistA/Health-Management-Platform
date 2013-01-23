package org.osehra.cpe.domain.mappings;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

/**
 * This is an example of quick set up to test Hibernate mappings.
 * @author vhaislsovar
 *
 */
public class ExampleHibernateMappingITCase {
	
	@Test
	public void testJoin(){
		assertEquals(0, 0);
	    Session session = null;
        SessionFactory sessionFactory = new Configuration().configure( "test-hibernate.cfg.xml" ).buildSessionFactory();
        session = sessionFactory.openSession();
		String hql = "select pr.id as id, cmnts.comment as comments from Problem as pr join pr.comments as cmnts WHERE pr.id =:id";
		Query query = session.createQuery(hql);
		query.setParameter("id", 119L);
		List list = query.list();
		assertNotNull(list);
	}

	@Test
	public void testLabTrendComments(){
		assertEquals(0, 0);
	    Session session = null;
        SessionFactory sessionFactory = new Configuration().configure( "test-hibernate.cfg.xml" ).buildSessionFactory();
        session = sessionFactory.openSession();
		String hql = "SELECT r.uid as uid, r.typeName as name, coalesce(r.displayName,r.typeName) as display, r.typeCode as type, r.result as result, r.units as units, r.high as high, r.low as low, r.observed as observed, r.specimen as specimen, r.resultStatus.id as result_status_id, r.comment as comment, r.interpretation.code as interpretation FROM Result as r left join r.interpretation WHERE r.patient.id=42 AND r.observed  >= '20090518160706.652'   ORDER BY r.observed DESC";
		Query query = session.createQuery(hql);
		List list = query.list();
		assertNotNull(list);
	}

}
