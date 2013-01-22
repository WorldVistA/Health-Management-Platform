package EXT.DOMAIN.cpe.vpr.frameeng;

import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.IFrameActionExec;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameAction.IPatientSerializableAction;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.PatientEvent;
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class FrameRunner {
	private static String QUEUE_NAME = "vpr.events";
	private static String JMSXGROUP_ID = "JMSXGroupID";
	private static String JMSXGROUP_SEQ = "JMSXGroupSeq";
	
	@Autowired
	protected JdsTemplate tpl;
	
	@Autowired
	protected JmsTemplate jms;
	
	private FrameActionRunner[] actionRunners;
	private FrameRegistry registry;
	
	@Autowired
	public FrameRunner(FrameRegistry registry, FrameActionRunner... actionRunners) {
		this.registry = registry;
		this.actionRunners = actionRunners;
	}
	
	public FrameRegistry getRegistry() {
		return registry;
	}
	
	public void pushEvents(IPatientObject item) {
		List<PatientEvent<IPatientObject>> events = item.getEvents();
		if (events == null || events.size() == 0) {
			return;
		}
		
		for (final PatientEvent<IPatientObject> evt : events) {
			jms.send(QUEUE_NAME, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					Message msg = session.createObjectMessage(evt);
					IPatientObject obj = evt.getSource();
					msg.setStringProperty("uid", obj.getUid());
					msg.setStringProperty("pid", obj.getPid());
					msg.setStringProperty("type", evt.getType().toString());
					msg.setStringProperty("summary", obj.getSummary());
//					msg.setJMSCorrelationID(arg0)
//					msg.setJMSDeliveryMode(arg0)
//					msg.setJMSType(arg0)
					return msg;
				}
			});
		}
	}	
	
	public FrameJob exec(IFrameEvent<?>... events) throws Exception {
		return exec(Arrays.asList(events));
	}
	
	public FrameJob exec(List<IFrameEvent<?>> events) throws Exception {
		// get the frames to run
		FrameJob job = registry.createJob(events);
		job.exec();
		
		// process the actions
		if (this.actionRunners != null && this.actionRunners.length > 0) {
			for (FrameAction action : job.getActions()) {
				for (FrameActionRunner runner : this.actionRunners) {
					runner.exec(job, action);
				}
			}
		}
		
		return job;
	}
	
	protected void loadParamVals(FrameTask task) {
		IFrameEvent evt = task.getTriggerEvent();
		IFrame frame = task.getFrame();
		if (tpl != null && evt instanceof PatientEvent) {
			String pid = ((PatientEvent) evt).getPID();
			Map data = tpl.getForMap("/vpr/" + pid + "/urn:va:::frame:" + frame.getID());
			if (data != null && data.size() > 0) {
				task.setParams(data);
			}
		}
	}
	
	public abstract static class FrameActionRunner {
		public abstract void exec(FrameJob job, FrameAction action);
	}
	
	public static class DefaultFrameActionRunner extends FrameActionRunner {
		@Override
		public void exec(FrameJob job, FrameAction action) {
			if (action instanceof IFrameActionExec) {
				IFrameActionExec a = (IFrameActionExec) action;
				a.exec(job);
			}
		}
	}
	
	public static class JDSSaveActionRunner extends FrameActionRunner  {
		private JdsTemplate tpl;

		public JDSSaveActionRunner(JdsTemplate tpl) {
			this.tpl = tpl;
		}
		
		@Override
		public void exec(FrameJob job, FrameAction action) {
			if (action instanceof IPatientSerializableAction) {
				IPatientSerializableAction a = (IPatientSerializableAction) action;
				tpl.postForLocation("/vpr/" + a.getPid(), a);
			}
		}
	}
	
	public static class PatientObjectActionRunner extends FrameActionRunner  {
		private IGenericPatientObjectDAO dao;

		public PatientObjectActionRunner(IGenericPatientObjectDAO dao) {
			this.dao = dao;
		}
		
		@Override
		public void exec(FrameJob job, FrameAction action) {
			if (action instanceof IPatientObject) {
				dao.save((IPatientObject) action);
			}
		}
	}
	
	
	/**
	 * Idea: this could inject ClockEvents at set times?
	 * TODO: Maybe the spring scheduling mechanism can do this instead and inject into 
	 * an existing FrameRunner instead.
	 */
	public abstract static class ClockFrameRunner extends FrameRunner {
		public ClockFrameRunner(FrameRegistry registry, FrameActionRunner proc) {
			super(registry, proc);
		}
	}

	/**
	 * Idea: this could be the replacement for ViewDefRenderer?
	 */
	public abstract static class ViewDefRunner extends FrameRunner {

		public ViewDefRunner(FrameRegistry registry, FrameActionRunner proc) {
			super(registry, proc);
		}
	}

}
