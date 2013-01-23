package org.osehra.cpe.vpr.sync.vista

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.osehra.cpe.vista.rpc.ConnectionCallback
import org.osehra.cpe.vista.rpc.RpcOperations
import org.osehra.cpe.vista.rpc.RpcRequest
import org.osehra.cpe.vista.rpc.RpcResponse
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.PatientFacility
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.util.FileCopyUtils

import static org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants.*
import static junit.framework.Assert.assertSame
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItem
import static org.mockito.Mockito.*
import static org.hamcrest.CoreMatchers.is

class VistaPatientDataServiceTests {

    RpcOperations mockRpcTemplate

    IPatientDAO mockPatientDao

    VistaPatientDataService s

    @Before
    void setUp() {
        mockRpcTemplate = mock(RpcOperations.class)
        mockPatientDao = mock(IPatientDAO.class)

        s = new VistaPatientDataService()
        s.patientDao = mockPatientDao
        s.synchronizationRpcTemplate = mockRpcTemplate
    }

    static final String VERSION_RESULT_STRING = 'foo'

    @Test
    void testFetchVprVersion() {
        when(mockRpcTemplate.executeForString("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_DATA_VERSION}".toString())).thenReturn(VERSION_RESULT_STRING);

        String version = s.fetchVprVersion("foobar")
        assert VERSION_RESULT_STRING == version
    }

    static final String PATIENT_RESULT_STRING_JSON = '''
	{
	    "apiVersion": "1.01",
	    "data": {
	        "updated": "20120620144938-0700",
	        "totalItems": 1,
	        "items": [
	            {
	                "address": {
	                    "city": "Any Town",
	                    "state": "WEST VIRGINIA",
	                    "streets": {
	                        "street": [
	                            "Any Street"
	                        ]
	                    },
	                    "zip": "99998-0071"
	                },
	                "aliases": [
	                    {
	                        "alias": "P4"
	                    }
	                ],
	                "bid": "A0004",
	                "dob": 19350407,
	                "ethnicity": [
	                    "0000-0"
	                ],
	                "facilities": [
	                    {
	                        "code": 500,
	                        "name": "CAMP MASTER"
	                    }
	                ],
	                "familyName": "AVIVAPATIENT",
	                "flags": [
	                    {
	                        "name": "WANDERER",
	                        "text": "patient has a history of wandering off and getting lost"
	                    }
	                ],
	                "fullName": "AVIVAPATIENT,TWENTYFOUR",
	                "gender": {
	                    "name": "Male",
	                    "uid": "urn:va:pat-gender:M"
	                },
	                "givenNames": "TWENTYFOUR",
	                "icn": 10104,
	                "id": 229,
	                "maritalStatus": {
	                    "name": "Divorced",
	                    "uid": "urn:va:pat-maritalStatus:D"
	                },
	                "race": [
	                    "0000-0"
	                ],
	                "religion": {
	                    "name": "METHODIST",
	                    "uid": "urn:va:pat-religion:4"
	                },
	                "sensitive": 1,
	                "ssn": 666000004,
	                "support": [
	                    {
	                        "contactType": {
	                            "name": "Next of Kin",
	                            "person": "VETERAN,BROTHER",
	                            "uid": "urn:va:pat-contact:NOK"
	                        }
	                    }
	                ],
	                "telecomList": [
	                    {
	                        "contactUsage": {
	                            "name": "home address",
	                            "uid": "H"
	                        },
	                        "telecom": "(222)555-8235"
	                    },
	                    {
	                        "contactUsage": {
	                            "name": "work place",
	                            "uid": "WP"
	                        },
	                        "telecom": "(222)555-7720"
	                    }
	                ],
	                "uid": "urn:va:patient:F484:229",
	                "veteran": {
	                    "lrdfn": 177,
	                    "serviceConnected": 1,
	                    "serviceConnectedPercent": 10
	                }
	            }
	        ]
	    }
	}
	'''

