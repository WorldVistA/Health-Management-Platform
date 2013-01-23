package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.queryeng.ColDef.QueryColDef;
import org.osehra.cpe.vpr.queryeng.Query.StaticQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ViewDefRenderingTests {
	private TestViewDef vd;
    private ConversionService mockConversionService;
	
	private static class TestViewDef extends ViewDef {
		Query q1;
		Query q2;
		public TestViewDef() {
			q1 = new StaticQuery("id");
			// query 1 returns col1
			HashMap<String, Object> row = new HashMap<String, Object>();
			row.put("id", "1");
			row.put("col1", "a1");
			q1.addRow(row);
			row = new HashMap<String, Object>();
			row.put("id", "2");
			row.put("col1", "a2");
			q1.addRow(row);
			row = new HashMap<String, Object>();
			row.put("id", "3");
			row.put("col1", "a3");
			q1.addRow(row);
			addQuery(q1);
			addColumn(new QueryColDef(q1, "col1"));
			
			
			// query 2 returns col2
			q2 = new StaticQuery("id");
			row = new HashMap<String, Object>();
			row.put("id", "1");
			row.put("col2", "b1");
			q2.addRow(row);
			row = new HashMap<String, Object>();
			row.put("id", "2");
			row.put("col2", "b2");
			q2.addRow(row);
			row = new HashMap<String, Object>();
			row.put("id", "3");
			row.put("col2", "b3");
			q2.addRow(row);
			addQuery(q2);
			addColumn(new QueryColDef(q2, "col2"));
		}
	}
	
	@Before
    public void setUp() throws Exception {
    	vd = new TestViewDef();
        mockConversionService = mock(ConversionService.class);
    }
	
	@Test
	@Ignore
	public void testRender() {
		ViewDefRenderer renderer = new ViewDefRenderer(vd);
		
		// render should essentially return a simple table
		Query sq = renderer.renderToQuery();
		assertEquals(3, sq.getFullSize());
		assertEquals("id", sq.getPK());
		assertTrue(sq.getColumns().contains("col1"));
		assertTrue(sq.getColumns().contains("col2"));
	}
	
	@Test
	public void testGetColumns() {
		// get the columns out of the test object
		assertEquals(2, vd.getColumns().size());
		assertNotNull(vd.getColumn("col1"));
		assertNotNull(vd.getColumn("col2"));
		assertSame(vd.getColumn("col1").getQuery(), vd.q1);
		assertSame(vd.getColumn("col2").getQuery(), vd.q2);
		
		// should be returned in natural order
		assertEquals("col1", vd.getColumns().get(0).getKey());
		assertEquals("col2", vd.getColumns().get(1).getKey());
	}
	
	@Test
	public void testAddColumnsQuery() {
		vd.addColumns(vd.q1, "asdf1", "asdf2", "asdf3");
		
		// assert that it actually added3 separate columns
		assertNotNull(vd.getColumn("asdf1"));
		assertNotNull(vd.getColumn("asdf2"));
		assertNotNull(vd.getColumn("asdf3"));
		
		// they should be QueryColDefs
		assertTrue(vd.getColumn("asdf1") instanceof QueryColDef);
		assertTrue(vd.getColumn("asdf2") instanceof QueryColDef);
		assertTrue(vd.getColumn("asdf3") instanceof QueryColDef);
	}
}
