package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Before
import org.junit.Test
import org.osehra.cpe.vpr.*

import static org.junit.Assert.*
import org.osehra.cpe.vista.util.VistaStringUtils

class PatientImporterTest extends AbstractImporterTest{

    static final patient_test = '''
'''

	@Test
	public void testConvert() throws Exception {
		VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("patient.json"),mockPatient, "patient")
		PatientImporter pi = new PatientImporter()
		Patient p = pi.convert(chunk)
		
		assertNotNull(p);
		assertEquals(p.getUid(), UidUtils.getPatientUid(chunk.getSystemId(), chunk.getLocalPatientId()));
		assertEquals("10104", p.getIcn());
		assertEquals("AVIVAPATIENT", p.getFamilyName());
		assertEquals("TWENTYFOUR", p.getGivenNames());
		assertEquals("A0004", p.getBriefId());
		assertEquals("666000004", p.getSsn());
		assertFalse(p.isSensitive());
		assertEquals(new PointInTime(1935, 4, 7), p.getDateOfBirth());
		
		assertEquals("Male", p.getGenderName());
		assertEquals("urn:va:pat-gender:M", p.getGenderCode());
		// TODO: test for religion (needs code translation)

		assertNotNull(p.getVeteran());
		assertEquals(177, p.getVeteran().getLrdfn().intValue());
		assertEquals("true", p.getVeteran().getServiceConnected());
		assertEquals("10", p.getVeteran().getServiceConnectionPercent());

		assertEquals(1, p.getAddresses().size());
		Address address = p.getAddresses().iterator().next();
		assertEquals("Any Street", address.getStreetLine1());
		assertEquals("Any Town", address.getCity());
		assertEquals("WEST VIRGINIA", address.getStateProvince());
		assertEquals("99998-0071", address.getPostalCode());

		assertEquals(1, p.getFlags().size());
		PatientFlag flag = p.getFlags().iterator().next();
		assertEquals("WANDERER", flag.getName());
		assertEquals("patient has a history of wandering off and getting lost", flag.getText());

		assertEquals(1, p.getMaritalStatuses().size());
		PatientMaritalStatus ms = p.getMaritalStatuses().iterator().next();
		assertEquals("urn:va:pat-maritalStatus:D", ms.getCode());
		assertEquals("Divorced", ms.getName());
		assertNull(ms.getFromDate());
		assertNull(ms.getThruDate());

		assertEquals(1, p.getAliases().size());
		Alias alias = p.getAliases().iterator().next();
		assertEquals("P4", alias.getFullName());
		assertNull(alias.getFamilyName());
		assertNull(alias.getGivenNames());

		assertEquals(2, p.getTelecoms().size());
		Set<Telecom> telecoms = p.getTelecoms();
		for (Telecom telecom : telecoms) {
			if (telecom.getUsageCode().equals("H") ){
				assertEquals("(222)555-8235", telecom.getTelecom());
			} else if (telecom.getUsageCode().equals("WP")) {
				assertEquals("(222)555-7720", telecom.getTelecom());
			} else {
				fail();
			}
		}
		assertEquals(1, p.getFacilities().size()); // .facilities.size()
		SortedSet<PatientFacility> facilities = p.getFacilities();
		PatientFacility facility = facilities.first();
        assertEquals("500", facility.getCode());
        assertEquals("CAMP MASTER", facility.getName());
        assertEquals(chunk.getSystemId(), facility.getSystemId());
        assertEquals(chunk.getLocalPatientId(), facility.getLocalPatientId());
        assertFalse(facility.isHomeSite());

		 assertEquals(6, p.getExposures().size());
		// assertEquals("urn:va:N", p.getExposures().iterator().next().getUid());

		assertEquals(1, p.getSupports().size());
		PatientSupport support = p.getSupports().iterator().next();
		assertEquals("urn:va:pat-contact:NOK", support.getContactTypeCode());
		assertEquals("Next of Kin", support.getContactTypeName());
		assertEquals("VETERAN,BROTHER", support.getName());

	}
}
