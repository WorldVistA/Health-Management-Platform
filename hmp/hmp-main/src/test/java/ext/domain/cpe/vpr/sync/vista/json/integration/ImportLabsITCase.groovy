package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.osehra.cpe.vpr.sync.vista.json.ResultImporter
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.*

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
