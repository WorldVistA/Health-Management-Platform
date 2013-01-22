package EXT.DOMAIN.cpe.vpr.pom.hibernate;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.test.junit4.runners.Importer;
import EXT.DOMAIN.cpe.vpr.*;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.PatientImporter;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.integration.AbstractImporterITCaseWithDB;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Importer(value = PatientImporter.class) // not used
public class HibMapTests extends AbstractImporterITCaseWithDB<IPatientObject> {

    public HibMapTests() {
        super(null); // not used
    }

    @Ignore
    @Test
    public void testPatientObjects() {
        // create a patient and a DAO to test with
        PatientHibMapDAO dao = new PatientHibMapDAO(fact);
        InputStream json = Patient.class.getResourceAsStream("sync/vista/json/patient.json");
        Patient p = POMUtils.newInstance(Patient.class, json);
        dao.save(p);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("patient"));
        assertEquals(1, countTableRows("patient_address"));
        assertEquals(1, countTableRows("patient_facility"));
        assertEquals(1, countTableRows("patient_alias"));
        assertEquals(2, countTableRows("patient_telecom"));

        // check the index records were stored
        assertEquals(2, countTableRows("vpr_index"));
        Map<String, Object> idx1 = getTableRow("vpr_index", 1);
        Map<String, Object> idx2 = getTableRow("vpr_index", 2);
        assertEquals(0, idx1.get("IDX"));
        assertEquals("patient-ids", idx1.get("INDEX_NAME"));
        assertEquals("10104", idx1.get("INDEX_VALUE"));
        assertEquals("1", idx1.get("PID"));
        assertEquals("urn:va:6273:229:patient:229", idx1.get("UID"));

        assertEquals(1, idx2.get("IDX"));
        assertEquals("patient-ids", idx2.get("INDEX_NAME"));
        assertEquals("666000004", idx2.get("INDEX_VALUE"));
        assertEquals("1", idx2.get("PID"));
        assertEquals("urn:va:6273:229:patient:229", idx2.get("UID"));


        // ensure the data is stored as intended
        Map<String, Object> row = getTableRow("patient", 1);

        // test DB values
        // TODO: JSON, domain_updated, last_updated
        assertEquals("1", row.get("PID"));
        assertEquals("666000004", row.get("SSN"));
        assertEquals("urn:va:6273:229:patient:229", row.get("UID"));
        assertEquals("TWENTYFOUR", row.get("GIVEN_NAMES"));
        assertEquals("AVIVAPATIENT", row.get("FAMILY_NAME"));
        assertEquals("19350407", row.get("BORN"));
        assertNull(row.get("DIED"));
        assertEquals("10104", row.get("ICN"));
        assertEquals("urn:va:pat-gender:M", row.get("GENDER_CODE"));
        assertEquals("Male", row.get("GENDER_NAME"));
        assertEquals("urn:va:pat-religion:4", row.get("RELIGION_CODE"));
        assertEquals("METHODIST", row.get("RELIGION_NAME"));

        // test addresses table
        row = getTableRow("patient_address", 1);
        assertEquals("1", row.get("PID"));
        assertEquals("Any Street", row.get("STREET_LINE1"));
        assertNull(row.get("STREET_LINE2"));
        assertEquals("Any Town", row.get("CITY"));
        assertEquals("WEST VIRGINIA", row.get("STATE_PROVINCE"));
        assertEquals("99998-0071", row.get("POSTAL_CODE"));
        assertNull(row.get("COUNTRY"));

        // test all the different getter mechanisms
        p = dao.findByIcn("10104");
        assertNotNull(p);
        assertEquals("urn:va:6273:229:patient:229", p.getUid());

        p = dao.findByUID("urn:va:6273:229:patient:229");
        assertNotNull(p);
        assertEquals("10104", p.getIcn());

        /* facility info not complete
          p = dao.findByPid("960;229");
          assertNotNull(p);
          assertEquals("urn:va:6273:229:patient:229", p.getUid());

          p = dao.findByPid("9F06;229");
          assertNotNull(p);
          assertEquals("urn:va:6273:229:patient:229", p.getUid());

          p = dao.findByLocalID("960", "229");
          assertEquals("TWENTYFOUR", p.getGivenNames());
          assertEquals("AVIVAPATIENT", p.getFamilyName());

          p = dao.findByLocalID("9F06", "229");
          assertEquals("TWENTYFOUR", p.getGivenNames());
          assertEquals("AVIVAPATIENT", p.getFamilyName());
          */

        p = dao.findByAnyPid("10104");
        assertEquals("TWENTYFOUR", p.getGivenNames());
        assertEquals("AVIVAPATIENT", p.getFamilyName());

        p = dao.findByVprPid("1");
        assertEquals("TWENTYFOUR", p.getGivenNames());
        assertEquals("AVIVAPATIENT", p.getFamilyName());

        p = dao.findByAnyPid("1");
        assertEquals("TWENTYFOUR", p.getGivenNames());
        assertEquals("AVIVAPATIENT", p.getFamilyName());
        assertEquals("1", p.getPid());

        // test the index queries
        List<Patient> list = dao.findAllByIndex("1", "patient-ids", "10104", null, null);
        assertEquals(1, list.size());

        list = dao.findAllByIndex(null, "patient-ids", "10104", null, null);
        assertEquals(1, list.size());

