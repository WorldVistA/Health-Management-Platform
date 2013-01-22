package EXT.DOMAIN.cpe.vpr.dao.jms;

import EXT.DOMAIN.cpe.vpr.SyncError;
import EXT.DOMAIN.cpe.vpr.dao.ISyncErrorDao;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import EXT.DOMAIN.cpe.vpr.sync.SyncQueues;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.*;
import java.util.*;

public class JmsSyncErrorDao implements ISyncErrorDao {

    private JmsOperations jmsTemplate;

    private ConversionService conversionService;

    private QueueViewMBean errorQueueMBean;

    @Required
    public void setJmsTemplate(JmsOperations jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Required
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Required
    public void setErrorQueueMBean(QueueViewMBean errorQueueMBean) {
        this.errorQueueMBean = errorQueueMBean;
    }

    @Override
    public SyncError save(final SyncError error) {
        Map msg = conversionService.convert(error, Map.class);
        jmsTemplate.convertAndSend(SyncQueues.ERROR_QUEUE, msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                error.setId(message.getJMSMessageID());
                return message;
            }
        });
        return error;
    }

    @Override
    public Page<SyncError> findAll(final Pageable pageable) {
        // sort is ignored
        return jmsTemplate.browse(SyncQueues.ERROR_QUEUE, new BrowserCallback<Page<SyncError>>() {
            @Override
            public Page<SyncError> doInJms(Session session, QueueBrowser browser) throws JMSException {
                int total = pageable.getOffset();

                Enumeration<MapMessage> e = browser.getEnumeration();
                // advance to the right element in the queue
                for (int i = 0; i < pageable.getOffset(); i++) {
                    if (e.hasMoreElements()) {
                        e.nextElement();
                    } else {
                        throw new IllegalArgumentException("Requested offset " + pageable.getOffset() + " is greater than the number of error messages (" + i + ")");
                    }
                }
                // grab a page worth's of MapMessages and convert them to SyncError objects
                List<SyncError> messages = new ArrayList<SyncError>(pageable.getPageSize());
                for (int i = 0; i < pageable.getPageSize(); i++) {
                    if (e.hasMoreElements()) {
                        MapMessage msg = e.nextElement();
                        messages.add(conversionService.convert(msg, SyncError.class));
                    }
                }
                total += messages.size();

                int remaining = 0;
                while (e.hasMoreElements()) {
                    e.nextElement();
                    remaining++;
                }
                total += remaining;

                return new PageImpl<SyncError>(messages, pageable, total);
            }
        });
    }

    @Override
    public Integer countByPatientId(String pid) {
        int queueSize = jmsTemplate.browseSelected(SyncQueues.ERROR_QUEUE, getPatientIdSelector(pid), new BrowserCallback<Integer>() {
            @Override
            public Integer doInJms(Session session, QueueBrowser queueBrowser) throws JMSException {
                int count = Collections.list(queueBrowser.getEnumeration()).size();
                return count;
            }
        });
        return queueSize;
    }

    @Override
    public Integer countAllPatientIds() {
        return jmsTemplate.browse(SyncQueues.ERROR_QUEUE, new BrowserCallback<Integer>() {
            @Override
            public Integer doInJms(Session session, QueueBrowser browser) throws JMSException {
                Set<String> pids = new HashSet<String>();
                Enumeration<MapMessage> e = browser.getEnumeration();
                while (e.hasMoreElements()) {
                    MapMessage msg = e.nextElement();
                    pids.add(msg.getString(SyncMessageConstants.PATIENT_ID));
                }
                return pids.size();
            }
        });
    }

    @Override
    public Page<SyncError> findAllByPatientId(final String pid, final Pageable pageable) {
        // sort is ignored
        return jmsTemplate.browseSelected(SyncQueues.ERROR_QUEUE, getPatientIdSelector(pid), new BrowserCallback<Page<SyncError>>() {
            @Override
            public Page<SyncError> doInJms(Session session, QueueBrowser browser) throws JMSException {
                int total = pageable.getOffset();

                Enumeration<MapMessage> e = browser.getEnumeration();
                // advance to the right element in the queue
                for (int i = 0; i < pageable.getOffset(); i++) {
                    if (e.hasMoreElements()) {
                        e.nextElement();
                    } else {
                        throw new IllegalArgumentException("Requested offset " + pageable.getOffset() + " is greater than the number of error messages for patient '" + pid + "' (" + i + ")");
                    }
                }
                // grab a page worth's of MapMessages and convert them to SyncError objects
                List<SyncError> messages = new ArrayList<SyncError>(pageable.getPageSize());
                for (int i = 0; i < pageable.getPageSize(); i++) {
                    if (e.hasMoreElements()) {
                        MapMessage msg = e.nextElement();
                        messages.add(conversionService.convert(msg, SyncError.class));
                    }
                }
                total += messages.size();

                int remaining = 0;
                while (e.hasMoreElements()) {
                    e.nextElement();
                    remaining++;
                }
                total += remaining;

                return new PageImpl<SyncError>(messages, pageable, total);
            }
        });
    }

    @Override
    public int deleteByPatientId(String pid) {
        String selector = getPatientIdSelector(pid);
        int num = jmsTemplate.browseSelected(SyncQueues.ERROR_QUEUE, selector, new BrowserCallback<Integer>() {
            @Override
            public Integer doInJms(Session session, QueueBrowser browser) throws JMSException {
                return Collections.list(browser.getEnumeration()).size();
            }
        });
        for (int i = 0; i < num; i++) {
            jmsTemplate.receiveSelected(SyncQueues.ERROR_QUEUE, selector);
        }
        return num;
    }

    private String getPatientIdSelector(String pid) {
        return SyncMessageConstants.PATIENT_ID + "='" + pid + "'";
    }

    @Override
    public SyncError findOne(String id) {
        return jmsTemplate.browseSelected(SyncQueues.ERROR_QUEUE, "JMSMessageID='" + id + "'", new BrowserCallback<SyncError>() {
            @Override
            public SyncError doInJms(Session session, QueueBrowser browser) throws JMSException {
                Enumeration<MapMessage> e = browser.getEnumeration();
                if (!e.hasMoreElements()) return null;
                MapMessage msg = e.nextElement();
                return conversionService.convert(msg, SyncError.class);
            }
        });
    }

    @Override
    public List<SyncError> findAll() {
        return jmsTemplate.browse(SyncQueues.ERROR_QUEUE, new BrowserCallback<List<SyncError>>() {
            @Override
            public List<SyncError> doInJms(Session session, QueueBrowser browser) throws JMSException {
                List<SyncError> errors = new ArrayList<SyncError>();
                Enumeration<MapMessage> e = browser.getEnumeration();
                while (e.hasMoreElements()) {
                    MapMessage msg = e.nextElement();

                }
                return errors;
            }
        });
    }

    @Override
    public long count() {
        return errorQueueMBean.getQueueSize();
    }

    @Override
    public void delete(String id) {
        jmsTemplate.receiveSelected(SyncQueues.ERROR_QUEUE, "JMSMessageID='" + id + "'");
    }

    @Override
    public void delete(SyncError err) {
        delete(err.getId());
    }

    @Override
    public void deleteAll() {
        try {
            errorQueueMBean.purge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SyncError> findAll(Sort sort) {
        return findAll();  // sort is ignored
    }
}
