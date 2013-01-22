package EXT.DOMAIN.cpe.vpr.sync.vista.json

import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test
import EXT.DOMAIN.cpe.vpr.*
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.*

class TaskImporterTest extends AbstractImporterTest {
	
static final String TASK_RESULT_STRING_JSON = '''
{
                "assignToCode": "urn:va:user:F484:1089",
                "assignToName": "AVIVAUSER,TWELVE",
                "complete": false,
                "description": "?test",
                "dueDate": "20121026",
                "facilityCode": "500",
                "facilityName": null,
                "ownerCode": "urn:va:user:F484:1089",
                "ownerName": "AVIVAUSER,TWELVE ",
                "taskName": "a test of a task",
                "uid": "urn:va:F484:229:task:16"
            }
'''

	@Test
	public void testTask() throws Exception {
		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(TASK_RESULT_STRING_JSON, mockPatient, "task")
		TaskImporter t1 = new TaskImporter()
		Task t = t1.convert(fragment)
		assertNotNull(t)
//        assertThat(t.getPid(), is(equalTo(MOCK_PID)))
//        assertThat(t.getFacilityCode(), is("500"))
	}

}
