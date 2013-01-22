package EXT.DOMAIN.cpe.vpr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import EXT.DOMAIN.cpe.vpr.vistasvc.CacheMgrTests;
import EXT.DOMAIN.cpe.vpr.vistasvc.EhCacheTestUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.util.FileCopyUtils;

public class RosterServiceTests {
	RpcOperations mockRPC;
	RosterService s;

    @BeforeClass
    public static void init() throws IOException {
        EhCacheTestUtils.setUp();
    }

    @AfterClass
    public static void shutdown() throws IOException {
        EhCacheTestUtils.tearDown();
    }

    public String getResourceString(String str) {
		try {
			return FileCopyUtils.copyToString(new InputStreamReader(getClass().getResourceAsStream(str)));
		} catch (IOException e) {
		}
		return null;
	}

	@Before
	public void setUp() {
        new CacheMgrTests().setup();

		mockRPC = mock(RpcOperations.class);
		s = new RosterService();
		s.rpcTemplate = mockRPC;
		
		when(mockRPC.executeForString(eq("/VPR UI CONTEXT/VPR ROSTERS"), Matchers.anyList())).thenReturn(getResourceString("VPR ROSTERS.xml"));
		when(mockRPC.executeForString(eq("/VPR UI CONTEXT/VPR ROSTER PATIENTS"), Matchers.anyVararg())).thenReturn(getResourceString("VPR ROSTER PATIENTS.xml"));
		when(mockRPC.executeForString(eq("/VPR UI CONTEXT/VPR GET SOURCE"), Matchers.anyList())).thenReturn(getResourceString("VPR GET SOURCE.xml"));
	}
	
	@Test
	public void testRosters() {
		// make sure rosters list work
		List<Map> l = s.getRosters(); 
		assertTrue(l.size() > 0);
	}
	
	@Test
	@Ignore // Broken?
	public void testRosterPats() {
		// make sure patient list returns
		List<Map> p = s.getRosterPats("14");
		assertTrue(p.size() > 0);
		List p2 = s.getRosterPatDFNs("14");
		assertEquals(p.size(), p2.size());
		Map r1 = p.get(0);
		
		// ensure all the fields are present
		assertEquals(r1.get("dfn"), p2.get(0));
		assertEquals("AVIVAPATIENT,TWENTYEIGHT", r1.get("name"));
		assertEquals("urn:va:patient:foo:3", r1.get("uid"));
		assertEquals("M", r1.get("gender"));
		assertEquals("19350407", r1.get("dob"));
		assertEquals("3", r1.get("dfn"));
		assertEquals("10108", r1.get("icn"));
		//assertEquals("AVIVAPATIENT,TWENTYEIGHT", r1.get("ssn"));
	}
	
	@Test
	public void testPatientSearch() {
		// patient search results
		List<Map> r = s.searchRosterSource("Patient", "aviva");
		assertTrue(r.size() > 0);
		Map r1 = r.get(0);
		assertEquals("AVIVAPATIENT,EIGHT", r1.get("name"));
		assertEquals("100848", r1.get("dfn"));
		assertEquals("5000000347", r1.get("icn"));
		assertEquals("MALE", r1.get("gender"));
		assertEquals("19331001", r1.get("dob"));
		assertEquals("666000928", r1.get("ssn"));
		
		// TODO: Test other searchRosterSource's (including bad ones)
	}
	
	@Test
	public void tesRosterToRPCDefenition() throws Exception {
		
		Map rstr1 = new HashMap<String, Map>();
		rstr1.put("id", "39");
		rstr1.put("name", "test");
		rstr1.put("display", "test1");
		rstr1.put("ownerid", "1084");
		rstr1.put("ownername", null);
		HashMap pat1 = new HashMap<String, Object>();
		pat1.put("uid", "urn:va:patient:foo:8");
		pat1.put("dfn", "8");
		pat1.put("icn", "10110");
		pat1.put("name", "AVIVAPATIENT,X");
		pat1.put("gender", "urn:va:pat-gender:M");
		pat1.put("age", 77);
		List pts = new ArrayList();
		pts.add(pat1);
		rstr1.put("patients",pts);
		
		List<Map> cache = new ArrayList<Map>();
		cache.add(rstr1);
		
		String[] defenition = s.buildRPCDefenition(cache.get(0), "1287");
		assertNotNull(defenition);
		assertEquals("test^^test1^^1084",defenition[0]);
		assertEquals("Patient^UNION^8",defenition[1]);
		assertEquals("Patient^UNION^1287",defenition[2]);
		
//			def p1 = [uid:'urn:va:patient:foo:8', dfn:'8', icn:'10110', name:'AVIVAPATIENT,THIRTY', gender:'urn:va:pat-gender:M', dob:19350407, ssn:'666000010', pid:'2', familyName:'AVIVAPATIENT', givenNames:'THIRTY', updated:20121004130721.742, sensitive:false, died:null, age:77]
//			// Cache is array of maps each map is a roster data
//			def cache = [[id:39, name:'ROMSTER', display:'ROMSTER', ownerid:'', ownername:'', patientCount:1, patients:[p1]]]
//			int s=1;
	}

}
