package EXT.DOMAIN.cpe.vpr.pom.mongo;

import static org.junit.Assert.*;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.pom.hibernate.PatientHibMapDAO;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDAOITCase {

	DB db;
	GenericMongoDAO dao;
	
	@Before
	public void setup() throws UnknownHostException, MongoException {
		Mongo m = new Mongo("localhost");
		db = m.getDB("tmp");
		db.dropDatabase();
		dao = new GenericMongoDAO(db);
	}
	
	@After
	public void teardown() {
		//db.dropDatabase();
	}
	
	@Test
    public void testPatientObjects() {
        InputStream json = Patient.class.getResourceAsStream("sync/vista/json/patient.json");
        Patient p = POMUtils.newInstance(Patient.class, json);
        dao.save(p);
        
        // assert one row in the collection
        assertTrue(db.collectionExists("patient"));
        DBCollection col = db.getCollection("patient");
        assertEquals(1, col.count());
        
        // using the PatientMongoDAO
        PatientMongoDAO pdao = new PatientMongoDAO(dao);
        List<String> ptlist = pdao.listPatientIds();
        assertEquals(1, ptlist.size());
        assertEquals("1", ptlist.get(0));
        assertEquals(1, pdao.count());
	}
	
}
