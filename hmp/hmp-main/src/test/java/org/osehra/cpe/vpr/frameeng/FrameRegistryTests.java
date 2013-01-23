package org.osehra.cpe.vpr.frameeng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.PatientAlert;
import org.osehra.cpe.vpr.frameeng.AdapterFrame.DroolsFrameAdapter;
import org.osehra.cpe.vpr.frameeng.FrameAction.BaseFrameAction;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.frameeng.FrameRegistry.DroolsFrameLoader;
import org.osehra.cpe.vpr.frameeng.FrameRegistry.FrameLoader;
import org.osehra.cpe.vpr.frameeng.FrameRegistry.FrameStats;
import org.osehra.cpe.vpr.frameeng.FrameRegistry.ProtocolFileFrameLoader;
import org.osehra.cpe.vpr.frameeng.FrameRegistry.StaticFrameLoader;
import org.osehra.cpe.vpr.frameeng.IFrameTrigger.PatientObjectFieldChangedTrigger;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.sync.vista.json.PatientImporter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FrameRegistryTests {
	
	public static class TestFrame extends Frame {
		
		public TestFrame() {
			setID("testframe");
			setName("Test Frame");
			addTrigger(new PatientObjectFieldChangedTrigger<Patient>(Patient.class, "givenName"));
		}

		@Override
		public void exec(FrameTask ctx) throws Exception {
			ctx.addAction(new EchoAction("Hello World Action"));
		}
	}
	
	private static class EchoAction extends BaseFrameAction {
		private String str;

		public EchoAction(String str) {
			this.str = str;
		}
		
		public String getStr() {
			return str;
		}
	}

	public FrameRegistry registry = null;
	public FrameLoader loader = null;
	public TestFrame testFrame;
	
	@Before
	public void setup() throws Exception {
		testFrame = new TestFrame();
		loader = new StaticFrameLoader(testFrame);
		registry = new FrameRegistry(loader);
	}
	
	@Test
	public void testFrameMeta() {
		Frame frame = new TestFrame();
		assertEquals("testframe", frame.getID());
		assertEquals("Test Frame", frame.getName());
		
		List<IFrameTrigger<?>> triggers = frame.getTriggers();
		assertEquals(1, triggers.size());
	}
	
	@Test
	@Ignore
	public void testProtocolFileFrameLoader() throws URISyntaxException {
		URL url = FrameRegistryTests.class.getResource("/org.osehra/cpe/vpr/frames/");
		ProtocolFileFrameLoader loader = new ProtocolFileFrameLoader(new File(url.toURI()));
		List<IFrame> frames = loader.load();
		assertEquals(6, frames.size());
		IFrame frame = frames.get(0);
		Map<String, Object> meta = frame.getMeta();
		assertEquals("TEST_protocol_id", frame.getID());
		assertTrue(new File(frame.getResource()).exists());
		assertEquals("MRSA Infection", frame.getName());
		assertEquals("Acute Conditions", meta.get("conditionStatus"));
	}
	
	@Test
	@Ignore
	public void testFrameRegistry() throws Exception {
		// confirm the frame loader/registry
		assertEquals(1, registry.size());
		assertEquals(false, registry.isEmpty());
		assertNotNull(registry.findByID("testframe"));
		assertEquals(1, registry.getFrameLoaders().size());
		assertSame(loader, registry.getFrameLoaders().get(0));
	}
	
	@Test
	@Ignore
	public void testDroolsFrameLoader() throws Exception {
		// build the knowledge base
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( ResourceFactory.newClassPathResource( "/org.osehra/cpe/vpr/frames/HelloWorldAlert.drl", getClass()), ResourceType.DRL );
		if ( kbuilder.hasErrors() ) {
			fail(kbuilder.getErrors().toString());
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		// add it to the frame registry
		FrameRegistry.DroolsFrameLoader loader = new DroolsFrameLoader(kbase);
		List<IFrame> frames = loader.load();
		assertEquals(1, frames.size());
		assertTrue(frames.get(0) instanceof DroolsFrameAdapter);
		DroolsFrameAdapter frame = (DroolsFrameAdapter) frames.get(0);
		
		// create the patient, modify the name
		Patient p = POMUtils.newInstance(Patient.class, PatientImporter.class.getResourceAsStream("patient.json"));
		p.setData("givenNames", "FOO");
		p.setData("familyName", "BAR");
		
		FrameRegistry registry = new FrameRegistry(loader);
		FrameRunner runner = new FrameRunner(registry);
		FrameJob job = runner.exec(new ArrayList<IFrameEvent<?>>(p.getEvents()));
		assertEquals(2, job.getActions().size());
		FrameAction action = job.getActions().get(1);
		System.out.println(job.getActions());
		assertTrue(action instanceof PatientAlert);
		PatientAlert aa = (PatientAlert) action;
		assertEquals("drools alert", aa.getTitle());
		assertEquals(p.getSsn(), aa.getDescription());
	}
	
	@Test
	public void testFrameStats() {
		FrameStats stats = registry.getFrameStats(testFrame);
		
		// initally null/empty/default
		assertNotNull(stats);
		assertEquals(0, stats.RUN_COUNT);
		assertEquals(0, stats.RUN_LAST);
		assertEquals(-1, stats.RUNTIME_AVG_MS);
		assertEquals(-1, stats.RUNTIME_MAX_MS);
		assertEquals(-1, stats.RUNTIME_MIN_MS);
		assertEquals(0, stats.RUNTIME_SUM_MS);
		
		// run the frame
		stats.run(1000);
		
		stats = registry.getFrameStats(testFrame);
		assertNotNull(stats);
		assertEquals(1, stats.RUN_COUNT);
		assertEquals(System.currentTimeMillis(), stats.RUN_LAST, 100);
		assertEquals(1000, stats.RUNTIME_AVG_MS);
		assertEquals(1000, stats.RUNTIME_MAX_MS);
		assertEquals(1000, stats.RUNTIME_MIN_MS);
		assertEquals(1000, stats.RUNTIME_SUM_MS);

		
		
	}
	
	
}
