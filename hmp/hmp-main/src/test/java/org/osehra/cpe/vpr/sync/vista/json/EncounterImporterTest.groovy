package org.osehra.cpe.vpr.sync.vista.json

import org.junit.Test
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks

import org.osehra.cpe.vpr.Encounter
import org.osehra.cpe.vpr.EncounterProvider

import org.osehra.cpe.vpr.UidUtils

import org.osehra.cpe.datetime.PointInTime

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*

class EncounterImporterTest extends AbstractImporterTest {

	String[] uids = [
		"urn:va:F484:229:document:4191",
		"urn:va:F484:229:document:4232",
		"urn:va:F484:229:document:4236",
		"urn:va:F484:229:document:4248",
		"urn:va:F484:229:document:4277",
		"urn:va:F484:229:document:4297",
		"urn:va:F484:229:document:4308",
		"urn:va:F484:229:document:4309",
		"urn:va:F484:229:document:4310",
		"urn:va:F484:229:document:4311"
	];

    @Test
    void testConvert() {
        EncounterImporter importer = new EncounterImporter()
		Encounter a = importer.convert(MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("visit.json"), mockPatient, "visit"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)));

        assertThat(a.uid, is(equalTo(UidUtils.getVisitUid(MockVistaDataChunks.VISTA_ID, "229", "7193"))))
        assertThat(a.localId, is(equalTo("7193")))
		assertThat(a.categoryName, is(equalTo("Admission")));
		assertThat(a.categoryCode, is(equalTo("AD")));
		assertThat(a.dateTime, is(new PointInTime(2011,07,01,10,00)));
		assertThat(a.documentUids.size(), is(10));
		ArrayList<String> iter = new ArrayList<String>();
		for(String s: uids)
		{
			iter.add(s);
		}
		for(Map<String, Object> mp: a.documentUids)
		{
			assertThat(iter, hasItem(mp.get("uid")));
		}

        assertThat(a.facilityName, is(equalTo("SLC-FO HMP DEV")))
        //assertThat(a.location, is(equalTo("7A GEN MED")))
        assertThat(a.patientClassCode, is(equalTo("IMP")))
        assertThat(a.localId, is(equalTo("7193")))
        assertThat(a.stay.arrivalDateTime, is(new PointInTime(2011,07,01,10,00)))
        assertThat(a.stay.dischargeDateTime, is(new PointInTime(2011,07,02,10,00)))
		for(EncounterProvider ep: a.providers) {
			assertThat(ep.uid, is(equalTo(ep.role.equals("A")?"urn:va:user:F484:20006":"urn:va:user:F484:20001")))
			assertThat(ep.primary, is(equalTo(ep.role.equals("A")?null:true)))
		}
        assertThat(a.reason, is(equalTo(null)))
		assertThat(a.roomBed, is(equalTo("")))
		assertThat(a.service, is(equalTo("MEDICINE")))
		assertThat(a.specialty, is(equalTo("CARDIOLOGY")))
		assertThat(a.summary, is(equalTo("7A GEN MED")))
		assertThat(a.typeName, is(equalTo("HOSPITALIZATION")))
    }
	/**
	 * Removed primaryProvider field in class; caused errors on importer.convert line for some reason.
	 * Removed ${MEDICINE} from JSON response on summary field; Is this intended to be in the M RPC response?
	 */
}
