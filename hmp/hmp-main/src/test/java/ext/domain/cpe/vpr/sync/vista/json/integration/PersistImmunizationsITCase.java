package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration;

import EXT.DOMAIN.cpe.test.junit4.runners.*;
import EXT.DOMAIN.cpe.vpr.Immunization;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.ImmunizationImporter;
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
