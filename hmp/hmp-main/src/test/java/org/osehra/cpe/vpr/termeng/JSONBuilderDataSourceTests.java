package org.osehra.cpe.vpr.termeng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Ignore // This test suite requires Postgres
public class JSONBuilderDataSourceTests {
	
	public static Connection conn;
	JSONBuilderDataSource lnc, vandf;
	TermEng eng;

	@BeforeClass
	public static void setupClass() throws SQLException {
		String jdbcurl = "jdbc:postgresql://localhost:5432/postgres";
		String uname = "umls2012aa";
		String pword = "umls2012aa";
		conn = DriverManager.getConnection(jdbcurl, uname, pword);
	}
	
	@Before
	public void setup() throws SQLException {
		lnc = new JSONBuilderDataSource(conn, "umls2012aa");
		vandf = new JSONBuilderDataSource(conn, "umls2012aa");
		eng = TermEng.createInstance(new ITermDataSource[] {lnc,vandf});
	}
	
	@Test
	public void test() throws SQLException {
		String urn = "urn:lnc:2345-7";
		
		Set<String> set = lnc.getParentSet(urn);
		assertEquals(2, set.size());
		assertTrue(set.contains("urn:lnc:LP42107-0"));
		assertTrue(set.contains("urn:lnc:MTHU000049"));
		
		set = lnc.getEquivalentSet(urn);
	}
	
	@Test
	public void test2() throws SQLException {
		String urn = "urn:vandf:4014984";
		
		Set<String> set = vandf.getParentSet(urn);
		assertEquals(1, set.size());
		assertTrue(set.contains("urn:vandf:4021632")); // ORAL HYPOGLYCEMIC AGENTS,ORAL
		
		set = vandf.getEquivalentSet(urn);
		assertEquals(3, set.size());
		assertTrue(set.contains(urn)); // same as itself
		assertTrue(set.contains("urn:ndfrt:N0000162702")); // METFORMIN HCL 500MG TAB,SA [VA Product]
		assertTrue(set.contains("urn:rxnorm:860975")); // 24 HR Metformin hydrochloride 500 MG Extended Release Tablet
		
		set = vandf.getAncestorSet(urn);
		assertEquals(1, set.size());
		assertTrue(set.contains("urn:vandf:4021632")); // ORAL HYPOGLYCEMIC AGENTS,ORAL
	}
	
	@Test
	public void test3() throws SQLException {
		String urn = "urn:vandf:4014984";
		
		Concept c = eng.getConcept(urn);
		assertNotNull(c);
		assertEquals(urn, c.getURN());
		assertEquals("4014984", c.getCode());
		assertEquals("VANDF", c.getCodeSystem());
		assertEquals("METFORMIN HCL 500MG TAB,SA", c.getDescription());
		
		// check terms
		List<?> terms = c.getTerms();
		Map term = (Map) terms.get(0);
		assertNotNull(terms);
		assertEquals(2, terms.size());
		assertEquals("CD", term.get("tty"));
		assertEquals("ENG", term.get("lat"));
		assertEquals("A8446762", term.get("aui"));
		assertEquals("METFORMIN HCL 500MG TAB,SA", term.get("str"));
		term = (Map) terms.get(1);
		assertEquals(2, terms.size());
		assertEquals("AB", term.get("tty"));
		assertEquals("ENG", term.get("lat"));
		assertEquals("A15523915", term.get("aui"));
		assertEquals("METFORMIN HCL 500MG SA TAB", term.get("str"));
		
		// check equivalent set
		Set<String> set = c.getEquivalentSet();
		Map<String, String> map = c.getEquivalentMap();
		assertEquals(3, set.size());
		assertEquals(3, map.size());
		assertTrue(set.contains("urn:ndfrt:N0000162702"));
		assertTrue(set.contains("urn:rxnorm:860975"));
		assertTrue(set.contains("urn:vandf:4014984"));
		assertEquals("METFORMIN HCL 500MG TAB,SA [VA Product]", map.get("urn:ndfrt:N0000162702"));
		assertEquals("24 HR Metformin hydrochloride 500 MG Extended Release Tablet", map.get("urn:rxnorm:860975"));
		assertEquals("METFORMIN HCL 500MG TAB,SA", map.get("urn:vandf:4014984"));
		
		// check parent set
		set = c.getParentSet();
		map = c.getParentMap();
		assertEquals(1, set.size());
		assertEquals(1, map.size());
		assertTrue(set.contains("urn:vandf:4021632"));
		assertEquals("ORAL HYPOGLYCEMIC AGENTS,ORAL", map.get("urn:vandf:4021632"));
		
		// check ancestor set
		set = c.getAncestorSet();
		map = c.getAncestorMap();
		assertEquals(1, set.size());
		assertEquals(1, map.size());
	}
	
	@Test
	@Ignore // this test can take forever...
	public void testConceptIterator() throws SQLException, UnknownHostException, MongoException {
		Mongo m = new Mongo();
		DB db = m.getDB("vpr");
		DBCollection col = db.getCollection("umls2"); 
		MongoTermDataSource mtds = new MongoTermDataSource(col);
		EHCacheDataSource ehds = new EHCacheDataSource();
		
		int i=0;
		Iterator<String> itr = vandf.iterator("vandf");
		while (itr.hasNext()) {
			String urn = itr.next();
//			mtds.save(eng.getConceptData(urn));
			ehds.save(eng.getConceptData(urn));
			i++;
		}
		assertEquals(30610, i);
	}
	
	@Test
	public void testConcept() {
		Concept c1 = eng.getConcept("urn:vandf:4014984");
		Concept c2 = eng.getConcept("urn:rxnorm:860975");
		
		// test simple properties
		assertEquals("urn:vandf:4014984", c1.getURN());
		assertEquals("urn:rxnorm:860975", c2.getURN());
		assertEquals("METFORMIN HCL 500MG TAB,SA", c1.getDescription());
		assertEquals("24 HR Metformin hydrochloride 500 MG Extended Release Tablet", c2.getDescription());
		assertEquals("4014984", c1.getCode());
		assertEquals("860975", c2.getCode());
		assertEquals("VANDF", c1.getCodeSystem());
		assertEquals("RXNORM", c2.getCodeSystem());
		
//		c1.getTerms()
//		c1.getAncestorSet()
//		c1.getAncestorMap()
//		c1.getEquivalentSet()
//		c1.getEquivalentMap()
//		c1.getParentSet()
//		c1.getParentMap()
//		c1.getMappingTo(targetCodeSystem)
		
		// test isa/sameas/equas
		assertTrue(c1.equals(c2));
		assertTrue(c1.sameas(c2));
		assertTrue(c1.isa(c2));
		
		// test that only one instance is created per TermEng
		assertSame(c1, eng.getConcept("urn:vandf:4014984"));
		assertSame(c2, eng.getConcept("urn:rxnorm:860975"));
		assertSame(eng, c1.getEng());
		assertSame(eng, c2.getEng());
	}
}
