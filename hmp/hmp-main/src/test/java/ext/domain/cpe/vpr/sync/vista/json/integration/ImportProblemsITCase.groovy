package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.vpr.Problem
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.ProblemImporter
import org.junit.Test
import org.junit.runner.RunWith
import EXT.DOMAIN.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["229", "100847"])
@VprExtract(domain = "problem")
@Importer(ProblemImporter.class)
class ImportProblemsITCase extends AbstractImporterITCaseWithDB<Problem> {

    ImportProblemsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testProblemImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
