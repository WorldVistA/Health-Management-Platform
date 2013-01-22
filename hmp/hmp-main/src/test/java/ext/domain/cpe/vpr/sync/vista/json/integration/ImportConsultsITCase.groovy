package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.vpr.pom.hibernate.GenericHibMapDAO
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith
import EXT.DOMAIN.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat
import EXT.DOMAIN.cpe.vpr.Document
import EXT.DOMAIN.cpe.vpr.Procedure
import EXT.DOMAIN.cpe.vpr.sync.vista.json.*

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["711", "129"])
@VprExtract(domain = "consult")
@Importer(ProcedureImporter.class)
class ImportConsultsITCase extends AbstractImporterITCaseWithDB<Procedure> {

    ImportConsultsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testConsultImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
		assertSave(getDomainInstance());
    }

}
