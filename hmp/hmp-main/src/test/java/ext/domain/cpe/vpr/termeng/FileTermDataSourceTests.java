package EXT.DOMAIN.cpe.vpr.termeng;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: VHAISLBRAYB
 * Date: 6/10/11
 * Time: 2:34 PM
 */
public class FileTermDataSourceTests extends TestCase {
    /* Sample File:
RxNorm  VUID    VUID Description                Preferred
91349   4009353 HYDROGEN PEROXIDE 3% SOLN,TOP   T
106248  4002921 HYDROCORTISONE 0.1% CREAM,TOP   F
     */
//TODO - Brian will refactor the TermEng class and until then the tests will be commented out.
//    FileTermDataSource ds, ds2;
    File file1, file2;
    protected void setUp() throws Exception {
        file1 = new File(getClass().getResource("test.txt").toURI());
        file2 = new File(getClass().getResource("test2.txt").toURI());
//        ds = new FileTermDataSource(file1, "VHAT->RxNorm", "VHAT", "RxNorm", 2, 1, 2, "\t");
//        ds2 = new FileTermDataSource(file2, "VHAT2->RxNorm2", "VHAT2", "RxNorm2", 2, 1, 1, "\\|");
    }

    public void testMapSetInfo() throws URISyntaxException {
//        Set<MapSetInfo> sets = ds.getMapSetList();
//        assertEquals(1, sets.size());
//        MapSetInfo info = sets.iterator().next();
//
//        assertEquals(file1.getName(), info.getID());
//        assertEquals("VHAT->RxNorm", info.getName());
//        assertEquals("VHAT", info.getSourceCodeSystemID());
//        assertEquals("RxNorm", info.getTargetCodeSystemID());
//        assertEquals("2", info.getProperties().getProperty("start.row"));
//        assertEquals("\t", info.getProperties().getProperty("field.delimiter.pattern"));
//        assertEquals("2", info.getProperties().getProperty("field.source.idx"));
//        assertEquals("1", info.getProperties().getProperty("field.target.idx"));
    }

    public void testCodeSystemList() {
//        assertTrue(ds.getCodeSystemList().contains("VHAT"));
//        assertTrue(ds.getCodeSystemList().contains("RxNorm"));
//        assertTrue(ds2.getCodeSystemList().contains("VHAT2"));
//        assertTrue(ds2.getCodeSystemList().contains("RxNorm2"));
    }

    public void testCount() throws URISyntaxException {
//        assertEquals(2, ds.getMappingCount());
//        assertEquals(2, ds2.getMappingCount());
//        assertEquals(ds.getMappingCount(), ds.getMappings(file1.getName()).size());
//        assertEquals(ds.getMappingCount()+"", ds.getMapSet(file1.getName()).getProperties().getProperty("table.size"));
    }

    public void testMappings() {
        // these should exist
//        assertEquals("91349", ds.getMapping("4009353", "VHAT", "RxNorm"));
//        assertEquals("91349", ds2.getMapping("4009353", "VHAT2", "RxNorm2"));
//        assertEquals("106248", ds.getMapping("4002921", "VHAT", "RxNorm"));
//        assertEquals("106248", ds2.getMapping("4002921", "VHAT2", "RxNorm2"));
    }
}
