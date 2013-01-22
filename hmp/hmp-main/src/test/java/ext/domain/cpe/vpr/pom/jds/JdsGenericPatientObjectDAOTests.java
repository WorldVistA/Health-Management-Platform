package EXT.DOMAIN.cpe.vpr.pom.jds;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import EXT.DOMAIN.cpe.vpr.Allergy;
import EXT.DOMAIN.cpe.vpr.Document;
import EXT.DOMAIN.cpe.vpr.Encounter;
import EXT.DOMAIN.cpe.vpr.HealthFactor;
import EXT.DOMAIN.cpe.vpr.Medication;
import EXT.DOMAIN.cpe.vpr.Order;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.Problem;
import EXT.DOMAIN.cpe.vpr.Result;
import EXT.DOMAIN.cpe.vpr.VitalSign;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;

public class JdsGenericPatientObjectDAOTests {

    private static final String MOCK_MED_UID = "urn:va:F484:100845:medication:33571";
    private static final String MOCK_PID = "34";
    private JdsOperations mockJdsTemplate;
    private JdsGenericPatientObjectDAO dao;
    private Patient mockPt;

    @Before
    public void setUp() throws Exception {
        mockPt = new Patient();
        mockPt.setData("pid", "34");
        mockPt.setData("uid", "urn:va:ABCD:229:patient:229");

        mockJdsTemplate = mock(JdsOperations.class);

        dao = new JdsGenericPatientObjectDAO();
        dao.setJdsTemplate(mockJdsTemplate);
    }

    @Test
    public void testSavePatient() throws Exception {
        when(mockJdsTemplate.postForLocation(eq("/vpr"), any(HttpEntity.class))).thenReturn(URI.create("/vpr/34/urn:va:ABCD:229:patient:229"));

        Patient pt = new Patient();
        pt.setData("uid", "urn:va:ABCD:229:patient:229");
        pt.setData("familyName", "Bar");
        pt.setData("givenNames", "Foo");
//        pt.setDateOfBirth(new PointInTime(1934, 11, 11));

        dao.save(pt);
        assertThat(pt.getPid(), equalTo("34"));

        ArgumentCaptor<Patient> ptArg = ArgumentCaptor.forClass(Patient.class);
        verify(mockJdsTemplate).postForLocation(eq("/vpr"), ptArg.capture());

        assertThat(ptArg.getValue(), sameInstance(pt));
    }

    @Test
    public void testFindPatientByUID() {
        when(mockJdsTemplate.getForObject("/vpr/uid/urn:va:ABCD:34:pat:34", Patient.class)).thenReturn(mockPt);

        Patient pt = dao.findByUID(Patient.class, "urn:va:ABCD:34:pat:34");
        assertThat(pt, sameInstance(mockPt));

        verify(mockJdsTemplate).getForObject("/vpr/uid/urn:va:ABCD:34:pat:34", Patient.class);
    }

    @Test
    public void testDeletePatientByPID() {
        dao.deleteByPID(Patient.class, MOCK_PID);

        verify(mockJdsTemplate).delete("/vpr/" + MOCK_PID);
    }

