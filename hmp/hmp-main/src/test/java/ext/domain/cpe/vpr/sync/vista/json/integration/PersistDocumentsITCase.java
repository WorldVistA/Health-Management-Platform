package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration;

import EXT.DOMAIN.cpe.test.junit4.runners.*;
import EXT.DOMAIN.cpe.vpr.Document;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.DocumentImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"711", "129"})
@VprExtract(domain = "document")
@Importer(DocumentImporter.class)
public class PersistDocumentsITCase extends AbstractImporterITCaseWithDBPersisted<Document> {

    public PersistDocumentsITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    public void testDocumentImporter() {
        assertSave(getDomainInstance());
    }
}
