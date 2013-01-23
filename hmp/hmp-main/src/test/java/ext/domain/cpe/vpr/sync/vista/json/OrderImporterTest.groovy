package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Before
import org.junit.Test
import org.osehra.cpe.vpr.*
import static org.junit.Assert.*
import static org.hamcrest.CoreMatchers.*

class OrderImporterTest extends AbstractImporterTest {

static final String ORDER_RESULT_STRING_JSON_ONE = '''

'''

static final String ORDER_RESULT_STRING_JSON_TWO = '''

'''

	@Test
	public void testOrder() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("order.json"), mockPatient, "order")
		OrderImporter o1 = new OrderImporter()
		Order o = o1.convert(fragment)
		assertNotNull(o)
        assertThat(o.getPid(), is(equalTo(MOCK_PID)))
        assertEquals(o.getUid(), UidUtils.getOrderUid("66374", "229", "33939"))
        assertEquals(o.getLocalId(), "33939");
        assertEquals(o.getDisplayGroup(), "RAD");
        assertEquals(o.getEntered(), new PointInTime(2011, 7, 20, 8, 56));
        assertEquals(o.getStart(), new PointInTime(2011, 3, 10));
        assertEquals(o.getStop(), new PointInTime(2011, 7, 20, 9, 26));
        assertEquals("urn:va:order-status:comp", o.getStatusCode());
        assertEquals("COMPLETE", o.getStatusName());
	}

    @Test
	public void testOrderOne() throws Exception {
//		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_ONE, "pharmacy")
//		MedicationImporter m1 = new MedicationImporter()
//		Medication m = m1.convert(fragment)
////		assertSame(pt, m.getPatient());
////        assertSame(facility, m.getFacility());
//        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "27844"), m.getUid());
//        assertEquals("403838;O", m.getLocalId());
//        assertEquals(new PointInTime(2010, 5, 28), m.getOverallStop());
//        assertNull(m.getStopped());
//        assertEquals("active", m.getMedStatusName());
//        assertEquals(CodeConstants.SCT_MED_STATUS_ACTIVE, m.getMedStatus());
//        assertEquals(CodeConstants.SCT_MED_TYPE_PRESCRIBED, m.getMedType());


	}

    @Test
	public void testMedTwo() throws Exception {
//		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_TWO, "pharmacy")
//		MedicationImporter m1 = new MedicationImporter()
//		Medication m = m1.convert(fragment)
//        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "27844"), m.getUid());
//        assertEquals("403838;O", m.getLocalId());
//        assertEquals("urn:vuid:4023979", m.onlyProduct().getIngredientCode());
//        assertEquals("urn:sct:410942007", m.onlyProduct().getIngredientRole());
//        assertNull(m.getProductFormCode());
//        assertEquals("urn:vadc:HS502", m.onlyProduct().getDrugClassCode());
//        assertEquals(new PointInTime(2010, 2, 27), m.getOverallStart());
//        assertEquals(new PointInTime(2010, 5, 28), m.getOverallStop());
////        assertNull(m.getStopped());
//        assertEquals(CodeConstants.SCT_MED_STATUS_HISTORY, m.getMedStatus());
//        assertEquals("historical", m.getMedStatusName());
//        assertEquals(CodeConstants.SCT_MED_TYPE_PRESCRIBED, m.getMedType());
//        assertEquals("METFORMIN", m.onlyProduct().getIngredientName());
//        assertEquals("TAB,SA", m.getProductFormName());
//        assertEquals("ORAL HYPOGLYCEMIC AGENTS,ORAL", m.onlyProduct()
//                .getDrugClassName());
//        assertEquals(
//                "TAKE ONE TABLET MOUTH TWICE A DAY",
//                m.getSig());
//        assertEquals("METFORMIN", m.getQualifiedName());
//        assertEquals(
//                "METFORMIN HCL 500MG TAB,SA (EXPIRED)\n TAKE ONE TABLET MOUTH TWICE A DAY",
//                m.getSummary());
//
//        MedicationDose d = m.getDosages().get(0);
//        assertEquals(1, m.getDosages().size());
//        assertEquals("500 MG", d.getDose());
//        assertEquals(0, d.getRelativeStart().intValue());
//        assertEquals(129600, d.getRelativeStop().intValue());
////        assertNull(d.getStartDateString());
////        assertNull(d.getStopDateString());
////
//        assertEquals(1, m.getProducts().size());
//        assertEquals(1, m.getOrders().size());
//        assertNull(m.getFills());
//        MedicationOrder o = m.getOrders().get(0);
//        assertEquals("urn:va:F484:151:order:27844", o.getOrderUid());

	}
}
