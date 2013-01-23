package org.osehra.cpe.vpr;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.osehra.cpe.datetime.PointInTime;

import java.util.Set;

import org.joda.time.Period;
import org.junit.Test;

public class PatientTests {

    @Test
    public void testToString() {
        Patient pt = new Patient();
        pt.setData("pid", "42");
        pt.setData("icn", "12345");

        PatientFacility home = new PatientFacility();
        home.setData("code", "960");
        home.setData("name","FOO");
        home.setData("homeSite", true);
        home.setData("localPatientId","229");
        home.setData("systemId","9F06");
        pt.addToFacilities(home);

        PatientFacility facility = new PatientFacility();
        facility.setData("code","961");
        facility.setData("name","BAR");
        facility.setData("localPatientId","301");
        facility.setData("systemId","8636");
        pt.addToFacilities(facility);

        assertThat(pt.toString(), is("org.osehra.cpe.vpr.Patient{pids=[12345, 42, 8636;301, 960;229, 961;301, 9F06;229]}"));
    }

    @Test
    public void testHomeFacility() {
        PatientFacility home = new PatientFacility();
        home.setData("code","960");
        home.setData("name","FOO");
        home.setData("homeSite", true);
        Patient pt = new Patient();
        pt.setData("icn","12345");
        pt.addToFacilities(home);
        PatientFacility facility = new PatientFacility();
        facility.setData("code","961");
        facility.setData("name","BAR");
        pt.addToFacilities(facility);
        assertSame(home, pt.getHomeFacility());
    }

    @Test
    public void testAge() {
        Patient pt = new Patient();
        pt.setData("dateOfBirth",new PointInTime(1975, 7, 23));
        PointInTime today = PointInTime.today();
        int age = new Period(pt.getDateOfBirth(), today).getYears();
        assertEquals(age, pt.getAge().intValue());
    }

    @Test
    public void testAgeWhenDead() {
        Patient pt = new Patient();
        pt.setData("dateOfBirth",new PointInTime(1975, 7, 23));
        pt.setData("died",new PointInTime(1984, 3, 11));
        assertEquals(8, pt.getAge().intValue());
    }

    @Test
    public void testPatientIds() {
        Patient pt = new Patient();
        pt.setData("icn","12345");
        PatientFacility home = new PatientFacility();
        home.setData("code","960");
        home.setData("name","FOO");
        home.setData("homeSite",true);
        home.setData("systemId","9F06");
        home.setData("localPatientId","229");

        pt.addToFacilities(home);
        PatientFacility facility = new PatientFacility();
        facility.setData("code","961");
        facility.setData("name","BAR");
        facility.setData("localPatientId","301");
        facility.setData("systemId","8636");
        pt.addToFacilities(facility);

        Set<String> ptIds = pt.getPatientIds();
        assertThat(ptIds.size(), is(5));
        assertTrue(ptIds.contains("12345"));
        assertTrue(ptIds.contains("960;229"));
        assertTrue(ptIds.contains("961;301"));
        assertTrue(ptIds.contains("9F06;229"));
        assertTrue(ptIds.contains("8636;301"));
    }

    @Test
    public void testPatientIdsWithNullIcn() {
        Patient pt = new Patient();
        PatientFacility home = new PatientFacility();
        home.setData("code","960");
        home.setData("name","FOO");
        home.setData("homeSite",true);
        home.setData("localPatientId","229");
        home.setData("systemId","9F06");
        pt.addToFacilities(home);

        PatientFacility facility = new PatientFacility();
        facility.setData("code","961");
        facility.setData("name","BAR");
        facility.setData("localPatientId","301");
        facility.setData("systemId","8636");
        pt.addToFacilities(facility);//(code: '961', name: 'BAR')

        Set<String> ptIds = pt.getPatientIds();
        assertThat(ptIds.size(), is(4));
        assertTrue(ptIds.contains("960;229"));
        assertTrue(ptIds.contains("961;301"));
        assertTrue(ptIds.contains("9F06;229"));
        assertTrue(ptIds.contains("8636;301"));
    }

    @Test
    public void testGetLocalPatientIdForSystem() {
        Patient pt = createFacilityLadenPatient();

        assertEquals("229", pt.getLocalPatientIdForSystem("9F06"));
        assertEquals("301", pt.getLocalPatientIdForSystem("3663"));
        assertNull(pt.getLocalPatientIdForSystem("ABCD"));
    }

    @Test
    public void testGetLocalPatientIdForFacility() {
        Patient pt = createFacilityLadenPatient();

        assertEquals("229", pt.getLocalPatientIdForFacility("960"));
        assertEquals("229", pt.getLocalPatientIdForFacility("961"));
        assertEquals("301", pt.getLocalPatientIdForFacility("500"));
        assertNull(pt.getLocalPatientIdForFacility("971"));
    }

    private Patient createFacilityLadenPatient() {
        Patient pt = new Patient();

        PatientFacility facility1 = new PatientFacility();
        facility1.setData("code","960");
        facility1.setData("name","FOO");
        facility1.setData("systemId","9F06");
        facility1.setData("localPatientId","229");
        pt.addToFacilities(facility1);

        PatientFacility facility2 = new PatientFacility();
        facility2.setData("code","961");
        facility2.setData("name","BAR");
        facility2.setData("localPatientId","229");
        facility2.setData("systemId","9F06");
        pt.addToFacilities(facility2);

        PatientFacility facility3 = new PatientFacility();
        facility3.setData("code","500");
        facility3.setData("name","BAZ");
        facility3.setData("localPatientId","301");
        facility3.setData("systemId","3663");
        pt.addToFacilities(facility3);

        return pt;
    }
}
