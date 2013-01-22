package EXT.DOMAIN.cpe.vpr.pom.jds;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import EXT.DOMAIN.cpe.vpr.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientJdsDAOTests {

    private static final String JDS_URL = "http://localhost:9080";
    private static final String MOCK_PID = "23";
    private static final String MOCK_ICN = "123123123";
    private static final String MOCK_QDFN = "F484;321";

    private JdsOperations mockJdsTemplate;
    private JdsGenericPatientObjectDAO mockGenericDao;
    private JdsPatientDAO dao;

    @Before
    public void setUp() throws Exception {
        mockJdsTemplate = mock(JdsOperations.class);
        mockGenericDao = mock(JdsGenericPatientObjectDAO.class);

        dao = new JdsPatientDAO();
        dao.setJdsTemplate(mockJdsTemplate);
        dao.setGenericDao(mockGenericDao);
    }

    @Test
    public void testSave() throws Exception {
        Patient pt = new Patient();
        pt.setData("uid", "urn:va:ABCD:229:patient");
        pt.setData("familyName", "Bar");
        pt.setData("givenNames", "Foo");
        pt.setData("dateOfBirth", new PointInTime(1934, 11, 11));

        Patient returnPt = dao.save(pt);

        assertThat(returnPt, sameInstance(pt));
        verify(mockGenericDao).save(pt);
    }

    @Test
    public void testFindByIcn() {
        Patient mockPatient = new Patient();
        mockPatient.setData("pid", MOCK_PID);
        mockPatient.setData("icn", MOCK_ICN);

        when(mockJdsTemplate.getForObject("/vpr/pid/" + MOCK_ICN, Patient.class)).thenReturn(mockPatient);

        Patient pt = dao.findByIcn(MOCK_ICN);
        assertThat(pt, sameInstance(mockPatient));

        verify(mockJdsTemplate).getForObject("/vpr/pid/" + MOCK_ICN, Patient.class);
    }

    @Test
    public void testFindByVprPid() {
        Patient mockPatient = new Patient();
        mockPatient.setData("pid", MOCK_PID);
        mockPatient.setData("icn", MOCK_ICN);

        when(mockJdsTemplate.getForObject("/vpr/" + MOCK_PID, Patient.class)).thenReturn(mockPatient);

        Patient pt = dao.findByVprPid(MOCK_PID);
        assertThat(pt, sameInstance(mockPatient));

        verify(mockJdsTemplate).getForObject("/vpr/" + MOCK_PID, Patient.class);
    }

    @Test
    public void testFindByVprPidNotFound() {
        when(mockJdsTemplate.getForObject("/vpr/" + MOCK_PID, Patient.class)).thenReturn(null);

        Patient pt = dao.findByVprPid(MOCK_PID);
        assertThat(pt, nullValue());

        verify(mockJdsTemplate).getForObject("/vpr/" + MOCK_PID, Patient.class);
    }

    @Test
    public void testFindByAnyPidWithQualifiedDfn() {
        Patient mockPatient = new Patient();
        mockPatient.setData("pid", MOCK_PID);

        when(mockJdsTemplate.getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class)).thenReturn(mockPatient);

        Patient pt = dao.findByAnyPid(MOCK_QDFN);
        assertThat(pt, sameInstance(mockPatient));

        verify(mockJdsTemplate).getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class);
    }

    @Test
    public void testFindByAnyPidWithQualifiedDfnNotFound() {
        when(mockJdsTemplate.getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class)).thenReturn(null);

        Patient pt = dao.findByAnyPid(MOCK_QDFN);
        assertThat(pt, nullValue());

        verify(mockJdsTemplate).getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class);
    }

    @Test
    public void testFindByLocalIdWithVistaIdAndDfn() throws Exception {
        Patient mockPatient = new Patient();
        mockPatient.setData("pid", MOCK_PID);

        when(mockJdsTemplate.getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class)).thenReturn(mockPatient);

        Patient pt = dao.findByLocalID("F484","321");
        assertThat(pt, sameInstance(mockPatient));

        verify(mockJdsTemplate).getForObject("/vpr/pid/" + MOCK_QDFN, Patient.class);
    }

    @Test
    public void testCount() {
        Map<String, Object> topicCount = new HashMap<String, Object>();
        topicCount.put("topic", "patient");
        topicCount.put("count", 34);
        JsonCCollection<Map<String, Object>> mockResponse = JsonCCollection.create(Collections.singletonList(topicCount));
        when(mockJdsTemplate.getForJsonC("/vpr/all/count/patient")).thenReturn(mockResponse);

        assertThat(dao.count(), is(equalTo(34)));
    }
    
    @Test
    public void testFindAll() throws Exception {
        Map<String, Object> patientIds = new HashMap<String, Object>();
        String json = "{\"apiVersion\":\"1.0\",\"data\":{\"updated\":20120731161036,\"totalItems\":2,\"items\":[12,22]}}";
        JsonNode mockResponse = new ObjectMapper().readTree(json);
        when(mockJdsTemplate.getForJsonNode("/vpr/all/index/pid/pid")).thenReturn(mockResponse);
        
		Patient p1 = new Patient();
	    p1.setData("pid", "12");
	    Patient p2 = new Patient();
	    p2.setData("pid", "22");
	    
		when(mockJdsTemplate.getForObject("/vpr/12",Patient.class)).thenReturn(p1);
		when(mockJdsTemplate.getForObject("/vpr/22",Patient.class)).thenReturn(p2);
		Page<Patient> expected = dao.findAll(new PageRequest(0,5));
		assertThat(expected.getTotalElements(),is(equalTo(2L)));
	}
    
}
