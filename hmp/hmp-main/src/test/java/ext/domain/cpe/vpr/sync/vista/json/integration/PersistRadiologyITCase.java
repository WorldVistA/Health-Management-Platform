package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.test.junit4.runners.*;
import org.osehra.cpe.vpr.Procedure;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.osehra.cpe.vpr.sync.vista.json.ProcedureImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"711", "129"})
@VprExtract(domain = "rad")
@Importer(ProcedureImporter.class)
public class PersistRadiologyITCase extends AbstractImporterITCaseWithDBPersisted<Procedure> {

    public PersistRadiologyITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    public void testConsultImporter() {
        assertSave(getDomainInstance());
    }

}
