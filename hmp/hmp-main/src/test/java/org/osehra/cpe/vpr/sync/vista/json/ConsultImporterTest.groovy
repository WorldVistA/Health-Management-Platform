package org.osehra.cpe.vpr.sync.vista.json

import org.junit.Test
import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.Procedure

import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.UidUtils

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

class ConsultImporterTest extends AbstractImporterTest {

    static final String CONSULT_JSON = '''
	 {
      "category" : "C",
		"results" : [
			{"uid" : "urn:va:F484:229:tiu:3108"}
		],
      "facilityName" : "CAMP MASTER",
      "facilityCode" : "CM",
      "localId" : 373,
      "typeName" : "AUDIOLOGY OUTPATIENT Cons",
      "orderUid" : "urn:va:F484:229:order:15471",
      "consultProcedure" : "Consult",
      "dateTime" : "20010101010101",
      "service" : "AUDIOLOGY OUTPATIENT",
      "status" : "COMPLETE",
      "uid" : "urn:va:F484:229:proc:373"
    }
	'''

    @Test
    void testConvert() {
        ProcedureImporter importer = new ProcedureImporter()

        Procedure a = importer.convert(MockVistaDataChunks.createFromJson(CONSULT_JSON, mockPatient, "consult"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)));

        assertThat(a.uid, is(equalTo(UidUtils.getProcedureUid(MockVistaDataChunks.VISTA_ID, "229", "373"))))
        assertThat(a.localId, is(equalTo("373")))
        assertThat(a.category, is(equalTo("C")))
		assertThat(a.results.size(), is(1));
		assertThat(a.results.iterator().next().uid, is(equalTo(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "229", "3108"))))
        /*
         * TODO: Need a consult with an encounter on it.
         * Provider(s)
         * Modifier(s)
         * /assertThat(a.encounter.uid, is(equalTo(UidUtils.getVisitUid(MockVistaDataChunks.VISTA_ID, "229", "7297"))))
         */
        assertThat(a.facilityName, is(equalTo("CAMP MASTER")))
        assertThat(a.facilityCode, is(equalTo("CM")))
        assertThat(a.typeName, is(equalTo("AUDIOLOGY OUTPATIENT Cons")))
        assertThat(a.orderUid, is(equalTo(UidUtils.getOrderUid(MockVistaDataChunks.VISTA_ID, "229", "15471"))))
        assertThat(a.consultProcedure, is(equalTo("Consult")))
		assertThat(a.dateTime, is(new PointInTime(2001,01,01,01,01,01)))
		assertThat(a.service, is(equalTo("AUDIOLOGY OUTPATIENT")))
		assertThat(a.status, is(equalTo("COMPLETE")))
		
    }
}
