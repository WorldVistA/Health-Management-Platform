package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Before
import org.junit.Test
import org.osehra.cpe.vpr.*
import static org.junit.Assert.*
import static org.hamcrest.CoreMatchers.*

class MedicationImporterTest extends AbstractImporterTest {
	
static final String MEDICATION_RESULT_STRING_JSON = '''
{
    "dosages": [
        {
            "dose": 200,
            "route": "PO",
            "schedule": "3ID",
            "start": "201205171839-0700",
            "stop": "201205250000-0700",
            "units": "MG"
        }
    ],
    "facilityName": "CAMP MASTER",
    "facilityCode": "500",
    "imo": false,
    "localId": "5U;I",
    "medStatusName": "active",
    "name": "ACARBOSE TAB ",
    "orders": [
        {
            "location": "158^7A GEN MED",
            "orderUid": 34912,
            "ordered": "201205171839-0700",
            "pharmacistName": "AVIVAUSER,TWELVE",
            "pharmacistUid": "urn:va:user:F484:1089",
            "providerName": "AVIVAUSER,TWELVE",
            "providerUid": "urn:va:user:F484:1089"
        }
    ],
    "overallStart": "201205171839-0700",
    "productFormName": "TAB",
    "products": [
        {
            "drugClassCode": "urn:vadc:HS502",
            "drugClassName": "ORAL HYPOGLYCEMIC AGENTS,ORAL",
            "ingredientCode": "urn:vuid:4020940",
            "ingredientName": "ACARBOSE",
            "ingredientRole": "urn:sct:410942007",
            "strength": "100 MG",
            "suppliedCode": "urn:vuid:4012835",
            "suppliedName": "ACARBOSE 100MG TAB"
        }
    ],
    "sig": "Give: 200MG PO 3ID",
    "stopped": "201205250000-0700",
    "uid": "urn:va:F484:229:med:34912",
    "vaStatus": "ACTIVE",
    "vaType": "I"
}
'''

static final String MEDICATION_RESULT_STRING_JSON_ONE = '''
{
    "facilityName": "CAMP MASTER",
    "facilityCode": "500",
    "imo": false,
    "localId": "403838;O",
    "medStatus": "urn:sct:55561003",
    "medStatusName": "active",
    "orders": [
        {
            "pharmacistName": "",
            "pharmacistUid": "urn:va:user:F484:0",
            "providerName": "",
            "providerUid": "urn:va:user:F484:0"
        }
    ],
    "overallStop": 20100528,
    "sig": "",
    "type": "Prescription",
    "uid": "urn:va:F484:229:med:27844",
    "vaStatus": "",
    "vaType": "O"
}
'''

static final String MEDICATION_RESULT_STRING_JSON_EXPIRED_OUTPATIENT = '''
{
    "dosages": [
        {
            "dose": "500 MG",
            "relativeStart": "0",
            "relativeStop": 129600,
            "routeName": "PO",
            "scheduleName": "BID",
            "start": 20100227,
            "stop": 20100528,
            "units": "MG"
        }
    ],
    "facilityCode": "500D",
    "facilityName": "SLC-FO HMP DEV",
    "imo": false,
    "lastFilled": 20100227,
    "localId": "403838;O",
    "medStatus": "urn:sct:392521001",
    "medStatusName": "historical",
    "medType": "urn:sct:73639000",
    "name": "METFORMIN TAB,SA",
    "orders": [
        {
            "daysSupply": 90,
            "fillCost": 90,
            "fillsAllowed": "0",
            "fillsRemaining": "0",
            "locationCode": "urn:va:location:500D:23",
            "locationName": "GENERAL MEDICINE",
            "orderUid": "urn:va:F484:151:order:27844",
            "ordered": "20100227090342-0700",
            "pharmacistUid": "urn:va:user:F484:10000000056",
            "pharmacistName": "PHARMACIST,ONE",
            "prescriptionId": 500616,
            "providerUid": "urn:va:user:F484:983",
            "providerName": "PROVIDER,ONE",
            "quantityOrdered": 180,
            "vaRouting": "W"
        }
    ],
    "overallStart": 20100227,
    "overallStop": 20100528,
    "productFormName": "TAB,SA",
    "products": [
        {
            "drugClassCode": "urn:vadc:HS502",
            "drugClassName": "ORAL HYPOGLYCEMIC AGENTS,ORAL",
            "ingredientCode": "urn:vuid:4023979",
            "ingredientName": "METFORMIN",
            "ingredientRole": "urn:sct:410942007",
            "strength": "500 MG",
            "suppliedCode": "urn:vuid:4014984",
            "suppliedName": "METFORMIN HCL 500MG TAB,SA"
        }
    ],
    "qualifiedName": "METFORMIN TAB,SA",
    "sig": "TAKE ONE TABLET MOUTH TWICE A DAY",
    "stopped": 20100528,
    "type": "Prescription",
    "uid": "urn:va:F484:229:med:27844",
    "vaStatus": "EXPIRED",
    "vaType": "O"
}
'''

