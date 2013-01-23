package org.osehra.cpe.vpr.ws.link;

import java.util.Date;

import org.osehra.cpe.HmpProperties;
import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.feed.atom.Link;
import org.osehra.cpe.vpr.*;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenInfoButtonLinkGeneratorTests {

    static final String BASE_URL = "https://cfmdcisrv05.duhs.duke.edu/infobutton-service/infoRequest";

    private String MOCK_PID = "42";
    private Patient MOCK_PATIENT;
    private OpenInfoButtonLinkGenerator generator;
    private Medication medication;
    private Problem problem;
    private Result result;

    private IPatientDAO mockPatientDao;
    private Environment mockEnvironment;

    @Before
    public void setUp() {
        MOCK_PATIENT = new Patient();
//        MOCK_PATIENT.setData("dateOfBirth", new PointInTime(1969, 7, 20));
        MOCK_PATIENT.setData("dateOfBirth", new PointInTime(PointInTime.today().subtractYears(42)));
        MOCK_PATIENT.setData("genderCode","M");

        mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getProperty(HmpProperties.INFO_BUTTON_URL)).thenReturn(BASE_URL);

        mockPatientDao = mock(IPatientDAO.class);

        generator = new OpenInfoButtonLinkGenerator();
        generator.setPatientDao(mockPatientDao);
        generator.setEnvironment(mockEnvironment);

        when(mockPatientDao.findByVprPid(MOCK_PID)).thenReturn(MOCK_PATIENT);
    }

    @Test
    public void testSupports() {
        assertTrue(generator.supports(new Medication()));
        assertTrue(generator.supports(new Result()));
        assertTrue(generator.supports(new Problem()));
        assertFalse(generator.supports(new Document()));
    }

    @Test
    public void testGenerateLinkForMedication() {
        //Medication medication = new Medication(qualifiedName: "SIMVASTATIN", patient: MOCK_PATIENT);
        medication = new Medication();
        medication.setData("qualifiedName", "SIMVASTATIN");
        medication.setData("pid", MOCK_PID);
        Link link = generator.generateLink(medication);
        assertTrue(LinkRelation.OPEN_INFO_BUTTON.toString().equals(link.getRel()));
        assertEquals((BASE_URL + "?representedOrganization.id.root=1.3.6.1.4.1.3768&patientPerson.genderCode=M&age.v.v=42&age.v.u=a&taskContext.c.c=MLREV&mainSearchCriteria.v.dn=SIMVASTATIN&performer=PROV".toString()),link.getHref());
    }

    @Test
    public void testGenerateLinkForProblem() {
        problem = new Problem();
        problem.setData("problemText", "FOOBAR");
        problem.setData("pid", MOCK_PID);
        Link link = generator.generateLink(problem);
        assertTrue(LinkRelation.OPEN_INFO_BUTTON.toString().equals(link.getRel()));
        assertTrue((BASE_URL + "?representedOrganization.id.root=1.3.6.1.4.1.3768&patientPerson.genderCode=M&age.v.v=42&age.v.u=a&taskContext.c.c=PROBLISTREV&mainSearchCriteria.v.dn=FOOBAR&performer=PROV".toString()).equals(link.getHref()));
    }

    @Test
    public void testGenerateLinkForResult() {
        result = new Result();
        result.setData("typeName", "GLUCOSE");
        result.setData("pid", MOCK_PID);
        ResultOrganizer organizer = new ResultOrganizer();
        organizer.setData("pid", MOCK_PID);
        result.addToOrganizers(organizer);
        Link link = generator.generateLink(result);
        assertTrue(LinkRelation.OPEN_INFO_BUTTON.toString().equals(link.getRel()));
        assertTrue((BASE_URL + "?representedOrganization.id.root=1.3.6.1.4.1.3768&patientPerson.genderCode=M&age.v.v=42&age.v.u=a&taskContext.c.c=LABRREV&mainSearchCriteria.v.dn=GLUCOSE&performer=PROV".toString()).equals(link.getHref()));
    }
}
