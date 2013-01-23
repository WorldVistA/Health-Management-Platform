package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.vpr.VitalSign
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.osehra.cpe.vpr.sync.vista.json.VitalSignImporter
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["224", "237"])
@VprExtract(domain = "vital")
@Importer(VitalSignImporter.class)
class ImportVitalSignITCase extends AbstractImporterITCase<VitalSign> {

    ImportVitalSignITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testVitalSignImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
    }
}