    static final String MEDICATION_RESULT_STRING_JSON_IV_PARSER = '''
{
    "dosages": [
        {
            "routeName": "IV",
            "scheduleName": "NOW"
        }
    ],
    "facilityCode": "500D",
    "facilityName": "SLC-FO HMP DEV",
    "imo": false,
    "localId": "46V;I",
    "medStatus": "urn:sct:392521001",
    "medStatusName": "historical",
    "medType": "urn:sct:105903003",
    "name": "FUROSEMIDE INJ,SOLN",
    "orders": [
        {
            "locationCode": "urn:va:location:500D:66",
            "locationName": "5 WEST PSYCH",
            "orderUid": "urn:va:F484:8:order:10090",
            "ordered": "199906221019-0700",
            "pharmacistUid": "urn:va:user:F484:11817",
            "pharmacistName": "RADTECH,FORTYONE",
            "providerCode": "urn:va:user:F484:11595",
            "providerName": "PROVIDER,TWOHUNDREDFORTYNINE"
        }
    ],
    "overallStart": "199906221100-0700",
    "products": [
        {
            "drugClassCode": "urn:vadc:CV702",
            "drugClassName": "LOOP DIURETICS",
            "ingredientCode": "urn:vuid:4017830",
            "ingredientName": "FUROSEMIDE",
            "ingredientRole": "urn:sct:418804003",
            "strength": "20 MG",
            "suppliedCode": "urn:vuid:4002371",
            "suppliedName": "FUROSEMIDE 10MG/ML INJ"
        },
        {
            "drugClassCode": "urn:vadc:TN101",
            "drugClassName": "IV SOLUTIONS WITHOUT ELECTROLYTES",
            "ingredientCode": "urn:vuid:4017760",
            "ingredientName": "DEXTROSE",
            "ingredientRole": "urn:sct:418297009",
            "suppliedCode": "urn:vuid:4014924",
            "suppliedName": "DEXTROSE 5% INJ,BAG,1000ML",
            "volume": "50 ML"
        }
    ],
    "qualifiedName": "FUROSEMIDE in DEXTROSE",
    "stopped": "199906221500-0700",
    "uid": "urn:va:F484:229:med:10090",
    "vaStatus": "EXPIRED",
    "vaType": "V"
}
'''

	@Test
	public void testActiveMed() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON, mockPatient, "pharmacy")
		MedicationImporter m1 = new MedicationImporter()
		Medication m = m1.convert(fragment)
		assertNotNull(m)
        assertThat(m.getPid(), is(equalTo(MOCK_PID)))
        assertThat(m.getFacilityCode(), is("500"))
        assertThat(m.getFacilityName(), is("CAMP MASTER"))
        assertEquals("TAB", m.productFormName)
        assertEquals(m.getUid(), UidUtils.getMedicationUid("F484", "229", "34912"))
        assertEquals("urn:vuid:4020940", m.onlyProduct().getIngredientCode());
        assertEquals("urn:sct:410942007", m.onlyProduct().getIngredientRole());
        assertEquals("ACARBOSE", m.onlyProduct().getIngredientName());
        assertEquals("ORAL HYPOGLYCEMIC AGENTS,ORAL", m.onlyProduct().getDrugClassName());
        assertEquals("urn:vadc:HS502", m.onlyProduct().getDrugClassCode());
        assertNull(m.getProductFormCode());
        assertEquals(
                "Give: 200MG PO 3ID",
                m.getSig());
        assertEquals("34912",m.getOrders()[0].orderUid)


	}

    @Test
	public void testMed() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_ONE, mockPatient, "pharmacy")
		MedicationImporter m1 = new MedicationImporter()
		Medication m = m1.convert(fragment)
        assertThat(m.getPid(), is(equalTo(MOCK_PID)))
        assertThat(m.getFacilityCode(), is("500"))
        assertThat(m.getFacilityName(), is("CAMP MASTER"))
        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "27844"), m.getUid());
        assertEquals("403838;O", m.getLocalId());
        assertEquals(new PointInTime(2010, 5, 28), m.getOverallStop());
        assertNull(m.getStopped());
        assertEquals("active", m.getMedStatusName());
        assertEquals(CodeConstants.SCT_MED_STATUS_ACTIVE, m.getMedStatus());
