package org.osehra.cpe.vpr.queryeng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.osehra.cpe.vpr.queryeng.Query.StaticQuery;
import org.osehra.cpe.vpr.viewdef.QueryMapper.JoinQueryMapper;
import org.osehra.cpe.vpr.viewdef.QueryMapper.NestedViewDefQueryMapper;
import org.osehra.cpe.vpr.viewdef.QueryMapper.PerRowAppendMapper;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.osehra.cpe.vpr.viewdef.RenderTask.RowRenderSubTask;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderException;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2;
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2.JSONViewRenderer2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Ignore // THese all need to be rewritten
public class ViewDefRenderer2Tests {
	
	ViewDef vd;
	ViewDefRenderer2 vdr;
	ViewDefRenderer2 threaded_vdr;
	Map<String, Object> params = new HashMap<String, Object>();
	
	public static class TestViewDef extends ViewDef {
		@Override
		public void declareParam(String key, Object defaultVal) {
			super.declareParam(key, defaultVal);
		}
		
		@Override
		public boolean validate() {
			return true;
		}
	}
	
	/*
	 * Generates x rows and y fields of data
	 */
	public static class DataGeneratorQuery extends Query {

		private int cols;
		private int rows;
		private String prefix;
		private int delayHi, delayLo;

		public DataGeneratorQuery(String pk, String prefix, int rows, int cols) {
			super(pk, null);
			this.prefix = prefix;
			this.rows = rows;
			this.cols = cols;
		}
		
		public DataGeneratorQuery setDelay(int hi, int lo) {
			delayHi = hi;
			delayLo = lo;
			return this;
		}
		
		public DataGeneratorQuery setDelay(int ms) {
			delayLo = ms;
			return this;
		}
		
		private void doDelay() {
			try {
				if (delayLo > 0 && delayHi > 0) {
					// random delay
					double random = Math.random() * (delayHi - delayLo);
					Thread.sleep(Math.round(delayLo + random));
				} else if (delayLo > 0) {
					// static delay
					Thread.sleep(delayLo);
				} else {
					// no delay
				}
			} catch (InterruptedException ex) {
				// ignore
			}
		}
		
		@Override
		public void exec(RenderTask task) {
			doDelay();
			for (int i=0; i < this.rows; i++) {
				HashMap<String, Object> row = new HashMap<String, Object>();
				row.put(getPK(), "row" + i);
				for (int j=0; j < this.cols; j++) {
					row.put(this.prefix + j, i + "-" + j);
				}
				task.add(row);
			}
		}
	}
	
	public static class ErrorQuery extends Query {
		public ErrorQuery(String pk) {
			super(pk, null);
		}

		@Override
		public void exec(RenderTask renderer) throws Exception {
			throw new RuntimeException("Testing that an error is trapped as expected....");
		}
	}
	
	private static void verify(Table q) {
//		System.out.println(q);
		assertEquals(10, q.size());
		assertEquals(17, q.getColumns().size());
		
		//verify correct results
		for (int i=0; i < q.size(); i++) {
			assertEquals("row" + i, q.getCellIdx(i, "id"));
			
			for (int j=0; j < 5; j++) {
				assertEquals(i + "-" + j, q.getCellIdx(i, "1st"+j));
				assertEquals(i + "-" + j, q.getCellIdx(i, "2nd"+j));
				assertEquals("0-" + j, q.getCellIdx(i, "3rd"+j));
			}
		}
	}
	
	@Before
	public void before() {
		
		// setup a single and multi threaded renderer
		vdr = new ViewDefRenderer2();
		threaded_vdr = new ViewDefRenderer2(500, 5);
		
		vd = new TestViewDef();
		vd.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		vd.addQuery(new JoinQueryMapper(new DataGeneratorQuery("id", "2nd", 10, 5)));
		vd.addQuery(new PerRowAppendMapper(new DataGeneratorQuery("otherid", "3rd", 1, 5)));
	}
	
