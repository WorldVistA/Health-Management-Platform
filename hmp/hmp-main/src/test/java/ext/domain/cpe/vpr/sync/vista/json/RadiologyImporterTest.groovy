package EXT.DOMAIN.cpe.vpr.sync.vista.json

import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.vpr.Procedure
import EXT.DOMAIN.cpe.vpr.UidUtils
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

class RadiologyImporterTest extends AbstractImporterTest {

    @Test
    void testConvert() {
        ProcedureImporter importer = new ProcedureImporter()

        Procedure a = importer.convert(MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("rad.json"), mockPatient, "rad"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)))

        assertThat(a.uid, is(equalTo(UidUtils.getRadiologyUid(MockVistaDataChunks.VISTA_ID, "100847", "6888880.8869-1"))))
        assertThat(a.category, is(equalTo("RA")))
        assertThat(a.dateTime, is(new PointInTime(2011, 11, 19, 11, 30)))
        assertThat(a.encounterUid, is(equalTo(UidUtils.getVisitUid(MockVistaDataChunks.VISTA_ID, "100847", "7289"))))

        assertThat(a.facilityName, is(equalTo("SLC-FO HMP DEV")))
        assertThat(a.facilityCode, is(equalTo("SLC")))
        assertThat(a.imageLocation, is(equalTo("RADIOLOGY MAIN FLOOR")))
        assertThat(a.hasImages, is(false))
        assertThat(a.imagingTypeUid, is(equalTo("urn:va:imaging-Type:GENERAL RADIOLOGY")))
        assertThat(a.kind, is(equalTo("Radiology")))
        assertThat(a.localId, is(equalTo("6888880.8869-1")))
        assertThat(a.locationUid, is(equalTo("urn:va:location:40")))
        assertThat(a.orderUid, is(equalTo(UidUtils.getOrderUid(MockVistaDataChunks.VISTA_ID, "100847", "34937"))))
        assertThat(a.providers.size(), is(1));
        assertThat(a.providers.iterator().next().uid, is(equalTo(UidUtils.getUserUid(MockVistaDataChunks.VISTA_ID, "1122"))))
        assertThat(a.results.size(), is(1));
        assertThat(a.results.iterator().next().uid, is(equalTo(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "100847", "6888880.8869-1"))))
        assertThat(a.status, is(equalTo("COMPLETE")))
        assertThat(a.summary, is(equalTo("RADIOLOGIC EXAMINATION, CHEST, 2 VIEWS, FRONTAL AND LATERAL;")))
        assertThat(a.typeName, is(equalTo("RADIOLOGIC EXAMINATION, CHEST, 2 VIEWS, FRONTAL AND LATERAL;")))
        assertThat(a.verified, is(true));

    }
}
