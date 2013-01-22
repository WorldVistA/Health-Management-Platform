package EXT.DOMAIN.cpe.vpr.frameeng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry.FrameLoader;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry.StaticFrameLoader;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrameTrigger.PatientObjectFieldChangedTrigger;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.pom.PatientEvent;

import EXT.DOMAIN.cpe.vpr.pom.jds.JdsGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.PatientImporter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class FrameRunnerTests {
	
	String brokerURL = "vm://hmp-test";
	ConnectionFactory fact = new ActiveMQConnectionFactory(brokerURL);
	JmsTemplate tpl = new JmsTemplate(fact);
	
	FrameRegistry registry;
	Frame testFrame;
	Patient p;
	
	@Before
	public void setup() throws Exception {
		FrameRegistryTests x = new FrameRegistryTests();
		x.setup();
		registry = x.registry;
		testFrame = x.testFrame;
		p = POMUtils.newInstance(Patient.class, PatientImporter.class.getResourceAsStream("patient.json"));
	}
	
	@Test
	public void testAMQ() throws JMSException {

		tpl.send("testmq", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage msg = session.createMapMessage();
				msg.setString("foo", "bar");
				return msg;
			}
		});
		
		tpl.setReceiveTimeout(100);
		
		Message msg = tpl.receive("testmq");
		assertNotNull(msg);
		assertTrue(msg instanceof MapMessage);
		assertEquals("bar", ((MapMessage) msg).getString("foo"));
	}
	
	/*
	@Test
	public void testPatientObjectJMSMessage() throws JMSException {
		String uid = "urn:va:F484:229:pat:229";
		
		// ensure the object exists
		assertNotNull(p);
		assertEquals(uid, p.getUid());
		
		// change a few things to get an update event
		p.setData("givenNames", "FOO");
		p.setData("familyName", "BAR");
		
		// ensure that appropriate events are fired
		List<PatientEvent<IPatientObject>> events = p.getEvents();
		assertNotNull(events);
		assertEquals(1, events.size());
		PatientEvent e = events.get(0);
		assertSame(PatientEvent.Type.UPDATE, e.getType());
		
		// push the event with the GenericJDSDao
		JdsGenericPatientObjectDAO.pushJMSEvents(p, tpl);
		
		// pop the same event
		ObjectMessage msg = (ObjectMessage) tpl.receive("vpr.events");
		assertNotNull(msg);
		assertEquals(uid, msg.getStringProperty("uid"));
		assertEquals(null, msg.getStringProperty("pid"));
		assertEquals("UPDATE", msg.getStringProperty("type"));
		assertEquals("EXT.DOMAIN.cpe.vpr.Patient{pids=[10104, 500;229, 666000004, F484;229]}", msg.getStringProperty("summary"));
		
		// ensure the event comes through
		Object body = msg.getObject();
		assertTrue(body instanceof PatientEvent);
		PatientEvent event = (PatientEvent) body;
		System.out.println(msg);
	}
	*/
	
	@Test
	@Ignore
	public void testxxx() throws Exception {
		// create the patient, modify the name
		p.setData("givenNames", "FOO");
		p.setData("familyName", "BAR");

		// generate an event
		PatientObjectFieldChangedTrigger<Patient> trig = new PatientObjectFieldChangedTrigger<Patient>(Patient.class, "givenNames", "familyName");

		// get the events and create the job
		List<IFrameEvent<?>> events = new ArrayList<IFrameEvent<?>>();
		events.addAll(p.getEvents());
		assertEquals(1, events.size());
		FrameJob job = registry.createJob(events);
		assertNotNull(job);
		assertEquals(1, job.size());
		FrameTask task = job.iterator().next();
		assertNotNull(task);
		assertSame(testFrame, task.getFrame());
		assertSame(trig, task.getFrameTrigger());
		assertSame(events.get(0), task.getTriggerEvent());
		
		// run the frame, check the fake results as expected
		task.exec();
		assertEquals(1, task.getActions().size());
//		assertTrue(myAction, task.getActions().get(0));
	}
}
