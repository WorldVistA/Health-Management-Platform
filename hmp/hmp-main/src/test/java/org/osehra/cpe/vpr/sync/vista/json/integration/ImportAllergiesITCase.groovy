package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.vpr.Allergy
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.osehra.cpe.vpr.sync.vista.json.*
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat


@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["229", "100846"])
@VprExtract(domain = "allergy")
@Importer(AllergyImporter.class)
class ImportAllergiesITCase extends AbstractImporterITCaseWithDB<Allergy> {

    ImportAllergiesITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testAllergyImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
