package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import org.junit.runner.RunWith;

import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.Importer;
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner;
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.vpr.HealthFactor
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.HealthFactorImporter
import org.junit.Test

import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.nullValue

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["229","100846"])
@VprExtract(domain = "factor")
@Importer(HealthFactorImporter.class)
class ImportHealthFactorsITCase extends AbstractImporterITCaseWithDB<HealthFactor> {
	
	ImportHealthFactorsITCase(VistaDataChunk chunk){
		super(chunk)
	}
	
	@Test
	public void testAppointmentImporter() throws Exception {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
		
	}
}
