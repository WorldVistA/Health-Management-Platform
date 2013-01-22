package EXT.DOMAIN.cpe.vpr.sync.vista.json

import org.junit.Test
import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.vpr.Procedure
import EXT.DOMAIN.cpe.vpr.ProcedureResult

import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import EXT.DOMAIN.cpe.vpr.UidUtils

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*

class SurgeryImporterTest extends AbstractImporterTest {

    @Test
    void testConvert() {
        ProcedureImporter importer = new ProcedureImporter()

        Procedure a = importer.convert(MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("surgery.json"), mockPatient, "surgery"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)))
        assertThat(a.uid, is(equalTo(UidUtils.getSurgeryUid(MockVistaDataChunks.VISTA_ID, "229", "10010"))))
        assertThat(a.category, is(equalTo("SR")))
		assertThat(a.dateTime, is(new PointInTime(2006, 12, 8, 07, 30)))
		
		// TODO: Find a surgery that has an encounter/visit associated with it.
		assertThat(a.encounterUid, is(equalTo("")))

        assertThat(a.facilityName, is(equalTo("CAMP MASTER")))
        assertThat(a.facilityCode, is(equalTo("???")))
		assertThat(a.kind, is(equalTo("Surgery"))) // Dictated by kind field calculations in Procedure.java; doesn't matter what comes in JSON.
        assertThat(a.localId, is(equalTo("10010")))
        assertThat(a.providers.size(), is(1));
		assertThat(a.providers.iterator().next().uid, is(equalTo(UidUtils.getUserUid(MockVistaDataChunks.VISTA_ID, "983"))))
		ArrayList<String> ruids = new ArrayList<String>();
		ruids.add(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "229", "3557"))
        ruids.add(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "229", "3514"))
        ruids.add(UidUtils.getDocumentUid(MockVistaDataChunks.VISTA_ID, "229", "3513"))
        assertThat(a.results.size(), is(3));
		for(ProcedureResult r: a.results)
		{
			assertThat(ruids, hasItem(r.uid));
		}
		assertThat(a.status, is(equalTo("COMPLETE")))
	}
}
