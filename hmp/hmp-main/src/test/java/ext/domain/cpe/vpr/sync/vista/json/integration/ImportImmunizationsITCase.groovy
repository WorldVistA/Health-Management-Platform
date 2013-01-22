package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration

import EXT.DOMAIN.cpe.test.junit4.runners.TestPatients
import EXT.DOMAIN.cpe.vpr.pom.hibernate.GenericHibMapDAO
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith

//import EXT.DOMAIN.cpe.vpr.sync.vista.rel.ImmunizationImporter
import EXT.DOMAIN.cpe.test.junit4.runners.VprExtract
import EXT.DOMAIN.cpe.test.junit4.runners.ImportTestSession
import EXT.DOMAIN.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import EXT.DOMAIN.cpe.test.junit4.runners.Importer
//import EXT.DOMAIN.cpe.vpr.sync.vista.rel.DocumentImporter
import EXT.DOMAIN.cpe.vpr.sync.vista.json.*
import EXT.DOMAIN.cpe.vpr.Document

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import EXT.DOMAIN.cpe.vpr.Immunization

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["129", "1"])
@VprExtract(domain = "immunization")
@Importer(ImmunizationImporter.class)
class ImportImmunizationsITCase extends AbstractImporterITCaseWithDB<Immunization> {

    ImportImmunizationsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testImmunizationImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }

}
