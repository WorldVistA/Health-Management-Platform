package EXT.DOMAIN.cpe.vpr.sync.vista.json

import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.vpr.HealthFactor
import EXT.DOMAIN.cpe.vpr.UidUtils
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks;
import org.junit.Test
import static org.junit.Assert.*
import static org.hamcrest.CoreMatchers.*;

class HealthFactorImporterTest extends AbstractImporterTest {
	
	@Test
	public void testConvert() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("healthFactor.json"),mockPatient,'factor')
        fragment.localPatientId = '100846'
        fragment.systemId = '112'

		HealthFactorImporter importer = new HealthFactorImporter()
		HealthFactor hf = importer.convert(fragment);
		assertNotNull(hf)
		assertEquals(MOCK_PID,hf.getPid())
		assertEquals("THESE ARE COMMENTS FOR A HEALTH FACTOR", hf.comment);
		assertEquals(UidUtils.getHealthFactorUid("F484", fragment.localPatientId, fragment.systemId), hf.getUid())
		assertEquals("CAMP MASTER",hf.facilityName)
		assertEquals("500",hf.facilityCode)
		assertEquals ("Health Factor", hf.kind)
		assertEquals ("112",hf.localId)
		assertEquals("PREVIOUS SMOKER", hf.name)
		assertEquals(new PointInTime(2010,6,4,13,0), hf.recorded)
		//assertEquals("",hf.severity)
		assertEquals("PREVIOUS SMOKER", hf.summary)
		assertEquals(UidUtils.getVisitUid("F484", fragment.localPatientId, '7142'), hf.encounterUid) 
	}
}