    @Test
    public void testSave() {
        Medication mockMed = new Medication();
        mockMed.setData("pid", MOCK_PID);
        mockMed.setData("uid", MOCK_MED_UID);

        dao.save(mockMed);

        ArgumentCaptor<Medication> medArg = ArgumentCaptor.forClass(Medication.class);
        verify(mockJdsTemplate).postForLocation(eq("/vpr/" + MOCK_PID), medArg.capture());

        assertThat(medArg.getValue(), sameInstance(mockMed));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveWithMissingPid() {
        Medication mockMed = new Medication();
        mockMed.setData("uid", MOCK_MED_UID);

        dao.save(mockMed);
    }

    @Test
    public void testFindMedicationByUID() {
        Medication mockMed = new Medication();
        mockMed.setData("pid", MOCK_PID);
        mockMed.setData("uid", MOCK_MED_UID);
        when(mockJdsTemplate.getForObject("/vpr/uid/" + MOCK_MED_UID, Medication.class)).thenReturn(mockMed);

        Medication m = dao.findByUID(Medication.class, MOCK_MED_UID);

        assertThat(m, sameInstance(mockMed));

        verify(mockJdsTemplate).getForObject("/vpr/uid/" + MOCK_MED_UID, Medication.class);
    }

    @Test
    public void testCountByPID() {
        JsonCCollection<Map<String, Object>> jsonc = JsonCCollection.create(getClass().getResourceAsStream("count-domains.json"));

        when(mockJdsTemplate.getForJsonC("/vpr/" + MOCK_PID + "/count/domain")).thenReturn(jsonc);

        assertThat(dao.countByPID(Allergy.class, MOCK_PID), is(2));
        assertThat(dao.countByPID(Document.class, MOCK_PID), is(18));
        assertThat(dao.countByPID(Encounter.class, MOCK_PID), is(45));
        assertThat(dao.countByPID(HealthFactor.class, MOCK_PID), is(2));
        assertThat(dao.countByPID(Result.class, MOCK_PID), is(337));
        assertThat(dao.countByPID(Medication.class, MOCK_PID), is(20));
        assertThat(dao.countByPID(Order.class, MOCK_PID), is(199));
        assertThat(dao.countByPID(Problem.class, MOCK_PID), is(6));
        assertThat(dao.countByPID(Result.class, MOCK_PID), is(337));
        assertThat(dao.countByPID(VitalSign.class, MOCK_PID), is(177));

        verify(mockJdsTemplate, times(10)).getForJsonC("/vpr/" + MOCK_PID + "/count/domain");
    }

    @Test
    public void testDeleteByUID() {
        dao.deleteByUID(Medication.class, MOCK_MED_UID);

        verify(mockJdsTemplate).delete("/vpr/uid/" + MOCK_MED_UID);
    }

    @Test
    public void testDelete() {
        Medication mockMed = new Medication();
        mockMed.setData("pid", MOCK_PID);
        mockMed.setData("uid", MOCK_MED_UID);

        dao.delete(mockMed);

        verify(mockJdsTemplate).delete("/vpr/" + MOCK_PID + "/" + MOCK_MED_UID);
    }
    
    @Test
    public void testFindAllByIndex() {
        JsonCCollection<Map<String, Object>> jsonc = JsonCCollection.create(getClass().getResourceAsStream("result-summary.json"));
        when(mockJdsTemplate.getForJsonC(eq("/vpr/" + MOCK_PID + "/index/lab-type/summary?range=GLUCOSE&start=0&limit=100"), anyMap())).thenReturn(jsonc);
        List<Result>  result =  dao.findAllByIndex(Result.class, MOCK_PID,"lab-type/summary","GLUCOSE", null, null);
        assertThat(result.size(),is(2));
    	verify(mockJdsTemplate).getForJsonC(eq("/vpr/" + MOCK_PID + "/index/lab-type/summary?range=GLUCOSE&start=0&limit=100"), anyMap());
    
    }
    
    @Test
    public void testFindAllByQuery() {
    	QueryDef qry = new QueryDef();
    	PageRequest page = new PageRequest(0,100);
		qry.namedIndexRange("result", String.valueOf(page.getOffset()), String.valueOf(page.getPageSize()));
    	qry.skip(page.getOffset());
    	qry.limit(page.getPageSize());

		
		JsonCCollection<Map<String, Object>> jsonc = JsonCCollection.create(getClass().getResourceAsStream("result-summary.json"));
		when(mockJdsTemplate.getForJsonC(eq("/vpr/" + MOCK_PID + "/index/result?range=0..100&start=0&limit=100"), anyMap())).thenReturn(jsonc);
		Map params = new HashMap();
		params.put("pid", MOCK_PID);
		List<Result>  result = dao.findAllByQuery(Result.class, qry, params);	
        assertThat(result.size(),is(2));
    }
   
    @Test
    public void testFindAllByPID() {
    	
		JsonCCollection<Map<String, Object>> jsonc = JsonCCollection.create(getClass().getResourceAsStream("result-summary.json"));
		when(mockJdsTemplate.getForJsonC(eq("/vpr/" + MOCK_PID + "/index/result"), anyMap())).thenReturn(jsonc);
		
		PageRequest page = new PageRequest(0,100);
		Page result = dao.findAllByPID(Result.class, MOCK_PID, page);	
        assertThat(result.getContent().size(),is(2));

    }

}
