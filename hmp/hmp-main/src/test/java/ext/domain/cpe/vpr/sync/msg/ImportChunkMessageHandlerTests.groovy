package EXT.DOMAIN.cpe.vpr.sync.msg

import EXT.DOMAIN.cpe.test.mockito.ReturnsArgument
import EXT.DOMAIN.cpe.vpr.dao.RoutingDataStore
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRunner

import EXT.DOMAIN.cpe.vpr.sync.vista.CentralImporter
import EXT.DOMAIN.cpe.vpr.sync.vista.Foo
import EXT.DOMAIN.cpe.vpr.sync.vista.MockVistaDataChunks
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Before
import org.junit.Test
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import EXT.DOMAIN.cpe.vpr.*

import static org.mockito.Matchers.anyMapOf
import static org.mockito.Mockito.*

import EXT.DOMAIN.cpe.vpr.sync.ISyncService
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import org.mockito.internal.stubbing.answers.ThrowsException
import org.mockito.ArgumentCaptor

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.sameInstance

class ImportChunkMessageHandlerTests {

    ImportChunkMessageHandler importMessageHandler;
    ConversionService mockConversionService;
    CentralImporter mockCentralImporter;
    RoutingDataStore mockRoutingDao;
    Converter<VistaDataChunk, Foo> mockFooConverter;
    ISyncService mockSyncService;
	FrameRunner mockRunner;

    VistaDataChunk mockFragment = MockVistaDataChunks.createFromJson("{\"foo\":{\"bar\":\"baz\"}}", "foo");

    @Before
    void setUp() {
        mockFooConverter = mock(Converter.class);
        mockConversionService = mock(ConversionService.class);
        mockSyncService = mock(ISyncService.class);
        mockRoutingDao = mock(RoutingDataStore.class);
		mockRunner = mock(FrameRunner.class);

        mockCentralImporter = new CentralImporter(importers: [foo: mockFooConverter]);

        when(mockSyncService.errorDuringMsg(anyMap(), any(Throwable.class))).thenAnswer(new Answer() {
            Object answer(InvocationOnMock invocation) {
                return new ThrowsException((Throwable) invocation.arguments[1]).answer(invocation)
            }
        });

        importMessageHandler = new ImportChunkMessageHandler();
        importMessageHandler.syncService = mockSyncService;
        importMessageHandler.conversionService = mockConversionService;
        importMessageHandler.centralImporter = mockCentralImporter;
        importMessageHandler.routingDao = mockRoutingDao;
		importMessageHandler.runner = mockRunner;
    }

    @Test
    void testOnMessage() {
        Object mockEntity = new Foo(bar: "spaz", baz: false)
        Map msg = [:];
        Map<String, Object> mockDomainObjects;

        when(mockConversionService.convert(msg, VistaDataChunk.class)).thenReturn(mockFragment);
        when(mockFooConverter.convert(mockFragment)).thenReturn(mockEntity);
        when(mockRoutingDao.save(any())).thenAnswer(new ReturnsArgument<Object>());

        importMessageHandler.onMessage(msg);

        verify(mockConversionService).convert(msg, VistaDataChunk.class);
        verify(mockFooConverter).convert(mockFragment);
        verify(mockRoutingDao).save(mockEntity);
    }
}
