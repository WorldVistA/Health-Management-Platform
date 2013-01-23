package org.osehra.cpe.vpr

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

class PatientFacilityTests {

    Patient pt
	PatientFacility f1
	PatientFacility f2
	
    @Before
    void setUp() {
        pt = new Patient(id: 23)
		
		f1 = new PatientFacility()
		f1.patient = pt
		f1.code ='960'
		
		f2 = new PatientFacility()
		f2.patient = pt
		f2.code ='960'
    }

    @Test
    void testEquals() {
        assertTrue(f1.equals(f2))
        assertTrue(f2.equals(f1))
    }

    @Test
    void testHashcode() {
       assertTrue(f1.hashCode() == f2.hashCode())
    }

    @Test
    void testCompareTo() {
        assertEquals(0, f1.compareTo(f2))
        assertEquals(0, f2.compareTo(f1))

        f2.code = '961'
        assertEquals(-1, f1.compareTo(f2))
        assertEquals(1, f2.compareTo(f1))
    }
}
