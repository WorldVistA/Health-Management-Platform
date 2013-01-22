package EXT.DOMAIN.cpe.vpr.queryeng.query;

import static org.junit.Assert.*;
import static EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria.where;
import EXT.DOMAIN.cpe.vpr.queryeng.Table;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef.QueryFieldTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO: Test conditional parameters
 * @author brian
 */
public class QueryDefTests {
	
	List<Map<String,Object>> data;
	
	@Before
	public void setup() {
		// create a data structure with 3 rows of 3 fields
		data = new ArrayList<Map<String, Object>>();
		data.add(Table.buildRow("row", 1, "field1", "r1f1", "field2", "r1f2", "field3", "r1f3"));
		data.add(Table.buildRow("row", 2, "field1", "r2f1", "field2", "r2f2", "field3", "r2f3"));
		data.add(Table.buildRow("row", 3, "field1", "r3f1", "field2", "r3f2", "field3", "r3f3"));
	}
	
	@Test
	public void testIncludes() {
		// only include field 1,3
		QueryDef qd = new QueryDef();
		qd.fields().include("field1","field3");
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		for (Map<String, Object> row : data) {
			assertEquals(2, row.size());
			assertTrue(row.containsKey("field1"));
			assertFalse(row.containsKey("field2"));
			assertTrue(row.containsKey("field3"));
		}
	}
	
	@Test
	public void testExcludes() {
		// exclude fields 1,3
		QueryDef qd = new QueryDef();
		qd.fields().exclude("row", "field1","field3");
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		for (Map<String, Object> row : data) {
			assertEquals(1, row.size());
			assertFalse(row.containsKey("field1"));
			assertTrue(row.containsKey("field2"));
			assertFalse(row.containsKey("field3"));
		}
	}
	
	@Test
	public void testMixedIncludesExcludes() {
		// exclude fields 1,3
		QueryDef qd = new QueryDef();
		qd.fields().exclude("field1","field3").include("field2");
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		for (Map<String, Object> row : data) {
			assertEquals(1, row.size());
			assertFalse(row.containsKey("field1"));
			assertTrue(row.containsKey("field2"));
			assertFalse(row.containsKey("field3"));
		}
	}
	
	@Test
	public void testQueryDefAlias() {
		// alias field1-3 as fielda-c and apply the query filters
		QueryDef qd = new QueryDef();
		qd.fields().alias("field1", "fielda").alias("field2", "fieldb").alias("field3", "fieldc");
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		for (Map<String, Object> row : data) {
			assertEquals(4, row.size());
			assertTrue(row.containsKey("fielda"));
			assertTrue(row.containsKey("fieldb"));
			assertTrue(row.containsKey("fieldc"));
			assertFalse(row.containsKey("field1"));
			assertFalse(row.containsKey("field2"));
			assertFalse(row.containsKey("field3"));
		}
	}
	
	@Test
	public void testAliasIncludes() {
		// aliases should work even if you dont explicitly include the field
		QueryDef qd = new QueryDef();
		qd.fields().include("field1").alias("field2", "fieldb");
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		for (Map<String, Object> row : data) {
			assertEquals(2, row.size());
			assertTrue(row.containsKey("field1"));
			assertTrue(row.containsKey("fieldb"));
			assertFalse(row.containsKey("field2"));
			assertFalse(row.containsKey("field3"));
		}
	}
	
	@Test
	public void testFieldTransform() {
		// define a couple field transformers
		QueryDef qd = new QueryDef();
		qd.fields().exclude("row");
		qd.fields().transform("field1", new QueryFieldTransformer() {
			@Override
			public Object transform(String field, Object value) {
				return "prefix:" + value;
			}
		});
		qd.fields().transform("field2", new QueryFieldTransformer.ReplaceTransformer("f2","foo"));
		qd.applyFilters(data, null);
		
		// check results
		assertEquals(3, data.size());
		Map<String, Object> row1 = data.get(0);
		Map<String, Object> row2 = data.get(1);
		Map<String, Object> row3 = data.get(2);
		assertEquals(3, row1.size());
		assertEquals(3, row2.size());
		assertEquals(3, row3.size());
		
		assertEquals("prefix:r1f1", row1.get("field1"));
		assertEquals("r1foo", row1.get("field2"));
		assertEquals("r1f3", row1.get("field3"));
		
		assertEquals("prefix:r2f1", row2.get("field1"));
		assertEquals("r2foo", row2.get("field2"));
		assertEquals("r2f3", row2.get("field3"));
		
		assertEquals("prefix:r3f1", row3.get("field1"));
		assertEquals("r3foo", row3.get("field2"));
		assertEquals("r3f3", row3.get("field3"));
	}
	
	
	@Test
	public void testSorting() {
		QueryDef qd = new QueryDef();
		qd.sort().desc("field1").asc("row");
		
		// add an extra row with same values to test multi-value sorting...
		data.add(Table.buildRow("row", 4, "field1", "r3f1", "field2", "r3f2", "field3", "r3f3"));
		
		// get row refs before sorting...
		Map<String, Object> row1 = data.get(0), row2 = data.get(1), row3 = data.get(2), row4 = data.get(3);
		qd.applySorting(data, null);
		
		// check results
		assertEquals(4, data.size());
		assertSame(row3, data.get(0));
		assertSame(row4, data.get(1));
		assertSame(row2, data.get(2));
		assertSame(row1, data.get(3));
	}
	
