package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.vpr.Result
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.ResultImporter
import org.junit.Test
import org.junit.runner.RunWith
import EXT.DOMAIN.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["129", "1"])
@VprExtract(domain = "lab")
@Importer(ResultImporter.class)
class ImportLabsITCase extends AbstractImporterITCase<Result> {

    ImportLabsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testAccessionImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
    }
}
