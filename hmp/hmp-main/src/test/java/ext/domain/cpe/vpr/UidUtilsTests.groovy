package EXT.DOMAIN.cpe.vpr

import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame

class UidUtilsTests {

    @Test
    void testGetDomainClassByUid() {
        assertSame(Allergy, UidUtils.getDomainClassByUid("urn:va:9F06:229:art:354"))
        assertSame(Procedure, UidUtils.getDomainClassByUid("urn:va:9F06:229:cons:354"))
        assertSame(Document, UidUtils.getDomainClassByUid("urn:va:9F06:229:tiu:354"))
        assertSame(Encounter, UidUtils.getDomainClassByUid("urn:va:9F06:229:visit:354"))
        assertSame(Encounter, UidUtils.getDomainClassByUid("urn:va:9F06:229:appt:354"))
        assertSame(HealthFactor, UidUtils.getDomainClassByUid("urn:va:9F06:229:hf:354"))
        assertSame(Immunization, UidUtils.getDomainClassByUid("urn:va:9F06:229:imm:354"))
        assertSame(Medication, UidUtils.getDomainClassByUid("urn:va:6273:229:med:orderID9527"))
        assertSame(Medication, UidUtils.getDomainClassByUid("urn:va:6273:229:med:354"))
        assertSame(Observation, UidUtils.getDomainClassByUid("urn:va:9F06:229:obs:354"))
        assertSame(Order, UidUtils.getDomainClassByUid("urn:va:9F06:229:order:354"))
        assertSame(Patient, UidUtils.getDomainClassByUid("urn:va:9F06:229:pat"))
        assertSame(Problem, UidUtils.getDomainClassByUid("urn:va:9F06:229:prob:354"))
        assertSame(Procedure, UidUtils.getDomainClassByUid("urn:va:9F06:229:proc:354"))
        assertSame(Procedure, UidUtils.getDomainClassByUid("urn:va:9F06:229:rad:354"))
        assertSame(Result, UidUtils.getDomainClassByUid("urn:va:6273:229:lab:CH;6889297.92;2"))
        assertSame(VitalSignOrganizer, UidUtils.getDomainClassByUid("urn:va:6273:229:vs:23:20070508135624.000"))
        assertSame(VitalSign, UidUtils.getDomainClassByUid("urn:va:6273:229:vs:32536"))
    }

    @Test
    void testGetUid() {
        assertEquals("urn:va:9F06:229:art:354", UidUtils.getAllergyUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:appt:354", UidUtils.getAppointmentUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:cons:354", UidUtils.getConsultUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:tiu:354", UidUtils.getDocumentUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:hf:354", UidUtils.getHealthFactorUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:imm:354", UidUtils.getImmunizationUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:med:354", UidUtils.getMedicationUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:order:354", UidUtils.getOrderUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:pat:229", UidUtils.getPatientUid("9F06", "229"))
        assertEquals("urn:va:9F06:229:prob:354", UidUtils.getProblemUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:proc:354", UidUtils.getProcedureUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:rad:354", UidUtils.getRadiologyUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:lab:CH;6889297.92;2", UidUtils.getResultUid("9F06", "229", "CH;6889297.92;2"))
        assertEquals("urn:va:9F06:229:lab:354", UidUtils.getResultOrganizerUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:visit:354", UidUtils.getVisitUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:vs:354", UidUtils.getVitalSignUid("9F06", "229", "354"))
        assertEquals("urn:va:9F06:229:vs:42:20120229", UidUtils.getVitalSignOrganizerUid("9F06", "229", "42", "20120229"))

        assertEquals("urn:va:user:9F06:12345", UidUtils.getUserUid("9F06", "12345"))
    }
}