	@Test
	public void testAND() {
		QueryDef qd = new QueryDef();
		
		// 3 criteria
		qd.addCriteria(where("field1").is("r1f1").and("field2").is("r1f2").and("field3").is("r1f3"));
		qd.applyFilters(data, null);
		
		// expect row 1
		assertEquals(1, data.size());
		assertEquals("r1f1", data.get(0).get("field1"));
	}
	
	@Test
	@Ignore // Broken
	public void testOR() {
		QueryDef qd = new QueryDef();
		
		// 3 criteria
		qd.addCriteria(where("field1").is("r1f1").orOperator(where("field2").is("r2f2"), where("field3").is("r2f3")));
		qd.applyFilters(data, null);
		
		// expect row 1,2,3
		assertEquals(3, data.size());
		assertEquals("r1f1", data.get(0).get("field1"));
	}
	
	@Test
	@Ignore// not implemented yet
	public void testNOR() {
		
	}

	@Test
	@Ignore // these are broken
	public void testToURL2() {
		QueryDef qd = new QueryDef();

		// this doesn't work... probably should?
		qd.addCriteria(QueryDefCriteria.server("field").gte("aaa"));
		qd.addCriteria(QueryDefCriteria.server("field").gte("bbb"));
		
		// but this does work
		qd.addCriteria(QueryDefCriteria.server("field").gte("aaa").lte("bbb"));
		
		// test IN + NIN (this is broken)
		qd.addCriteria(QueryDefCriteria.server("field").in(1,2,3).nin("a","b","c"));
		String url = qd.toURL(null,  123,  456);
		assertEquals("/vpr/666/index/foo-idx?range=bar&filter=in(field,1,2,3),nin(field,\"a\",\"b\",\"c\")&start=123&limit=456", url);

	}
	
	@Test
	public void testToURL() {
		String prefix = "/vpr/666/index/foo-idx?range=bar";
		String suffix = "&start=123&limit=456";
		Map<String, Object> params = Table.buildRow("pid", "666");
		QueryDef qd = new QueryDef();
		qd.namedIndexValue("foo-idx", "bar");
		
		// with no filters defined...
		String url = qd.toURL(params,  123,  456);
		assertEquals(prefix + suffix, url);
		
		// test eq
		qd.addCriteria(QueryDefCriteria.server("field").is(1));
		url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=eq(field,1)" + suffix, url);
		
		// test GT + LT
		qd.addCriteria(QueryDefCriteria.server("field").gt("aaa").lt("bbb"));
		url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=gt(field,\"aaa\"),lt(field,\"bbb\")" + suffix, url);
		
		// test LTE + GTE
		qd.addCriteria(QueryDefCriteria.server("field").gte("aaa").lte("bbb"));
		url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=gte(field,\"aaa\"),lte(field,\"bbb\")" + suffix, url);
		
		// test IN + NIN
		qd.addCriteria(QueryDefCriteria.server("field").in(1,2,3));
		url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=in(field,[1,2,3])" + suffix, url);

		// test BETWEEN
		qd.addCriteria(QueryDefCriteria.server("field").between(1,10));
		url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=gte(field,1),lte(field,10)" + suffix, url);
		
		qd.applyFilters(data, params);
		assertEquals(3, data.size());
	}
	
