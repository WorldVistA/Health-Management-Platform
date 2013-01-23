package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.test.junit4.runners.ImportTestSession
import org.osehra.cpe.test.junit4.runners.TestPatients
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.osehra.cpe.vpr.sync.vista.json.*
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import org.osehra.cpe.test.junit4.runners.VprExtract
import org.osehra.cpe.test.junit4.runners.Importer

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import org.osehra.cpe.vpr.Medication

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["1", "236", "8", "40"])
@VprExtract(domain = "pharmacy")
@Importer(MedicationImporter.class)
class ImportMedicationsITCase extends AbstractImporterITCaseWithDB<Medication> {

    ImportMedicationsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testMedicationImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