	@Test
	public void testDataGeneratorQuery() throws Exception {
		// test our basic data generator query
		ViewDefRenderer2 vdr = new ViewDefRenderer2();
		ViewDef viewdef = new TestViewDef();
		viewdef.addQuery(new DataGeneratorQuery("id", "col", 10, 10));
		Table q = vdr.render(viewdef, params);
		assertEquals(10, q.size());
		assertEquals(11, q.getColumns().size());
		
		// sample the rows/cols diagonally
		assertEquals("0-0", q.getCellIdx(0, "col0"));
		assertEquals("1-1", q.getCellIdx(1, "col1"));
		assertEquals("2-2", q.getCellIdx(2, "col2"));
		assertEquals("3-3", q.getCellIdx(3, "col3"));
		assertEquals("4-4", q.getCellIdx(4, "col4"));
		assertEquals("5-5", q.getCellIdx(5, "col5"));
		assertEquals("6-6", q.getCellIdx(6, "col6"));
		assertEquals("7-7", q.getCellIdx(7, "col7"));
		assertEquals("8-8", q.getCellIdx(8, "col8"));
		assertEquals("9-9", q.getCellIdx(9, "col9"));
	}
	
	@Test
	public void mainRenderingTest() throws Exception {
		// test our baseline ViewDefRenderer2 class to ensure all queries are returned appropriately
		RenderTask q = vdr.render(vd, null);
		verify(q); // verification is delegated to another function for reusablility
	}
	
	@Test
	public void testRendererReusability() throws Exception {
		// run the same viewdef through the same renderer 10 times
		for (int i=0; i < 10; i++) {
			mainRenderingTest();
		}
	}
	