	@Test
	public void testToURLConditionals() {
		Map<String, Object> params = Table.buildRow("pid", "666", "foo", "bar");
		String prefix = "/vpr/666/index/foo-idx?range=bar";
		String suffix = "&start=123&limit=456";
		QueryDef qd = new QueryDef();
		qd.namedIndexValue("foo-idx", "bar");
		
		// test conditionals
		qd.addCriteria(QueryDefCriteria.server("field").is("?:foo"));
		qd.addCriteria(QueryDefCriteria.server("field2").gte("?:bar"));
		qd.addCriteria(QueryDefCriteria.server("field3").gte(":baz"));
		String url = qd.toURL(params,  123,  456);
		assertEquals(prefix + "&filter=eq(field,\"bar\")" + suffix, url);
	}
	
	@Test
	public void testSkipAndLimit() {
		QueryDef qd = new QueryDef();
		
		// test defaults
		assertEquals(0, qd.getSkip());
		assertEquals(100, qd.getLimit());

		// test changes
		qd.skip(123).limit(456);
		assertEquals(123, qd.getSkip());
		assertEquals(456, qd.getLimit());
	}
	
	@Test
	public void testFilterIS() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("field1").is("r3f1"));
		qd.applyFilters(data, null);
		
		// expect only the last row to be returned
		assertEquals(1, data.size());
		assertEquals("r3f1", data.get(0).get("field1"));
	}
	
	@Test
	public void testFilterVarIS() {
		QueryDef qd = new QueryDef();
		
		Map<String, Object> params = Table.buildRow("filter1", "r3f1", "filter2", "r3f2");
		
		// filter1 should be applied (since its non-conditional)
		// filter2 should be applied (since its conditional and specified)
		// filter3 should be ignored (since its conditional and not-specified)
		qd.addCriteria(QueryDefCriteria.where("field1").is(":filter1"));
		qd.addCriteria(QueryDefCriteria.where("field2").is("?:filter2"));
		qd.addCriteria(QueryDefCriteria.where("field3").is("?:filter3"));
		qd.applyFilters(data, params);
		
		// expect only the last row to be returned
		assertEquals(1, data.size());
		assertEquals("r3f1", data.get(0).get("field1"));
	}

	
	@Test
	public void testFilterNE() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("field1").ne("r3f1"));
		qd.applyFilters(data, null);
		
		// expect rows 1,2 to be returned
		assertEquals(2, data.size());
		assertEquals("r1f1", data.get(0).get("field1"));
		assertEquals("r2f1", data.get(1).get("field1"));
	}
	
	@Test
	public void testFilterIN() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("field1").in("r1f1","r3f1"));
		qd.applyFilters(data, null);
		
		// expect rows 1,3 to be returned
		assertEquals(2, data.size());
		assertEquals("r1f1", data.get(0).get("field1"));
		assertEquals("r3f1", data.get(1).get("field1"));
	}
	
	@Test
	public void testFilterNIN() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("field1").nin("r1f1","r3f1"));
		qd.applyFilters(data, null);
		
		// expect row 2 to be returned
		assertEquals(1, data.size());
		assertEquals("r2f1", data.get(0).get("field1"));
	}
	
	@Test
	public void testFilterGTE() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("row").gte(2));
		qd.applyFilters(data, null);
		
		// expect rows 2,3 to be returned
		assertEquals(2, data.size());
		assertEquals(2, data.get(0).get("row"));
		assertEquals(3, data.get(1).get("row"));
	}
	
	@Test
	public void testFilterLTE() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("row").lte(2));
		qd.applyFilters(data, null);
		
		// expect rows 1,2 to be returned
		assertEquals(2, data.size());
		assertEquals(1, data.get(0).get("row"));
		assertEquals(2, data.get(1).get("row"));
	}
	
	@Test
	public void testFilterGT() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("row").gt(2));
		qd.applyFilters(data, null);
		
		// expect row 3 to be returned
		assertEquals(1, data.size());
		assertEquals(3, data.get(0).get("row"));
	}
	
	@Test
	public void testFilterLT() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("row").lt(2));
		qd.applyFilters(data, null);
		
		// expect row 1 to be returned
		assertEquals(1, data.size());
		assertEquals(1, data.get(0).get("row"));
	}
	
	@Test
	public void testFilterBETWEEN() {
		QueryDef qd = new QueryDef();
		
		// check the is
		qd.addCriteria(QueryDefCriteria.where("row").between(1,2));
		qd.applyFilters(data, null);
		
		// expect rows 1,2 to be returned
		assertEquals(2, data.size());
		assertEquals(1, data.get(0).get("row"));
		assertEquals(2, data.get(1).get("row"));
	}
}
