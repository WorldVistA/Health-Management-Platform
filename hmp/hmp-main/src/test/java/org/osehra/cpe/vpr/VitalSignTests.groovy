package org.osehra.cpe.vpr

import org.junit.Test
import static org.junit.Assert.assertEquals

class VitalSignTests {

    @Test
    void testSummaryWithoutInterpretation() {
        VitalSign s = new VitalSign(typeName: "WEIGHT", result:"182", units:"lb", metricResult: "82.73", metricUnits: "kg")
        assertEquals("WEIGHT 182 lb", s.summary)
   }

    @Test
    void testSummaryWithInterpretation() {
        VitalSign s = new VitalSign(typeName: "WEIGHT", result:"182", units:"lb", metricResult: "82.73", metricUnits: "kg", interpretationCode: "L")
        assertEquals("WEIGHT 182L lb", s.summary)
    }

    @Test
    void testSummaryWithoutUnits() {
        VitalSign s = new VitalSign(typeName: "BLOOD PRESSURE", result: "134/81")
        assertEquals("BLOOD PRESSURE 134/81", s.summary)
    }
	
    @Test
    void testSummaryWithoutType() {
    	VitalSign s = new VitalSign(result: "134/81")
    	assertEquals(" 134/81", s.summary)
    }
	
    @Test
    void testSummaryWithoutAnything() {
    	VitalSign s = new VitalSign()
    	assertEquals("", s.summary)
    }
}
