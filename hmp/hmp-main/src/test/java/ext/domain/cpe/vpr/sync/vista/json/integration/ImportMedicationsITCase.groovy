package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.*
import org.junit.Test
import org.junit.runner.RunWith
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.test.junit4.runners.Importer

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import EXT.DOMAIN.cpe.vpr.Medication

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["1", "236", "8", "40"])
@VprExtract(domain = "pharmacy")
@Importer(MedicationImporter.class)
class ImportMedicationsITCase extends AbstractImporterITCaseWithDB<Medication> {

    ImportMedicationsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testMedicationImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
