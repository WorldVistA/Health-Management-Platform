package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.test.junit4.runners.*;
import org.osehra.cpe.vpr.Document;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.osehra.cpe.vpr.sync.vista.json.DocumentImporter;
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
