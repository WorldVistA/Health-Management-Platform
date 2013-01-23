package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.test.junit4.runners.TestPatients
import org.osehra.cpe.vpr.pom.hibernate.GenericHibMapDAO
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith

//import org.osehra.cpe.vpr.sync.vista.rel.ImmunizationImporter
import org.osehra.cpe.test.junit4.runners.VprExtract
import org.osehra.cpe.test.junit4.runners.ImportTestSession
import org.osehra.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import org.osehra.cpe.test.junit4.runners.Importer
//import org.osehra.cpe.vpr.sync.vista.rel.DocumentImporter
import org.osehra.cpe.vpr.sync.vista.json.*
import org.osehra.cpe.vpr.Document

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import org.osehra.cpe.vpr.Immunization

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