//        assertEquals(CodeConstants.SCT_MED_TYPE_PRESCRIBED, m.getMedType());


	}

    @Test
	public void testExpiredOutpatientMed() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_EXPIRED_OUTPATIENT, mockPatient,  "pharmacy")
		MedicationImporter m1 = new MedicationImporter()
		Medication m = m1.convert(fragment)
        assertThat(m.getPid(), is(equalTo(MOCK_PID)))
        assertThat(m.getFacilityCode(), is("500D"))
        assertThat(m.getFacilityName(), is("SLC-FO HMP DEV"))
        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "27844"), m.getUid());
        assertEquals("403838;O", m.getLocalId());
        assertEquals("urn:vuid:4023979", m.onlyProduct().getIngredientCode());
        assertEquals("urn:sct:410942007", m.onlyProduct().getIngredientRole());
        assertNull(m.getProductFormCode());
        assertEquals("urn:vadc:HS502", m.onlyProduct().getDrugClassCode());
        assertEquals(new PointInTime(2010, 2, 27), m.getOverallStart());
        assertEquals(new PointInTime(2010, 5, 28), m.getOverallStop());
//        assertNull(m.getStopped());
        assertEquals(CodeConstants.SCT_MED_STATUS_HISTORY, m.getMedStatus());
        assertEquals("historical", m.getMedStatusName());
        assertEquals(CodeConstants.SCT_MED_TYPE_PRESCRIBED, m.getMedType());
        assertEquals("METFORMIN", m.onlyProduct().getIngredientName());
        assertEquals("TAB,SA", m.getProductFormName());
        assertEquals("ORAL HYPOGLYCEMIC AGENTS,ORAL", m.onlyProduct()
                .getDrugClassName());
        assertEquals(
                "TAKE ONE TABLET MOUTH TWICE A DAY",
                m.getSig());
        assertEquals("METFORMIN TAB,SA", m.getQualifiedName());
        assertEquals(
                "METFORMIN HCL 500MG TAB,SA (EXPIRED)\n TAKE ONE TABLET MOUTH TWICE A DAY",
                m.getSummary());

        MedicationDose d = m.getDosages().get(0);
        assertEquals(1, m.getDosages().size());
        assertEquals("500 MG", d.getDose());
        assertEquals(0, d.getRelativeStart().intValue());
        assertEquals(129600, d.getRelativeStop().intValue());
//        assertNull(d.getStartDateString());
//        assertNull(d.getStopDateString());
//
        assertEquals(1, m.getProducts().size());
        assertEquals(1, m.getOrders().size());
        assertNull(m.getFills());
        MedicationOrder o = m.getOrders().get(0);
        assertEquals("urn:va:F484:151:order:27844", o.getOrderUid());

	}

    @Test
	public void testIvParser() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_IV_PARSER, mockPatient, "pharmacy")
		MedicationImporter m1 = new MedicationImporter()
		Medication m = m1.convert(fragment)
        assertThat(m.getPid(), is(equalTo(MOCK_PID)))
        assertThat(m.getFacilityCode(), is("500D"))
        assertThat(m.getFacilityName(), is("SLC-FO HMP DEV"))
        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "10090"), m.getUid());
        assertTrue("46V;I".equals(m.getLocalId()));
        assertNull(m.getProductFormName());
        //TODO: speak to Kevin about this the old test is FUROSEMIDE IN %5 DEXTROSE
        assertEquals("FUROSEMIDE in DEXTROSE", m.getQualifiedName());
        MedicationDose d = m.getDosages().get(0);
        assertNull(d.getDose());
        assertNull(d.getIvRate());
        assertNull(d.getRelativeStart());
        assertNull(d.getRelativeStop());
        assertEquals("IV", d.getRouteName());
        assertEquals("NOW", d.getScheduleName());
        assertEquals(2, m.getProducts().size());
        MedicationProduct p = m.getProducts().get(0);
        MedicationProduct v = m.getProducts().get(1);
        assertEquals("IV SOLUTIONS WITHOUT ELECTROLYTES", v.getDrugClassName());
        assertEquals("50 ML", v.getVolume());
        assertEquals("LOOP DIURETICS", p.getDrugClassName());
        assertEquals("20 MG", p.getStrength());
    }
}
