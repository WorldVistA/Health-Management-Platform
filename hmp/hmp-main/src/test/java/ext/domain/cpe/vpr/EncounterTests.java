package EXT.DOMAIN.cpe.vpr;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncounterTests {

    @Test
    public void testGetPrimaryProviderOnlyProviders() {
        Encounter e = new Encounter();

        EncounterProvider provider = new EncounterProvider();
        Clinician clinician = new Clinician();
        clinician.setName("FOO");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(false);
        e.addToProviders(provider);

        provider = new EncounterProvider();
        clinician = new Clinician();
        clinician.setName("BAR");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(true);
        e.addToProviders(provider);

        provider = new EncounterProvider();
        clinician = new Clinician();
        clinician.setName("BAZ");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(false);
        e.addToProviders(provider);

        EncounterProvider p = e.getPrimaryProvider();
        assertNotNull(p);
        assertEquals("BAR", p.getProviderName());
    }

    @Test
    public void testGetPrimaryProviderPrimaryProviderAndProviders() {
        Encounter e = new Encounter();

        EncounterProvider provider = new EncounterProvider();
        Clinician clinician = new Clinician();
        clinician.setName("FOO");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());

        provider.setPrimary(false);
        e.addToProviders(provider);

        provider = new EncounterProvider();
        clinician = new Clinician();
        clinician.setName("BAR");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(false);
//		provider.setPrimary(true); // Otherwise last test for primary could potentially fail?
        e.addToProviders(provider);

        provider = new EncounterProvider();
        clinician = new Clinician();
        clinician.setName("BAZ");
        clinician.setUid("urn:va:F484:user:600");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(false);
        e.addToProviders(provider);

        EncounterProvider pProvider = new EncounterProvider();
        clinician = new Clinician();
        clinician.setName("BAZ");
        clinician.setUid("urn:va:F484:user:600");
        pProvider.setData("providerName", clinician.getName());
        pProvider.setData("providerUid",clinician.getUid());
        pProvider.setPrimary(true);
        //provider.setPrimary(false);
        e.addToProviders(pProvider);

        EncounterProvider p = e.getPrimaryProvider();
        assertNotNull(p);
        assertEquals("BAZ", p.getProviderName());
    }

    @Test
    public void testGetPrimaryProvider() {
        Encounter e = new Encounter();

        EncounterProvider provider = new EncounterProvider();
        Clinician clinician = new Clinician();
        clinician.setName("BAZ");
        provider.setData("providerName", clinician.getName());
        provider.setData("providerUid",clinician.getUid());
        provider.setPrimary(true);
//		e.setPrimaryProvider(provider);
        e.addToProviders(provider);

        EncounterProvider p = e.getPrimaryProvider();
        assertNotNull(p);
        assertEquals("BAZ", p.getProviderName());
    }

    @Test
    public void testSummary() {
        Encounter e = new Encounter();
        e.setData("localId", "sdgfsddf");
        e.setData("service", "foo");
        e.setData("location", "bar");
        e.setData("summary", "foo: bar");
        assertEquals("foo: bar", e.getSummary());
    }

    @Test
    public void testSummaryWithNullService() {
        Encounter e = new Encounter();
        e.setData("localId", "sdgfsddf");
        e.setData("location", "bar");
        e.setData("summary", "bar");
        assertEquals("bar", e.getSummary());
    }

    @Test
    public void testAppointmentKind() {
        Encounter e = new Encounter();
        e.setData("categoryCode", "AP");
        e.setData("categoryName", "Future Appointment");
        assertEquals("Appointment", e.getKind());
    }

    @Test
    public void testVisitKind() {
        Encounter e = new Encounter();
        e.setData("categoryCode", "OV");
        e.setData("categoryName", "Outpatient Visit");
        assertEquals("Visit", e.getKind());

        e.setData("categoryCode", "TC");
        e.setData("categoryName", "Phone Contact");

        assertEquals("Visit", e.getKind());
    }

    @Test
    public void testAdmissionKind() {
        Encounter e = new Encounter();
        e.setData("categoryCode", "AD");
        e.setData("categoryName", "Admission");
        assertEquals("Admission", e.getKind());

        e = new Encounter();
        e.setData("categoryCode", "NH");
        e.setData("categoryName", "Nursing Home");
        assertEquals("Admission", e.getKind());
    }

    @Test
    public void testUnknownKind() {
        Encounter e = new Encounter();
        e.setData("categoryCode", "AA");
        e.setData("categoryName", "Foo");
        assertEquals("Unknown", e.getKind());
    }
}