	@Test
	public void testParallelRendering() throws InterruptedException {
		// test that I can render the same viewdef via a single renderer in parallel.
		ExecutorService exec = Executors.newFixedThreadPool(5);
		
		//setup a callable to be run 10 times in parallel
		Callable<Object> callme = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				mainRenderingTest();
				return null;
			}
		};
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int i=0; i < 10; i++) tasks.add(callme);
		
		// wait for them all to finish, should be quick
		exec.invokeAll(tasks, 5000, TimeUnit.MILLISECONDS); // 5s timeout
	}
	
	@Test
	public void testMultiThreadedRendering() throws Exception {
		// run the same main test through the multi-threaded renderer
		verify(threaded_vdr.render(vd));
	}
	
	/**
	 * by setting specific query execution delays, we should be able to predict how long
	 * it takes to render based on the number of threads running in parallel
	 */
	@Test @Ignore // Fails in Hudson
	public void testMultiThreadRenderingIsFaster() throws ViewDefRenderException {
		// new view def, each query should take 100ms to run
		ViewDef view = new TestViewDef();
		view.addQuery(new DataGeneratorQuery("id", "1st", 10, 5).setDelay(100));
		view.addQuery(new PerRowAppendMapper(new DataGeneratorQuery("otherid", "3rd", 1, 5).setDelay(100)));

		// ensure the expected thread counts
		assertEquals(0, vdr.getThreadCount());
		assertEquals(5, threaded_vdr.getThreadCount());
		
		// w/o multi-threading, we predict it will take approximately 1.1s
		// query1+(query2*10)
		long start = System.currentTimeMillis();
		vdr.render(view);
		assertEquals(1100, (System.currentTimeMillis() - start), 100); // 5% fudge factor
		
		// with multi-threading (5 threads) we predict it will be almost 5x quicker
		// query1+(query2*10/5)
		start = System.currentTimeMillis();
		threaded_vdr.render(view);
		assertEquals(300, (System.currentTimeMillis() - start), 45); // 15% fudge factor
	}
	
	
	/**
	 * By repeating the same simple test many times with some random-ness, we can spot concurrency and race conditions.
	 */
	@Test
	public void testRaceConditions() throws Exception {
		
		// should randomly take between 5-10ms to complete each one 
		ViewDef view = new TestViewDef();
		view.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		view.addQuery(new JoinQueryMapper(new DataGeneratorQuery("id", "2nd", 10, 5)));
		view.addQuery(new PerRowAppendMapper(new DataGeneratorQuery("otherid", "3rd", 1, 5).setDelay(5, 10)));
		
		// run the test 100 times, make sure it returns the same values each time
		for (int i=0; i < 100; i++) {
			Table t = threaded_vdr.render(view);
			verify(t);
		}
	}
	
	@Test
	public void testQueryRuntimeException() {
		ViewDef def = new TestViewDef();
		def.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		def.addQuery(new PerRowAppendMapper(new ErrorQuery("id")));
		
		try {
			Table q = vdr.render(def);
			Assert.fail("expected an exception somewhere");
		} catch (ViewDefRenderException ex) {
			// ignore, expected
		}
		
	}
	
	@Test
	public void testNestedViewDefs() throws ViewDefRenderException {
		ViewDef def = new TestViewDef();
		def.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		def.addQuery(new NestedViewDefQueryMapper("subview", vd));
		
		// we expect the usual 10 rows
		RenderTask results = vdr.render(def);
		assertEquals(10, results.size());
		for (int i=0; i < 10; i++) {
			// each one should have 1 column that can be verifyied as the standard results
			Map<String, Object> row = results.getRowIdx(i);
			assertTrue(row.containsKey("subview"));
			assertTrue(row.get("subview") instanceof Table);
			verify((Table) row.get("subview"));
		}
	}
	
	
	@Test
	public void testTimeout() {
		// should take 1s to render this
		ViewDef view = new TestViewDef();
		view.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		view.addQuery(new JoinQueryMapper(new DataGeneratorQuery("id", "2nd", 10, 5).setDelay(1000)));

		// we expect the renderer to timeout in 500ms
		assertEquals(500, threaded_vdr.getTimeoutMS());
		
		try {
			threaded_vdr.render(view);
			fail("Expected timeout");
		} catch (ViewDefRenderException ex) {
			// timeout exception should be thrown (wrapped by a RenderException)
			assertEquals(TimeoutException.class, ex.getCause().getClass());
		}
	}
	
	@Test
	public void testMergeAppender() throws Exception {
		ViewDef vd = new TestViewDef();
		vd.addQuery(ViewDefSamples.sq1);
		vd.addQuery(new JoinQueryMapper(ViewDefSamples.sq2));
		
		Table q = vdr.render(vd);
		assertEquals(3, q.size());
		
		assertEquals("a1", q.getCellIdx(0, "a"));
		assertEquals("b1", q.getCellIdx(0, "b"));
		assertEquals("c1", q.getCellIdx(0, "c"));
		
		assertEquals("x1", q.getCellIdx(0, "x"));
		assertEquals("y1", q.getCellIdx(0, "y"));
		assertEquals("z1", q.getCellIdx(0, "z"));
	}
	
	
	@Test
	public void testMergeAppenderFK() throws Exception {
		ViewDef vd = new TestViewDef();
		vd.addQuery(ViewDefSamples.sq2);
		vd.addQuery(new JoinQueryMapper(ViewDefSamples.sq1, "id2"));
		
		Table q = vdr.render(vd);
		assertEquals(3, q.size());
		
		assertEquals("a1", q.getCellIdx(0, "a"));
		assertEquals("b1", q.getCellIdx(0, "b"));
		assertEquals("c1", q.getCellIdx(0, "c"));
		
		assertEquals("x1", q.getCellIdx(0, "x"));
		assertEquals("y1", q.getCellIdx(0, "y"));
		assertEquals("z1", q.getCellIdx(0, "z"));
	}
	
	
	@Test
	public void testPerRowAppendMapper() throws Exception {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(Table.buildRow("foo","bar"));
		
		// build a query that will just copy the PK field
		Query q2 = new Query("idcopy", null) {
			@Override
			public void exec(RenderTask task) throws Exception {
				if (task instanceof RowRenderSubTask) {
					RowRenderSubTask subtask = (RowRenderSubTask) task;
					if (subtask.getRowIdx() >= 0) {
						task.add(Table.buildRow("idcopy", subtask.getParentRowKey()));
					}
				}
			}
		};
		
		// regular three rows, but the second query is appended to each row
		ViewDef vd = new TestViewDef();
		vd.addQuery(ViewDefSamples.sq1);
		vd.addQuery(new PerRowAppendMapper(new StaticQuery("foo", data)));
		vd.addQuery(new PerRowAppendMapper(q2));
		
		// should append the queries to each other, totaling 3 rows with 7 columns apeice
		Table q = vdr.render(vd);
		assertEquals(3, q.size());
		assertEquals(7, q.getRowIdx(0).size());
		assertEquals(7, q.getRowIdx(1).size());
		assertEquals(7, q.getRowIdx(2).size());
		
		// static data just gets added to each row
		assertEquals("bar", q.getCellIdx(0, "foo"));
		assertEquals("bar", q.getCellIdx(1, "foo"));
		assertEquals("bar", q.getCellIdx(2, "foo"));
		
		// q2 copies the PK
		assertEquals("1", q.getCellIdx(0, "idcopy"));
		assertEquals("2", q.getCellIdx(1, "idcopy"));
		assertEquals("3", q.getCellIdx(2, "idcopy"));
	}

	@Test
	public void testParams() throws ViewDefRenderException {
		// create a mock ViewDef with no a specific declared params
		TestViewDef vd = new TestViewDef();
		vd.declareParam("myparam", "mydefaultval"); 
		vd.addQuery(ViewDefSamples.sq1);
		vd.addQuery(ViewDefSamples.sq2);
		
		// run the renderer with some specified params, examine the working parameter set that is returned
		RenderTask result = vdr.render(vd, Table.buildRow("foo", 1, "bar", 2, "myparam", 3));
		Map<String, Object> params = result.getParams();
		
		// params will contain a bunch of default stuff
		assertTrue(params.containsKey("row.count"));
		assertTrue(params.containsKey("view.class"));
		assertTrue(params.containsKey("col.list"));
		
		// plus the param we declared
		assertTrue(params.containsKey("myparam"));
		
		// also all the params we specified, which overrite the defaults (if any)
		assertEquals(1, params.get("foo"));
		assertEquals(2, params.get("bar"));
		assertEquals(3, params.get("myparam"));
		
		// nested queries (tasks) don't hold any params by default themselves
		RenderTask subtask = result.getSubTasks().get(0);
		
		// but will delegate to the parent task to find them
		assertEquals(1, subtask.getParamObj("foo"));
		assertEquals(2, subtask.getParamObj("bar"));
		assertEquals(3, subtask.getParamObj("myparam"));
		
	}
	
	@Test
	public void testJSONRenderer() throws Exception {
		// render simple ViewDef with JSON
		ViewDef def = new TestViewDef();
		def.addQuery(new DataGeneratorQuery("id", "1st", 10, 5));
		def.addColumns(def.getPrimaryQuery(), "id");
		JSONViewRenderer2 jsonrender =  new JSONViewRenderer2();

		String jsonstr = (String) jsonrender.renderToString(def, null);
		assertNotNull(jsonstr);
		
		// parse json
		JsonNode jsonobj = new ObjectMapper().readTree(jsonstr);
		assertEquals(10, jsonobj.get("total").asInt());
		
		// check fields
		ArrayNode fields = (ArrayNode) jsonobj.path("metaData").get("fields");
		assertEquals(6, fields.size());
		
		// check columns, only exists in metadata
        ArrayNode cols = (ArrayNode) jsonobj.path("metaData").get("columns");
		assertEquals(1, cols.size());
		assertEquals("id", cols.get(0).get("text").asText());
		
		// check data
        ArrayNode data = (ArrayNode) jsonobj.get("data");
		assertEquals(10, data.size());
		assertEquals("row0", data.get(0).get("id").asText());
		assertEquals("row1", data.get(1).get("id").asText());
		assertEquals("row2", data.get(2).get("id").asText());
		assertEquals("row3", data.get(3).get("id").asText());
		assertEquals("row4", data.get(4).get("id").asText());
		assertEquals("row5", data.get(5).get("id").asText());
		assertEquals("row6", data.get(6).get("id").asText());
		assertEquals("row7", data.get(7).get("id").asText());
		assertEquals("row8", data.get(8).get("id").asText());
		assertEquals("row9", data.get(9).get("id").asText());
	}
	
	@Test
	@Ignore
	public void testLabProfileViewDef() throws Exception {
		// create the renderer class and register any resources needed by the viewdef (or let spring do all the work)
		ViewDefRenderer2 vdr = new ViewDefRenderer2();
		//vdr.addResource(new MongoTemplate(new Mongo(), "vpr"));
		
		// create a viewdef and the parameters to run it with
		Map<String, Object> params = Table.buildRow("pid", "1");
		ViewDef vd = new LabProfileViewDef();

		// render it
		System.out.println(vdr.render(vd, params));
	}
	
}
