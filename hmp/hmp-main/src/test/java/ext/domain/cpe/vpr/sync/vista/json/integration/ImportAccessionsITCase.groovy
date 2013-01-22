package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients

import org.junit.runner.RunWith

import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner

import EXT.DOMAIN.cpe.test.junit4.runners.Importer
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.*
import org.junit.Test

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import EXT.DOMAIN.cpe.vpr.ResultOrganizer

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["129", "1"])
@VprExtract(domain = "accession")
@Importer(AccessionImporter.class)
class ImportAccessionsITCase extends AbstractImporterITCaseWithDB<ResultOrganizer> {

    ImportAccessionsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testAccessionImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
