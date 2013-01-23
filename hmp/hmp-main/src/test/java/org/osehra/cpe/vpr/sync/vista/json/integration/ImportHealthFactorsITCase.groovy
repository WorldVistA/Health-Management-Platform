package org.osehra.cpe.vpr.sync.vista.json.integration

import org.junit.runner.RunWith;

import org.osehra.cpe.test.junit4.runners.ImportTestSession
import org.osehra.cpe.test.junit4.runners.Importer;
import org.osehra.cpe.test.junit4.runners.ImporterIntegrationTestRunner;
import org.osehra.cpe.test.junit4.runners.TestPatients
import org.osehra.cpe.test.junit4.runners.VprExtract
import org.osehra.cpe.vpr.HealthFactor
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.osehra.cpe.vpr.sync.vista.json.HealthFactorImporter
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
