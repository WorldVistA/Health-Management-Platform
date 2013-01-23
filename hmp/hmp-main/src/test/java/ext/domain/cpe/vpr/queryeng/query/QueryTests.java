package org.osehra.cpe.vpr.queryeng.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.osehra.cpe.vpr.frameeng.FrameJob;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.queryeng.Query;
import org.osehra.cpe.vpr.queryeng.Query.JSONFileQuery;
import org.osehra.cpe.vpr.queryeng.Query.StaticQuery;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class QueryTests {

	StaticQuery q;
	HashMap<String, Object> row1;
	HashMap<String, Object> row2;
	HashMap<String, Object> row3;
	
	@Before
    public void setUp() throws Exception {
    	q = new StaticQuery("id");
    	row1 = new HashMap<String, Object>();
    	row1.put("id", "foo");
    	row1.put("a", 1);
    	row1.put("b", 2);
    	row1.put("c", 3);

    	row2 = new HashMap<String, Object>();
    	row2.put("id", "bar");
    	row2.put("a", 4);
    	row2.put("b", 5);
    	row2.put("c", 6);
    	
    	row3 = new HashMap<String, Object>();
    	row3.put("id", "baz");
    	row3.put("a", 7);
    	row3.put("b", 8);
    	row3.put("c", 9);
	}
	
	/* TODO: Need to test:
		q.exec(renderer)
	*/

	
	@Test
	public void testConstructor() {
		// static query is QueryMode.NEVER, no query string, no FK
		assertNull(q.getQueryString());
		assertEquals(Query.QueryMode.NEVER, q.getQueryMode());
		assertEquals("id", q.getPK());
		assertNull(q.getFK());
		
		// should be empty
		assertEquals(0, q.size());
		assertTrue(q.isEmpty());
		assertEquals(0, q.getFullSize());
	}
	
	@Test
	public void testFK() {
		assertNull(q.getFK());
		q.setFK("asdf");
		assertEquals("asdf", q.getFK());
	}
	
	@Test
	@Ignore // Need to fix this
	public void testJSONFileQuery() throws Exception {
		URL url = ClassLoader.getSystemResource("org.osehra/cpe/vpr/frames/");
		File dir = new File(url.toURI());
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
		
		File[] files = dir.listFiles(JSONFileQuery.JSON_FILES);
		assertEquals(1, files.length);
		JSONFileQuery q = new JSONFileQuery("id", files);
		
		RenderTask task = new RenderTask(null, q); 
		q.exec(task);
		
		assertEquals(1, task.size());
		assertEquals("urn:icd:041.11", task.getCell("TEST_protocol_id", "icdCode"));
	}
}
