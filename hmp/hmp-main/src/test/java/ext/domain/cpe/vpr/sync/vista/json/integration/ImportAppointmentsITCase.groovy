package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import org.junit.runner.RunWith;

import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.Importer;
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner;
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.vpr.Encounter
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.EncounterImporter
import org.junit.Test

import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["100846","100842"])
@VprExtract(domain = "appointment")
@Importer(EncounterImporter.class)
class ImportAppointmentsITCase extends AbstractImporterITCaseWithDB<Encounter> {
	
	ImportAppointmentsITCase(VistaDataChunk chunk){
		super(chunk)
	}
	
	@Test
	public void testAppointmentImporter() throws Exception {
		assertThat(getDomainInstance(),not(null))
        assertSave(getDomainInstance());
	}
}
