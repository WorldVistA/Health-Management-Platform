package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.VitalSign
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class VitalSignImporterTest extends AbstractImporterTest {

    @Test
    void testImport() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("vital.json"), mockPatient, 'vital')

        VitalSignImporter vi = new VitalSignImporter()

        VitalSign temperature = vi.convert(chunk)

        assertThat(temperature.facilityCode, equalTo("500D"))
        assertThat(temperature.facilityName, equalTo("SLC-FO HMP DEV"))
        assertThat(temperature.high, equalTo("102"))
        assertThat(temperature.kind, equalTo("Vital Sign"))
        assertThat(temperature.localId, equalTo("298"))
        assertThat(temperature.locationCode, equalTo("urn:va:location:121"))
        assertThat(temperature.locationName, equalTo("MIKE'S IP SUBSPECIALTY"))
        assertThat(temperature.low, equalTo("95"))
        assertThat(temperature.metricResult, equalTo("36.7"))
        assertThat(temperature.metricUnits, equalTo("C"))
        assertThat(temperature.observed, equalTo(new PointInTime(1999, 2, 26, 9, 22)))
        assertThat(temperature.result, equalTo("98"))
        assertThat(temperature.resulted, equalTo(new PointInTime(1999, 2, 26, 9, 22, 39)))
        assertThat(temperature.summary, equalTo("TEMPERATURE 98 F"))
        assertThat(temperature.typeCode, equalTo("urn:va:vuid:4500638"))
        assertThat(temperature.typeName, equalTo("TEMPERATURE"))
        assertThat(temperature.uid, equalTo("urn:va:F484:229:vs:298"))
        assertThat(temperature.units, equalTo("F"))
    }
}
