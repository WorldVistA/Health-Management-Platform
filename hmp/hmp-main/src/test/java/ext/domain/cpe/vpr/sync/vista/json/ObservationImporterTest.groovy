package EXT.DOMAIN.cpe.vpr.sync.vista.json


import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.vpr.Observation
import EXT.DOMAIN.cpe.vpr.PatientFacility
import EXT.DOMAIN.cpe.vpr.UidUtils
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import static org.junit.Assert.*

import org.junit.Test

class ObservationImporterTest extends AbstractImporterTest{
	@Test
	public void testCreate() throws Exception {
		mockPatient.addToFacilities(new PatientFacility(systemId: 'F484', localPatientId: '100842'))
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("observation.json"),mockPatient,'observation')
		fragment.localPatientId = '100847'
		fragment.systemId = '{F7A04600-1F7E-4DC7-B71C-136647E76C8A}'

		ObservationImporter importer = new ObservationImporter()
		Observation observation = importer.convert(fragment);
		assertNotNull(observation)
		assertEquals(MOCK_PID, observation.getPid());
		assertEquals("500D",observation.facilityCode)
		assertEquals("SLC-FO HMP DEV",observation.facilityName)
		assertEquals("urn:va:location:500D:5",observation.locationCode)
		assertEquals("3 NORTH SURG",observation.locationName)
		assertEquals("500D",observation.facilityCode)
		assertEquals("SLC-FO HMP DEV",observation.facilityName)
		assertEquals(UidUtils.getObservationUid("F484", fragment.localPatientId, fragment.systemId),observation.uid)
		assertEquals("{F7A04600-1F7E-4DC7-B71C-136647E76C8A}",observation.localId)
		assertEquals("Clinical Observation",observation.kind)
		assertEquals(null,observation.typeCode)
		assertEquals("OUTPUT - EMESIS",observation.typeName)
		assertEquals("1000",observation.result)
		assertEquals("ml",observation.units)
		assertEquals("N",observation.interpretation)
		assertEquals( new PointInTime(2011,11,18,13,17), observation.observed)
		assertNull(observation.resulted)
		assertEquals("complete",observation.resultStatus)
		assertEquals(null,observation.methodCode)
		assertEquals(null,observation.methodName)
		assertEquals(null,observation.bodySiteCode)
		assertEquals(null,observation.bodySiteName)
	
		assertEquals("",observation.comment)
		assertEquals(null,observation.vaStatus)
		assertEquals(null,observation.qualifierText)
		
	}
	
	@Test
	public void testConvertWithQualifiers() throws Exception {
		def json = '''
	{
	    "bodySiteCode":4500642,
	    "bodySiteName":"ORAL",
	    "comment":"",
	    "entered":20120629161729,
	    "facilityCode":"500D",
	    "facilityName":"SLC-FO HMP DEV",
	    "interpretation":"H",
	    "localId":"{D4953826-902D-4722-9520-BE1916BF739B}",
	    "locationCode":"urn:va:location:500D:56",
	    "locationName":"5TH FLOOR",
	    "observed":201206291614,
	    "qualifiers":[
	        {
	            "code":4688634,
	            "name":"ACTUAL",
	            "type":"quality"
	        }
	    ],
	    "resultStatus":"complete",
	    "typeCode":"urn:vuid:4500638",
	    "typeName":"TEMPERATURE",
	    "uid":"urn:va:F484:231:obs:{D4953826-902D-4722-9520-BE1916BF739B}",
	    "units":"F",
	    "value":102.5,
	    "vuid":4500638
	}
	'''
		mockPatient.addToFacilities(new PatientFacility(systemId: 'F484', localPatientId: '100842'))
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(json,mockPatient,'observation')
		fragment.localPatientId = '231'
		fragment.systemId = '{D4953826-902D-4722-9520-BE1916BF739B}'

		ObservationImporter importer = new ObservationImporter()
		Observation observation = importer.convert(fragment);
		assertNotNull(observation)
		assertEquals(MOCK_PID,observation.getPid());
		assertEquals("500D",observation.facilityCode)
		assertEquals("SLC-FO HMP DEV",observation.facilityName)
		assertEquals("urn:va:location:500D:56",observation.locationCode)
		assertEquals("5TH FLOOR",observation.locationName)
		assertEquals("500D",observation.facilityCode)
		assertEquals("SLC-FO HMP DEV",observation.facilityName)
		assertEquals(UidUtils.getObservationUid("F484", fragment.localPatientId, fragment.systemId),observation.uid)
		assertEquals("{D4953826-902D-4722-9520-BE1916BF739B}",observation.localId)
		assertEquals("Clinical Observation",observation.kind)
		assertEquals('urn:vuid:4500638',observation.typeCode)
		assertEquals("TEMPERATURE",observation.typeName)
		assertEquals(null,observation.result)
		assertEquals("F",observation.units)
		assertEquals("H",observation.interpretation)
		assertEquals( new PointInTime(2012,6,29,16,14), observation.observed)
		assertNull(observation.resulted)
		assertEquals("complete",observation.resultStatus)
		assertEquals(null,observation.methodCode)
		assertEquals(null,observation.methodName)
		assertEquals('4500642',observation.bodySiteCode)
		assertEquals('ORAL',observation.bodySiteName)

		assertEquals("",observation.comment)
		assertEquals(null,observation.vaStatus)
		assertEquals('quality: ACTUAL',observation.qualifierText)

		assertEquals(1, observation.qualifiers.size());
		assertEquals("4688634", observation.qualifiers.iterator().next().code);
		assertEquals("ACTUAL", observation.qualifiers.iterator().next().name);
		assertEquals("quality", observation.qualifiers.iterator().next().type);
	}

}
