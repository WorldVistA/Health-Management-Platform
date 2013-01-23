package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.vpr.frameeng.FrameRunner.DefaultFrameActionRunner;
import org.osehra.cpe.vpr.frameeng.FrameRunner.JDSSaveActionRunner;
import org.osehra.cpe.vpr.frameeng.FrameRunner.PatientObjectActionRunner;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.pom.PatientEvent;
import org.osehra.cpe.vpr.pom.jds.JdsTemplate;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
import org.osehra.cpe.vpr.sync.SyncMessageUtils;
import org.osehra.cpe.vpr.sync.SyncQueues;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * THis is a wrapper arounda  frame runner that recives JMS messages and runs then in the frame runner.
 * 
 * This is intended primarily for asyncronous frame/event execution.  For syncronous execution, use the FrameRunner directly.
 * 
 * Should handle:
 * - Transactions
 * - Error handling
 * - Multi-threaded (multiple consumers)
 * 
 * 
 * TODO: Due to the message grouping that we will ultimately need, the Spring JMS features may not work and we may need to write a direct
 * JMS consumer.
 * TODO: How to inject clock events?
 * 
 */
public class FrameEng implements MessageListener {
	private IGenericPatientObjectDAO dao; 
	private IPatientDAO patdao;
	private FrameRunner runner;
	private JmsTemplate jms;

	@Autowired
	public FrameEng(FrameRegistry registry, JmsTemplate jms, JdsTemplate tpl, IGenericPatientObjectDAO dao, IPatientDAO patdao) {
		this.dao = dao;
		this.jms = jms;
		this.patdao = patdao;
		this.runner = new FrameRunner(registry, new JDSSaveActionRunner(tpl), new DefaultFrameActionRunner(), new PatientObjectActionRunner(dao));
	}
	
	public FrameRunner getRunner() {
		return this.runner;
	}
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof ObjectMessage) {
			try {
				Object obj = ((ObjectMessage) msg).getObject();
				if (obj instanceof PatientEvent) {
					PatientEvent<?> evt = (PatientEvent<?>) obj;
					evt.reconsitute(dao, patdao);
					runner.exec((IFrameEvent<?>) obj);
				}
			} catch (Exception e) {
				sendErrorMessage(new HashMap<String, Object>(), e);
				System.err.println("Error processing event: ");
				e.printStackTrace();
			}
		}
	}
	
	private void sendErrorMessage(final Map<String, Object> msg, Throwable t) {
		final String pid = (String) msg.get(SyncMessageConstants.PATIENT_ID);
		jms.convertAndSend(SyncQueues.ERROR_QUEUE, SyncMessageUtils.createErrorMessage(msg, t), new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setStringProperty(SyncMessageConstants.PATIENT_ID, pid);
				return message;
			}
		});
	}
	
}
