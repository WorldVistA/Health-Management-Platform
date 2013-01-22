package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith

//import EXT.DOMAIN.cpe.vpr.sync.vista.rel.EncounterImporter
import EXT.DOMAIN.cpe.vpr.sync.vista.json.*
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import EXT.DOMAIN.cpe.test.junit4.runners.Importer

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import EXT.DOMAIN.cpe.vpr.VitalSignOrganizer
import EXT.DOMAIN.cpe.vpr.Encounter

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["237", "224"])
@VprExtract(domain = "visit")
@Importer(EncounterImporter.class)
class ImportVisitsITCase extends AbstractImporterITCaseWithDB<Encounter> {

    ImportVisitsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testEncounterImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