        Map filters = new HashMap();
        filters.put("givenNames", "TWENTYFOUR");
        list = dao.findAllByIndex("1", "patient-ids", "10104", null, filters);
    }

    @Test
    public void testPatientObjectUpdate() {
        // create a patient and a DAO to test with
        PatientHibMapDAO dao = new PatientHibMapDAO(fact);

        InputStream json = Patient.class.getResourceAsStream("sync/vista/json/patient.json");
        Patient p = POMUtils.newInstance(Patient.class, json);
        dao.save(p);

        p.setLastUpdated(PointInTime.now());
        dao.save(p);
    }

    @Test
    public void testPatientObjectFindAndUpdate() {
        // create a patient and a DAO to test with
        PatientHibMapDAO dao = new PatientHibMapDAO(fact);

        InputStream json = Patient.class.getResourceAsStream("sync/vista/json/patient.json");
        Patient p = POMUtils.newInstance(Patient.class, json);
        dao.save(p);

        String pid = p.getPid();

        p = dao.findByVprPid(pid);
        p.setLastUpdated(PointInTime.now());
        dao.save(p);

        assertEquals(1, countTableRows("patient"));
    }

    @Test
    public void testProcedureObjects() {
        genericDao.save(POMUtils.newInstance(Procedure.class, Procedure.class.getResourceAsStream("sync/vista/json/consult.json")));
        genericDao.save(POMUtils.newInstance(Procedure.class, Procedure.class.getResourceAsStream("sync/vista/json/rad.json")));
        genericDao.save(POMUtils.newInstance(Procedure.class, Procedure.class.getResourceAsStream("sync/vista/json/surgery.json")));

        // confirm expected database rows/values
        assertEquals(3, countTableRows("clinical_procedure"));
        assertEquals(5, countTableRows("procedure_result"));
        assertEquals(2, countTableRows("procedure_provider"));
    }

    @Test
    public void testImmunizationObjects() {
        genericDao.save(POMUtils.newInstance(Immunization.class, Immunization.class.getResourceAsStream("sync/vista/json/immunization.json")));

        // confirm expected database rows/values
        assertEquals(1, countTableRows("immunization"));
    }

    @Test
    public void testDocumentObjects() {
        genericDao.save(POMUtils.newInstance(Document.class, Document.class.getResourceAsStream("sync/vista/json/document.json")));

        // confirm expected database rows/values
        assertEquals(1, countTableRows("document"));

    }

    @Test
    @Ignore // Broken for some reason?
    public void testMedicationObjects() {
        Medication med = POMUtils.newInstance(Medication.class, Medication.class.getResourceAsStream("sync/vista/json/medication.json"));
        med.setData("pid", "23");
        genericDao.save(med);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("medication"));
    }

    @Test
    public void testOrderObjects() {
        Order o = POMUtils.newInstance(Order.class, Order.class.getResourceAsStream("sync/vista/json/order.json"));
        genericDao.save(o);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("clinical_order"));
    }

    @Test
    public void testEncounterObjects() {
        InputStream json = Encounter.class.getResourceAsStream("sync/vista/json/visit.json");
        Encounter en = POMUtils.newInstance(Encounter.class, json);
        genericDao.save(en);
    }

    @Test
    public void testAllergyObjects() {
          Allergy allergy = POMUtils.newInstance(Allergy.class, Allergy.class.getResourceAsStream("sync/vista/json/allergy.json"));
        genericDao.save(allergy);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("allergy"));
        assertEquals(1, countTableRows("allergy_product"));
        assertEquals(1, countTableRows("allergy_reaction"));
        
        genericDao.save(allergy);
        
        // confirm expected database rows/values
        assertEquals(1, countTableRows("allergy"));
        assertEquals(1, countTableRows("allergy_product"));
        assertEquals(1, countTableRows("allergy_reaction"));
    }

    @Test
    public void testAccessionObjects() {
        ResultOrganizer accession = POMUtils.newInstance(ResultOrganizer.class, ResultOrganizer.class.getResourceAsStream("sync/vista/json/accession.json"));
        genericDao.save(accession);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("result_organizer"));
        assertEquals(7, countTableRows("result"));
    }

    @Test
    public void testAccessionMerge() {
        ResultOrganizer accession = POMUtils.newInstance(ResultOrganizer.class, ResultOrganizer.class.getResourceAsStream("sync/vista/json/accession.json"));
        // save it once
        genericDao.save(accession);
        // save it again
        genericDao.save(accession);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("result_organizer"));
        assertEquals(7, countTableRows("result"));
    }

    @Test
    public void testProblemObjects() {
    	Problem problem = POMUtils.newInstance(Problem.class, Problem.class.getResourceAsStream("sync/vista/json/problem.json"));
    	genericDao.save(problem);
    	
    	// confirm expected database rows/values
    	assertEquals(1, countTableRows("problem"));
    	assertEquals(2, countTableRows("problem_comment"));
    	
    	genericDao.save(problem);

    	assertEquals(1, countTableRows("problem"));
    	assertEquals(2, countTableRows("problem_comment"));
    }
    
    @Test
    public void testVitalsObjects() {
        VitalSignOrganizer vitalSignOrganizer = POMUtils.newInstance(VitalSignOrganizer.class, VitalSignOrganizer.class.getResourceAsStream("sync/vista/json/vital.json"));
        genericDao.save(vitalSignOrganizer);

        // confirm expected database rows/values
        assertEquals(1, countTableRows("vital_sign_organizer"));
    }
    
    @Test
    public void testHealthFactorsObjects() {
    	HealthFactor healthFactor = POMUtils.newInstance(HealthFactor.class, HealthFactor.class.getResourceAsStream("sync/vista/json/healthFactor.json"));
    	genericDao.save(healthFactor);
    	
    	// confirm expected database rows/values
    	assertEquals(1, countTableRows("health_factor"));
    }
    
    @Test
    public void testObservationObjects() {
    	Observation obs = POMUtils.newInstance(Observation.class, Observation.class.getResourceAsStream("sync/vista/json/observation.json"));
    	genericDao.save(obs);
    	
    	// confirm expected database rows/values
    	assertEquals(1, countTableRows("observation"));
    }
}
