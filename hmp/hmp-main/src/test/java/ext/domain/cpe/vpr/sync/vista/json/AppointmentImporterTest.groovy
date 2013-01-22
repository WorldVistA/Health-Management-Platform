package EXT.DOMAIN.cpe.vpr.sync.vista.json

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.Encounter
import EXT.DOMAIN.cpe.vpr.PatientFacility;
import EXT.DOMAIN.cpe.vpr.UidUtils;
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import static org.junit.Assert.*

import org.junit.Test

import static org.hamcrest.CoreMatchers.*

class AppointmentImporterTest extends AbstractImporterTest {

    @Test
    public void testConvert() throws Exception {
        mockPatient.addToFacilities(new PatientFacility(systemId: 'F484', localPatientId: '100842'))

        VistaDataChunk fragment = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("appointment.json"), mockPatient, 'appointment')
        fragment.localPatientId = '100842'
        fragment.systemId = 'F484'

        EncounterImporter importer = new EncounterImporter()
        Encounter encounter = importer.convert(fragment);

        assertNotNull(encounter);

        assertEquals(MOCK_PID, encounter.getPid());
        assertEquals(UidUtils.getAppointmentUid(fragment.systemId, fragment.localPatientId, 'A;3120727.12;195'), encounter.getUid());

        assertEquals("CAMP MASTER", encounter.facilityName);
        assertEquals("500", encounter.facilityCode);

        assertEquals(new PointInTime(2012, 7, 27, 12, 0), encounter.getDateTime());

        assertEquals("SCHEDULED/KEPT", encounter.appointmentStatus);
        assertEquals("Outpatient Visit", encounter.categoryName);
        assertEquals("urn:va:encounter-category:OV", encounter.categoryCode);
        assertEquals("A;3120727.12;195", encounter.localId);
        assertEquals("AMB", encounter.patientClassCode);
        assertEquals("Ambulatory", encounter.patientClassName);
        assertEquals("MEDICINE", encounter.service);
        assertEquals("303", encounter.stopCode);
        assertEquals("CARDIOLOGY", encounter.stopCodeName);
        assertEquals("9", encounter.typeCode);
        assertEquals("REGULAR", encounter.typeName);
        assertEquals(1, encounter.providers.size());
        assertEquals("urn:va:user:F484:11256", encounter.providers.iterator().next().providerUid);
        assertEquals("WARDCLERK,FIFTYEIGHT", encounter.providers.iterator().next().providerName);
		assertEquals("", encounter.locationName);
		assertEquals("urn:va:location:500:0", encounter.locationUid);
    }
}
