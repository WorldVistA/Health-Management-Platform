package org.osehra.cpe.vpr.sync.vista.json

import org.junit.Test
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.Document
import org.osehra.cpe.vpr.DocumentClinician

import org.osehra.cpe.vpr.UidUtils

import org.osehra.cpe.datetime.PointInTime

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

class DocumentImporterTest extends AbstractImporterTest {

    @Test
    void testConvert() {
        DocumentImporter importer = new DocumentImporter()

        Document a = importer.convert(MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("document.json"), mockPatient, "document"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)));

        assertThat(a.uid, is(equalTo(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "229", "4329"))))
        assertThat(a.localId, is(equalTo("3531")))
		assertThat(a.text, not(null));
		assertThat(a.text.size(), is(equalTo(1)));
		assertThat(a.text[0], not(null));
		
		for(DocumentClinician dc: a.clinicians)
		{
			if(dc.role.equals("A"))
			{
				assertThat(dc.uid, is(equalTo(UidUtils.getUserUid(MockVistaDataChunks.VISTA_ID, "986"))))
			}
			else
			{
				assertThat(dc.role, is(equalTo("S")))
				assertThat(dc.signedDateTime, is(new PointInTime(2006, 12, 8, 18, 27, 50)))
				assertThat(dc.signature, is(equalTo("THREE PROVIDER PHYSICIAN")))
				assertThat(dc.uid, is(equalTo(UidUtils.getUserUid(MockVistaDataChunks.VISTA_ID, "986"))))
			}
		}
		
        assertThat(a.documentClass, is(equalTo("SURGICAL REPORTS")))
        assertThat(a.encounterUid, is(equalTo(UidUtils.getVisitUid(MockVistaDataChunks.VISTA_ID, "8", "5554"))))
        assertThat(a.encounterName, equalTo("OR4 Dec 08, 2006"))
        assertThat(a.facilityName, is(equalTo("SLC-FO HMP DEV")))
        assertThat(a.localId, is(equalTo("3531")))
        assertThat(a.localTitle, is(equalTo("ANESTHESIA REPORT")))
        assertThat(a.referenceDateTime, is(new PointInTime(2006, 12, 8, 7, 30)))
		assertThat(a.documentTypeName, is(equalTo("Surgery Report")))
		assertThat(a.documentTypeCode, is(equalTo("SR")))

    }
}
