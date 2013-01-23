package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.test.junit4.runners.ImportTestSession
import org.osehra.cpe.test.junit4.runners.TestPatients
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import org.junit.runner.RunWith

//import org.osehra.cpe.vpr.sync.vista.rel.EncounterImporter
import org.osehra.cpe.vpr.sync.vista.json.*
import org.osehra.cpe.test.junit4.runners.VprExtract
import org.osehra.cpe.test.junit4.runners.ImporterIntegrationTestRunner
import org.osehra.cpe.test.junit4.runners.Importer

import static org.junit.Assert.assertThat
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.CoreMatchers.nullValue
import org.osehra.cpe.vpr.VitalSignOrganizer
import org.osehra.cpe.vpr.Encounter

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["237", "224"])
@VprExtract(domain = "visit")
@Importer(EncounterImporter.class)
class ImportVisitsITCase extends AbstractImporterITCaseWithDB<Encounter> {

    ImportVisitsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testEncounterImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
