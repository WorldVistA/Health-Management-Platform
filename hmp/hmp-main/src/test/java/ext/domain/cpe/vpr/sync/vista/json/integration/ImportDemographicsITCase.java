package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession;
import EXT.DOMAIN.cpe.test.junit4.runners.Importer;
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner;
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients;
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.pom.JSONViews;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.pom.hibernate.PatientHibMapDAO;
import EXT.DOMAIN.cpe.vpr.pom.mongo.GenericMongoDAO;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.PatientImporter;

import java.util.List;
import java.util.Map;

import EXT.DOMAIN.cpe.vpr.sync.vista.json.PatientImporter;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.Mongo;


@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"229", "100846"})
@VprExtract(domain = "demographics")
@Importer(PatientImporter.class)
public class ImportDemographicsITCase extends AbstractImporterITCaseWithDB<Patient> {
	
	public ImportDemographicsITCase(VistaDataChunk chunk) {
        super(chunk);
    }
	
    @Test
    public void testDemographicsImporter() {
    	assertNotNull(getDomainInstance());
    	
    	// save the patient into the dao
    	PatientHibMapDAO dao = new PatientHibMapDAO(fact);
    	Patient p = getDomainInstance();
    	dao.save(p);
    	
    	// original JSON data for comparison
    	Map<String, Object> data = getChunk().getJsonMap();
    	
    	// count the number of sub-records
    	List addrs = (List) data.get("addresses");
    	List aliases = (List) data.get("aliases");
    	List facilities = (List) data.get("facilities");
    	
		// confirm same number of DB rows
		assertEquals(1, countTableRows("patient"));
		assertEquals(addrs.size(), countTableRows("patient_address"));
		assertEquals(aliases == null ? 0 : aliases.size(), countTableRows("patient_alias"));
		assertEquals(facilities.size(), countTableRows("patient_facility"));
		
		// we can even test a couple getters if we want
		p = dao.findByUID((String) data.get("uid"));
		assertNotNull(p);
    }
    
    @Test
    @Ignore
    public void testDemongraphicsImporterWithMongo() throws Exception {
    	
    	// save the patient into the dao
    	Mongo mongo = new Mongo();
    	GenericMongoDAO dao = new GenericMongoDAO(mongo.getDB("TEST"));
    	Patient p = getDomainInstance();
    	Map<String, Object> origData = p.getData(JSONViews.EventView.class);
    	dao.save(p);
    	
    	// original JSON data for comparison with fetched object
    	Map<String, Object> data = getChunk().getJsonMap();
    	p = dao.findByUID(Patient.class, (String) data.get("uid"));
    	assertNotNull(p);
    	
    	// using the event detection functions to find any differences in the object
    	// before it was written to mongo and after
    	Map<String, Object> newData = p.getData(JSONViews.EventView.class);
    	
    	List<String> changes = POMUtils.getMapChangedFields(origData, newData);
    	for (String key : changes) {
    		Object origValue = origData.get(key);
    		Object newValue = newData.get(key);
    		System.out.println(key);
    	}
    	assertEquals(0, changes.size());
    	
    }
    
}
