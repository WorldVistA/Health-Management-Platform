package org.osehra.cpe.vpr.sync.msg;

import org.osehra.cpe.HmpProperties;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.sync.ISyncService;
import org.osehra.cpe.vpr.sync.vista.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.PATIENT_DFN;
import static org.osehra.cpe.vpr.sync.SyncMessageConstants.VISTA_ID;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class LoadPatientMessageHandlerTests {

    private LoadPatientMessageHandler loadHandler;
    private Environment mockEnvironment;
    private IVistaPatientDataService mockPatientDataService;
    private Converter<VistaDataChunk, Object> mockCentralImporter;
    private ISyncService mockSyncService;
	private IPatientDAO mockPatientDao;

    @Before
    public void setUp() throws Exception {
        mockEnvironment = mock(Environment.class);
        mockPatientDataService = mock(IVistaPatientDataService.class);
        mockCentralImporter = mock(Converter.class);
        mockSyncService = mock(ISyncService.class);
        mockPatientDao = mock(IPatientDAO.class);

        loadHandler = new LoadPatientMessageHandler();
        loadHandler.setEnvironment(mockEnvironment);
        loadHandler.setVistaPatientDataService(mockPatientDataService);
        loadHandler.setCentralImporter(mockCentralImporter);
        loadHandler.setSyncService(mockSyncService);
        loadHandler.setPatientDao(mockPatientDao);
    }

    @Test
    public void testOnMessageWithDfn() throws Exception {
        Patient mockPatient = new Patient();
        mockPatient.setData("pid","23");

        ObjectMapper oumm = new ObjectMapper();
        VistaDataChunk mockPatientItem = MockVistaDataChunks.createFromJson(oumm.readTree("{\"localId\": \"229\"}"), "ABCD", "229", "patient");
        VistaDataChunk mockItem = MockVistaDataChunks.createFromJson(oumm.readTree("{\"localId\": \"12345\"}"), "ABCD", "229", "foo");
        List<VistaDataChunk> mockAllergyItems = MockVistaDataChunks.createListFromJson("ABCD", mockPatient, "allergy", 10);

        Map msg = new HashMap();
        msg.put(VISTA_ID, "ABCD");
        msg.put(PATIENT_DFN, "229");

        when(mockPatientDataService.fetchPatientDemographicsWithDfn("ABCD", "229")).thenReturn(mockPatientItem);
        when(mockCentralImporter.convert(any(VistaDataChunk.class))).thenReturn(mockPatient);
        when(mockPatientDao.save(mockPatient)).thenReturn(mockPatient);
        doThrow(new RuntimeException("Sync Error")).when(mockSyncService).errorDuringMsg(anyMap(), (Throwable)anyObject());
        when(mockPatientDataService.fetchDomainChunks(eq("ABCD"), eq(mockPatient), anyString(), anyBoolean(), anyString())).thenReturn(Collections.<VistaDataChunk>emptyList());
        when(mockPatientDataService.fetchDomainChunks("ABCD", mockPatient, "allergy", false, null)).thenReturn(mockAllergyItems);
        when(mockPatientDao.findByAnyPid("23")).thenReturn(mockPatient);
        when(mockEnvironment.getProperty(HmpProperties.SERVER_ID)).thenReturn("blazman123");
        
        loadHandler.onMessage(msg);

        verify(mockPatientDataService).fetchPatientDemographicsWithDfn("ABCD", "229");
        verify(mockCentralImporter).convert(mockPatientItem);
        verify(mockPatientDao).findByAnyPid("23");
        verify(mockPatientDataService).subscribePatient("ABCD", "229", "blazman123");

        for (Map<String, Object> domainLoadConfig : LoadPatientMessageHandler.getLoadConfig()) {
            // verify fetch chunks for all domains listed in the loadConfig
            verify(mockPatientDataService).fetchDomainChunks("ABCD",
                    mockPatient,
                    (String) domainLoadConfig.get("extract"),
                    domainLoadConfig.get("includeBody") != null ? (Boolean) domainLoadConfig.get("includeBody") : false,
                    (String) domainLoadConfig.get("category"));
        }

        // verify send import message for all items returned from all fetches
        for (VistaDataChunk mockAllergyItem : mockAllergyItems) {
            verify(mockSyncService).sendImportPatientDataExtractItemMsg(mockAllergyItem);
        }

        verify(mockSyncService).sendLoadPatientCompleteMsg(mockPatient, msg);
    }

    @Test(expected = AssertionError.class)
    public void testOnMessageFailsWithMissingFields() throws Exception {
        Map msg = new HashMap();
        loadHandler.onMessage(msg);
    }
}
