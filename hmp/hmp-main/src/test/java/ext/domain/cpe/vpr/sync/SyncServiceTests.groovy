package org.osehra.cpe.vpr.sync

import org.osehra.cpe.vpr.pom.IPatientDAO
import org.junit.Before
import org.junit.Test
import org.springframework.core.convert.ConversionService
import org.springframework.jms.core.JmsTemplate

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat
import org.springframework.jms.core.JmsOperations

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

class SyncServiceTests {

    IPatientDAO mockPatientDao

    JmsOperations mockJmsTemplate

    ConversionService mockConversionService

    SyncService syncService

    @Before
    void setUp() {
        mockJmsTemplate = mock(JmsOperations.class)
        mockPatientDao = mock(IPatientDAO.class)
        mockConversionService = mock(ConversionService.class)

        syncService = new SyncService()
        syncService.jmsTemplate = mockJmsTemplate
        syncService.patientDao = mockPatientDao
        syncService.conversionService = mockConversionService
    }

    @Test
    void testSendClearItemMsg() {
        syncService.sendClearItemMsg("foo")

        Map msg = [:]
        msg[SyncMessageConstants.UID] = "foo"
        msg[SyncMessageConstants.ACTION] = SyncAction.ITEM_CLEAR
        verify(mockJmsTemplate).convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }
}
