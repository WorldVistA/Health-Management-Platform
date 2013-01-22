package EXT.DOMAIN.cpe.vpr.sync.vista.json

import static org.junit.Assert.*;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.PatientFacility
import EXT.DOMAIN.cpe.vpr.Problem;
import EXT.DOMAIN.cpe.vpr.UidUtils;
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.mockito.Mockito.anyString
import static org.mockito.Matchers.anyString

class ProblemImporterTest extends AbstractImporterTest {

    ProblemImporter importer

    @Override
    void setUp() {
        super.setUp();

        importer = new ProblemImporter()
    }

    @Test
	public void testConvert() throws Exception {
		mockPatient.addToFacilities(new PatientFacility(systemId: 'F484', localPatientId: '229'))
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("problem.json"), mockPatient,'problem')
		Problem problem = importer.convert(fragment);
		
		assertNotNull(problem);
        assertThat(problem.getPid(), is(equalTo(MOCK_PID)))
		assertEquals(UidUtils.getProblemUid(fragment.systemId, fragment.localPatientId, '923'), problem.getUid())
		assertEquals("CAMP MASTER", problem.facilityName)
		assertEquals("500", problem.facilityCode)
		assertEquals("Diabetes Mellitus Type II or unspecified",problem.problemText);
		assertEquals(new PointInTime(2011, 3, 23), problem.getEntered());
		assertEquals(new PointInTime(2005), problem.getOnset());
		assertEquals(new PointInTime(2012, 5, 10), problem.getUpdated());
		assertEquals("250.0", problem.icdName);
		assertEquals("urn:icd:250.00", problem.icdCode);
		assertEquals("GENERAL MEDICINE", problem.locationName);
		assertEquals("23", problem.locationCode);
		assertNull(problem.service);
		assertEquals(UidUtils.getUserUid("F484", "983"), problem.providerCode);
		assertEquals("PROVIDER,ONE", problem.providerName);
		assertTrue(!Boolean.TRUE.equals(problem.getRemoved()));
		assertTrue(!Boolean.TRUE.equals(problem.getServiceConnected()));
		assertEquals("urn:va:sct:55561003", problem.statusCode);
		assertEquals("ACTIVE", problem.statusName);
		assertFalse(problem.unverified);
		assertEquals(2, problem.comments.size());
		assertEquals(new PointInTime(2012, 5, 10), problem.comments[0].entered);
		assertEquals("AVIVAUSER,SEVEN", problem.comments[0].enteredByName);
		assertEquals("urn:va:user:F484:1091", problem.comments[0].enteredByCode);
		assertEquals("Adequate control on oral medications.", problem.comments[0].comment);
		assertEquals(new PointInTime(2012, 5, 10), problem.comments[1].entered);
		assertEquals("AVIVAUSER,SEVEN", problem.comments[1].enteredByName);
		assertEquals("urn:va:user:F484:1091", problem.comments[1].enteredByCode);
		assertEquals("Goal to lose 50 pounds in 1 year.", problem.comments[1].comment);

	}
}
