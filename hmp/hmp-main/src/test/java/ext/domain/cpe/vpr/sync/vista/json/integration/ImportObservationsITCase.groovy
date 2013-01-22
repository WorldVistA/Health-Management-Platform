package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat
import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.Importer
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.vpr.Observation
import EXT.DOMAIN.cpe.vpr.pom.hibernate.GenericHibMapDAO
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.ObservationImporter

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["100847"])
@VprExtract(domain = "observation")
@Importer(ObservationImporter.class)
class ImportObservationsITCase extends AbstractImporterITCaseWithDB<Observation> {
	
	ImportObservationsITCase(VistaDataChunk chunk){
		super(chunk)
	}
	
	@Test
	public void testAppointmentImporter() throws Exception {
		assertThat(getDomainInstance(),not(null))
		
		// save the patient into the dao
		GenericHibMapDAO dao = new GenericHibMapDAO(fact, true, false);
		Observation o = getDomainInstance();
		dao.save(o);
		
		// original JSON data for comparison
		Map<String, Object> data = getChunk().getJsonMap();
		
		// count the number of records
		List qualifiers = (List) data.get("observation_qualifier");
		
		// confirm same number of DB rows
		assertEquals(1, countTableRows("observation"));
		assertEquals(qualifiers == null ? 0 : qualifiers.size(), countTableRows("observation_qualifier"));
		
		// check that we can get the record back out
		o = dao.findByUID(Observation.class, (String) data.get("uid"));
		assertNotNull(o);
	}
}
