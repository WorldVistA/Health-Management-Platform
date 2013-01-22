package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration;

import EXT.DOMAIN.cpe.test.junit4.runners.*;
import EXT.DOMAIN.cpe.vpr.Procedure;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.ProcedureImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"711", "129"})
@VprExtract(domain = "consult")
@Importer(ProcedureImporter.class)
public class PersistConsultsITCase extends AbstractImporterITCaseWithDBPersisted<Procedure> {

    public PersistConsultsITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    void testConsultImporter() {
        assertSave(getDomainInstance());
    }

}
