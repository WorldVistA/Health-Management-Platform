package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.vpr.pom.hibernate.GenericHibMapDAO
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat
import org.osehra.cpe.vpr.Procedure
import org.osehra.cpe.vpr.sync.vista.json.*

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["711", "129"])
@VprExtract(domain = "surgery")
@Importer(ProcedureImporter.class)
class ImportSurgeriesITCase extends AbstractImporterITCaseWithDB<Procedure> {

    ImportSurgeriesITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testSurgeryImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }

}
