package org.osehra.cpe.vpr.queryeng;

import static org.junit.Assert.*;

import org.osehra.cpe.vpr.queryeng.Query.QueryMode;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;

import java.util.Map;

import org.junit.Test;

public class ViewDefTests {
	
	public static class EmptyTestViewDef extends ViewDef {
		public EmptyTestViewDef() {
		}
	}
	
	@HMPAppInfo(value="foo", title="Foo Bar")
	private static class TestViewDef extends ViewDef {
		public TestViewDef() {
		}
	}

	@Test
	public void testAnnotations() {
		ViewDef vd = new EmptyTestViewDef();
		Map<String, Object> appInfo = vd.getAppInfo();
		
		// without the annotation, getAppInfo() type returns org.osehra.cpe.viewdef 
		assertEquals("org.osehra.cpe.viewdef", appInfo.get("type"));
		
		// however the annotation should over-ride its return value
		vd = new TestViewDef();
		appInfo = vd.getAppInfo();
		assertEquals("foo", appInfo.get("type"));
		
		// also, the title annotation is a backup mechanism if the title is not declared in the ViewParam
		assertEquals("Foo Bar", appInfo.get("name"));
	}
	
	@Test
	public void testColumns() {
		ViewDef vd = new EmptyTestViewDef();
		
		// columns are initally empty
		assertEquals(0, vd.getColumns().size());
		
		// adding a column
		ColDef c = new ColDef.QueryColDef(null, "foo");
		assertSame(c, vd.addColumn(c));
		assertSame(c, vd.getColumn("foo"));
		assertEquals(1, vd.getColumns().size());
	}
	
	/*
	@Test
	public void testQueries() {
		ViewDef vd = new EmptyTestViewDef();
		
		// initally there are no queries
		assertEquals(0, vd.getQueries(null).size());
		assertNull(vd.getPrimaryQuery());
		
		// add a query
		Query q = new JDSQuery("pk", null);
		assertSame(q, vd.addQuery(q));
		assertEquals(1, vd.getQueries(null).size());
		assertSame(q, vd.getQueries(null).get(0));
		assertSame(q, vd.getPrimaryQuery());
	}
	*/
}
