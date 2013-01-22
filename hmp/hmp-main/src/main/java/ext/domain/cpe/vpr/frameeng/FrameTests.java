package EXT.DOMAIN.cpe.vpr.frameeng;

import static org.junit.Assert.*;
import EXT.DOMAIN.cpe.vpr.VitalSign;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrameTrigger.NewVitalSignTrigger;
import EXT.DOMAIN.cpe.vpr.pom.PatientEvent;

import org.junit.Test;

public class FrameTests {

	@Test
	public void testTrigger() {
		VitalSign vs = new VitalSign();
		vs.setData("typeCode", "HEIGHT");
		PatientEvent<VitalSign> e = new PatientEvent<VitalSign>(vs, PatientEvent.Type.CREATE, null);
		NewVitalSignTrigger trig = new NewVitalSignTrigger("HEIGHT");
		
		assertTrue(trig.eval(e));
	}
	
	@Test
	public void testCallEvent() {
		
	}
}
