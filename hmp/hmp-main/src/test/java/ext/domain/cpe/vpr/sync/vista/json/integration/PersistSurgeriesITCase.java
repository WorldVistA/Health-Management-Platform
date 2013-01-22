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
@VprExtract(domain = "surgery")
@Importer(ProcedureImporter.class)
public class PersistSurgeriesITCase extends AbstractImporterITCaseWithDB<Procedure> {

    public PersistSurgeriesITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    public void testSurgeryImporter() {
        assertSave(getDomainInstance());
    }

}
