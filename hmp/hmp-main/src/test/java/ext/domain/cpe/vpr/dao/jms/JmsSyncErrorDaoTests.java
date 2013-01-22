package EXT.DOMAIN.cpe.vpr.dao.jms;

import EXT.DOMAIN.cpe.test.mock.jms.ReturnsBrowserCallbackArgument;
import EXT.DOMAIN.cpe.test.mock.jms.ReturnsConvertedMapMessage;
import EXT.DOMAIN.cpe.vpr.SyncError;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import EXT.DOMAIN.cpe.vpr.sync.SyncQueues;
import EXT.DOMAIN.cpe.vpr.sync.convert.MapMessageToSyncError;
import EXT.DOMAIN.cpe.vpr.sync.convert.SyncErrorToMap;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JmsSyncErrorDaoTests {

    private Session mockJmsSession;
    private QueueBrowser mockJmsQueueBrowser;
    private JmsOperations mockJmsTemplate;
    private ConversionService mockConversionService;
    private JmsSyncErrorDao dao;
    private MapMessageToSyncError mapMessageToSyncError = new MapMessageToSyncError();
    private QueueViewMBean mockQueueMBean;

    @Before
    public void setUp() throws Exception {
        mockJmsSession = mock(Session.class);
        mockJmsQueueBrowser = mock(QueueBrowser.class);
        mockJmsTemplate = mock(JmsOperations.class);
        mockConversionService = mock(ConversionService.class);
        mockQueueMBean = mock(QueueViewMBean.class);

        dao = new JmsSyncErrorDao();
        dao.setConversionService(mockConversionService);
        dao.setJmsTemplate(mockJmsTemplate);
        dao.setErrorQueueMBean(mockQueueMBean);

        when(mockJmsTemplate.browse(eq(SyncQueues.ERROR_QUEUE), any(BrowserCallback.class))).thenAnswer(new ReturnsBrowserCallbackArgument<Object>(mockJmsSession, mockJmsQueueBrowser));
        when(mockConversionService.convert(any(MapMessage.class), eq(SyncError.class))).thenAnswer(new ReturnsConvertedMapMessage<SyncError>(mapMessageToSyncError));
    }

    @Test
    public void testCount() throws Exception {
        when(mockQueueMBean.getQueueSize()).thenReturn(42L);
        assertThat(dao.count(), is(42L));
    }

    @Test
    public void testFindAllPaginated() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(100);
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        Page<SyncError> errors = dao.findAll(new PageRequest(1, 20, Sort.Direction.DESC, "dateCreated"));
        assertThat(errors.getNumberOfElements(), is(20));
        assertThat(errors.getTotalElements(), is(100L));
    }

    @Test
    public void testFindAllPaginatedLessThanPageSize() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(15);
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        Page<SyncError> errors = dao.findAll(new PageRequest(0, 20, Sort.Direction.DESC, "dateCreated"));
        assertThat(errors.getNumberOfElements(), is(15));
        assertThat(errors.getTotalElements(), is(15L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllPaginatedRequestedOffsetGreaterThanQueueSize() throws Exception {
        List<MapMessage> messages = createMockErrorMsgs(15);
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        Page<SyncError> errors = dao.findAll(new PageRequest(1, 20, Sort.Direction.DESC, "dateCreated"));
    }

    @Test
    public void testFindAllByPatientIdPaginated() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(5);
        // set them all to the same patient id
        for (MapMessage msg : messages) {
            when(msg.getString(SyncMessageConstants.PATIENT_ID)).thenReturn("23");
        }
        when(mockJmsTemplate.browseSelected(eq(SyncQueues.ERROR_QUEUE), eq(SyncMessageConstants.PATIENT_ID + "='23'"), any(BrowserCallback.class))).thenAnswer(new ReturnsBrowserCallbackArgument<Object>(mockJmsSession, mockJmsQueueBrowser));
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        Page<SyncError> errors = dao.findAllByPatientId("23", new PageRequest(0, 10, Sort.Direction.DESC, "dateCreated"));
        assertThat(errors.getNumberOfElements(), is(5));
        assertThat(errors.getTotalElements(), is(5L));
    }

    @Test
    public void testDeleteAll() throws Exception {
        dao.deleteAll();

        verify(mockQueueMBean).purge();
    }

    @Test
    public void testDeleteSyncError() throws JMSException {
        SyncError e = new SyncError();
        e.setId("ID:" + 3); // not an actual JMSMessageID, but close enough
        dao.delete(e);

        verify(mockJmsTemplate).receiveSelected(SyncQueues.ERROR_QUEUE, "JMSMessageID='ID:3'");
    }

    @Test
    public void testDeleteById() throws JMSException {
        dao.delete("ID:" + 3);  // not an actual JMSMessageID, but close enough

        verify(mockJmsTemplate).receiveSelected(SyncQueues.ERROR_QUEUE, "JMSMessageID='ID:3'");
    }

    @Test
    public void testDeleteByPatientId() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(5);
        // set them all to the same patient id
        for (MapMessage msg : messages) {
            when(msg.getString(SyncMessageConstants.PATIENT_ID)).thenReturn("23");
        }
        when(mockJmsTemplate.browseSelected(eq(SyncQueues.ERROR_QUEUE), eq(SyncMessageConstants.PATIENT_ID + "='23'"), any(BrowserCallback.class))).thenAnswer(new ReturnsBrowserCallbackArgument<Object>(mockJmsSession, mockJmsQueueBrowser));
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        dao.deleteByPatientId("23");

        verify(mockJmsTemplate, times(5)).receiveSelected(SyncQueues.ERROR_QUEUE, SyncMessageConstants.PATIENT_ID + "='23'");
    }

    @Test
    public void testCountAllPatientIds() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(13);
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(messages));

        assertThat(dao.countAllPatientIds(), is(equalTo(13)));
    }

    @Test
    public void testFindOne() throws JMSException {
        List<MapMessage> messages = createMockErrorMsgs(10);
        when(mockJmsTemplate.browseSelected(eq(SyncQueues.ERROR_QUEUE), eq("JMSMessageID='ID:3'"), any(BrowserCallback.class))).thenAnswer(new ReturnsBrowserCallbackArgument<Object>(mockJmsSession, mockJmsQueueBrowser));
        when(mockJmsQueueBrowser.getEnumeration()).thenReturn(Collections.enumeration(Collections.singletonList(messages.get(3))));

        SyncError e = dao.findOne("ID:" + 3);

        assertThat(e, notNullValue());
        assertThat(e.getId(), is(equalTo("ID:" + 3)));
    }

    @Test
    public void testSave() {
        SyncError mockError = new SyncError();
        mockError.setItem("foo");
        mockError.setMessage("bar");
        mockError.setPid("23");
        mockError.setStackTrace("baz");

        Map msg = new SyncErrorToMap().convert(mockError);

        when(mockConversionService.convert(mockError, Map.class)).thenReturn(msg);

        mockError = dao.save(mockError);

        verify(mockConversionService).convert(mockError, Map.class);
        verify(mockJmsTemplate).convertAndSend(eq(SyncQueues.ERROR_QUEUE), eq(msg), any(MessagePostProcessor.class));
    }

    private static List<MapMessage> createMockErrorMsgs(int num) throws JMSException {
        ArrayList<MapMessage> mockMessages = new ArrayList<MapMessage>(num);
        for (int i = 0; i < num; i++) {
            mockMessages.add(createMockErrorMsg(i));
        }
        return mockMessages;
    }

    private static MapMessage createMockErrorMsg(int index) throws JMSException {
        MapMessage msg = mock(MapMessage.class);
        String pid = new Random(System.currentTimeMillis()).toString();
        when(msg.getJMSMessageID()).thenReturn("ID:" + Integer.toString(index)); // sort of like a JMS message ID, but not exact
        when(msg.getStringProperty(SyncMessageConstants.PATIENT_ID)).thenReturn(pid);
        when(msg.getString(SyncMessageConstants.PATIENT_ID)).thenReturn(pid);
        when(msg.getString(SyncMessageConstants.RPC_ITEM_CONTENT)).thenReturn("{\"foo\":\"bar\",\"baz\":" + index + "}");
        when(msg.getString(SyncMessageConstants.EXCEPTION_MESSAGE)).thenReturn("message " + index);
        when(msg.getString("item")).thenReturn("item " + index);
//        when(msg.getString("stackTrace")).thenReturn("");
        return msg;
    }
}