    @Test
    void testFetchDemographicsWithDfn() {
        RpcResponse response = new RpcResponse(PATIENT_RESULT_STRING_JSON)
        response.setDivision("500")
        response.setDivisionName("CAMP MASTER")

        when(mockRpcTemplate.execute("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [patientId: '229', domain: 'patient'])).thenReturn(response)

        VistaDataChunk f = s.fetchPatientDemographicsWithDfn("foobar", "229")
        assert f
        assert f.systemId == "foobar"
        assert 0 == f.itemIndex
        assert 1 == f.itemCount
        assert f.patient == null
        assert f.localPatientId == "229"
        assert f.params[SyncMessageConstants.DIVISION] == "500"
        assert f.params[SyncMessageConstants.DIVISION_NAME] == "CAMP MASTER"
        assert f.domain.equals('patient');

        JsonNode patientElement = new ObjectMapper().readTree(PATIENT_RESULT_STRING_JSON).get("data").get("items").get(0);
        assertThat(f.json.toString(), equalTo(patientElement.toString()));
    }

    @Test
    void testFetchDemographicsWithIcn() {
        RpcResponse response = new RpcResponse(PATIENT_RESULT_STRING_JSON)
        response.setDivision("500")
        response.setDivisionName("CAMP MASTER")

        when(mockRpcTemplate.execute("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [patientId: ';10104', domain: 'patient'])).thenReturn(response)

        VistaDataChunk f = s.fetchPatientDemographicsWithIcn("foobar", "10104")
        assert f
        assert f.systemId == "foobar"
        assert 0 == f.itemIndex
        assert 1 == f.itemCount
        assert f.patient == null
        assert f.localPatientId == null // localPatientId is null when fetched with ICN, will pick up in PatientImporter
        assert f.params[SyncMessageConstants.DIVISION] == "500"
        assert f.params[SyncMessageConstants.DIVISION_NAME] == "CAMP MASTER"
        assert f.domain.equals('patient')

        JsonNode patientElement = new ObjectMapper().readTree(PATIENT_RESULT_STRING_JSON).get("data").get("items").get(0);
        assertThat(f.json.toString(), equalTo(patientElement.toString()));
    }

    static final String MOCK_JSON_RESULT_STRING = '''
{
    "apiVersion":"1",
    "data": {
        "items": [
            {
               "bar":1,
               "baz":false
            },
            {
               "bar":2,
               "baz":true
            }
        ]
    }
}
'''

    @Test
    void testFetchDomainChunks() {
        when(mockRpcTemplate.execute(eq("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString()), anyMap())).thenReturn(new RpcResponse(MOCK_JSON_RESULT_STRING))

        Patient mockPatient = new Patient(id: 42, icn: '12345')
        mockPatient.addToFacilities(new PatientFacility(code: 'moo', localPatientId: "229", systemId: "foobar"))

        String domain = "foo"
        List<VistaDataChunk> fragments = s.fetchDomainChunks("foobar", mockPatient, domain, false, null)

        verify(mockRpcTemplate).execute("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [patientId: ";12345", domain: domain, text: 0])

        assertThat(fragments.size(), equalTo(2))

        VistaDataChunk item = fragments[0]
        assertNotNull(item)
        assertThat(item.systemId, is('foobar'))
        assertSame mockPatient, item.patient
        assertThat(item.localPatientId, is('229'))
        assertThat(item.itemIndex, is(0))
        assertThat(item.itemCount, is(2))
        assertThat(item.content, is("{\"bar\":1,\"baz\":false}"))
        assertThat(item.domain, is("foo"))

        item = fragments[1]
        assertNotNull(item)
        assertThat(item.systemId, is('foobar'))
        assertSame mockPatient, item.patient
        assertThat(item.localPatientId, is('229'))
        assertThat(item.itemIndex, is(1))
        assertThat(item.itemCount, is(2))
        assertThat(item.content, is("{\"bar\":2,\"baz\":true}"))
        assertThat(item.domain, is("foo"))
    }

    @Test
    void testFetchUpdates() {
        RpcRequest mockUpdateRequest = new RpcRequest("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [domain: "new", id: "", text: "1", systemID: "baz"])
        mockUpdateRequest.setTimeout(3)

        String json = FileCopyUtils.copyToString(new InputStreamReader(getClass().getResourceAsStream("json/new.json"), "UTF-8"))
        when(mockRpcTemplate.execute(eq(mockUpdateRequest))).thenReturn(new RpcResponse(json))

        Patient mockPatient = MockPatientUtils.create()
        when(mockPatientDao.findByLocalID(anyString(), anyString())).thenReturn(mockPatient) // only needs to be non-null

        VprUpdateData data = s.fetchUpdates("foobar", "baz", "");

        verify(mockPatientDao).findByLocalID("foobar","8");
        verify(mockPatientDao).findByLocalID("foobar","205");
        verify(mockPatientDao).findByLocalID("foobar","229");
        verify(mockPatientDao).findByLocalID("foobar","231");
        verify(mockPatientDao).findByLocalID("foobar","237");

        assertThat(data.lastUpdate, equalTo("3120917:0"));

        assertThat(data.chunks.size(), is(53))

        assertThat(data.uidsToDelete, hasItem("urn:va:F484:237:hf:132"));
        assertThat(data.uidsToDelete, hasItem("urn:va:F484:229:hf:132"));
        assertThat(data.uidsToDelete, hasItem("urn:va:F484:229:obs:{05901567-F2BF-4ABA-B2A5-09D168C2CDE4}"));
        assertThat(data.uidsToDelete, hasItem("urn:va:F484:229:tiu:4343"));
        assertThat(data.uidsToDelete, hasItem("urn:va:F484:229:visit:7363"));
    }
	
	@Test
	public void testPatientNotInJDS() throws Exception {
		RpcRequest mockUpdateRequest = new RpcRequest("vrpcb://foobar/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [domain: "new", id: "", text: "1", systemID: "baz"])
		mockUpdateRequest.setTimeout(3)

		String json = FileCopyUtils.copyToString(new InputStreamReader(getClass().getResourceAsStream("json/new.json"), "UTF-8"))
		when(mockRpcTemplate.execute(eq(mockUpdateRequest))).thenReturn(new RpcResponse(json))

		Patient mockPatient = null
		when(mockPatientDao.findByLocalID(anyString(), anyString())).thenReturn(mockPatient)

		VprUpdateData data = s.fetchUpdates("foobar", "baz", "");
		assertThat(data.exceptions.size, is(0))
	}
}
