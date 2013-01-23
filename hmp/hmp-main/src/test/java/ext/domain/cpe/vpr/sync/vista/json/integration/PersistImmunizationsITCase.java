package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.test.junit4.runners.*;
import org.osehra.cpe.vpr.Immunization;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.osehra.cpe.vpr.sync.vista.json.ImmunizationImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"129", "1"})
@VprExtract(domain = "immunization")
@Importer(ImmunizationImporter.class)
public class PersistImmunizationsITCase extends AbstractImporterITCaseWithDBPersisted<Immunization> {

    public PersistImmunizationsITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    void testImmunizationImporter() {
        assertSave(getDomainInstance());
    }

}
