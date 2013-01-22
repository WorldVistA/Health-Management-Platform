package EXT.DOMAIN.cpe.vpr

import org.junit.Test

import static org.junit.Assert.assertEquals

class MedicationTests {
    @Test
    void testSummary() {
        // FIXME: implement summary include all product names/concentrations and sig
        Medication m = new Medication([productFormName: "FOO", sig: "Foo1"])

        assertEquals("FOO Foo1", m.summary)
    }

    @Test
    void testKind() {
        Medication m = new Medication([vaType: "O"])
        assertEquals("Medication, Outpatient", m.kind)

        m = new Medication([vaType: "I"])
        assertEquals("Medication, Inpatient", m.kind)

        m = new Medication([vaType: "N"])
        assertEquals("Medication, Non-VA", m.kind)

        m = new Medication([vaType: "V"])
        assertEquals("Infusion", m.kind)
    }
}
