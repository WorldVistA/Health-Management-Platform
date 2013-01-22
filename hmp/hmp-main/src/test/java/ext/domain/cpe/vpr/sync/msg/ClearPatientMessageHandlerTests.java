package EXT.DOMAIN.cpe.vpr.sync.msg;

import EXT.DOMAIN.cpe.HmpProperties;
import EXT.DOMAIN.cpe.hub.VistaAccount;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.PatientFacility;
import EXT.DOMAIN.cpe.vpr.dao.ISolrDao;
import EXT.DOMAIN.cpe.vpr.dao.ISyncErrorDao;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import EXT.DOMAIN.cpe.vpr.sync.vista.IVistaPatientDataService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ClearPatientMessageHandlerTests {

    public static final String MOCK_PID = "23";
    public static final String MOCK_LOCAL_PATIENT_ID_FOR_MOCK_VISTA_ID = "42";
    public static final String MOCK_HMP_SERVER_ID = "flibberty-floo";
    public static final String MOCK_VISTA_ID = "ABCD";

    private ClearPatientMessageHandler handler;

    private Environment mockEnvironment;
    private IPatientDAO mockPatientDao;
    private ISyncErrorDao mockSyncErrorDao;
    private ISolrDao mockSolrDao;
    private Patient mockPatient;
    private IVistaPatientDataService mockVistaPatientDataService;

    @Before
    public void setUp() throws Exception {
        mockEnvironment = mock(Environment.class);
        mockPatientDao = mock(IPatientDAO.class);
        mockSyncErrorDao = mock(ISyncErrorDao.class);
        mockSolrDao = mock(ISolrDao.class);
        mockVistaPatientDataService = mock(IVistaPatientDataService.class);

        handler = new ClearPatientMessageHandler();
        handler.setEnvironment(mockEnvironment);
        handler.setPatientDao(mockPatientDao);
        handler.setSyncErrorDao(mockSyncErrorDao);
        handler.setSolrDao(mockSolrDao);
        handler.setVistaPatientDataService(mockVistaPatientDataService);

        mockPatient = new Patient();
        mockPatient.setData("pid", MOCK_PID);

        PatientFacility facility = new PatientFacility();
        facility.setData("systemId", MOCK_VISTA_ID);
        facility.setData("localPatientId", MOCK_LOCAL_PATIENT_ID_FOR_MOCK_VISTA_ID);
        mockPatient.addToFacilities(facility);

        VistaAccount mockVistaAccount = new VistaAccount();
        mockVistaAccount.setVistaId(MOCK_VISTA_ID);

        when(mockEnvironment.getProperty(HmpProperties.SERVER_ID)).thenReturn(MOCK_HMP_SERVER_ID);
        when(mockPatientDao.findByVprPid(MOCK_PID)).thenReturn(mockPatient);
    }

    @Test
    public void testOnMessage() throws Exception {
        Map msg = new HashMap();
        msg.put(SyncMessageConstants.PATIENT_ID, MOCK_PID);

        handler.onMessage(msg);

        // verify deletion
        verify(mockPatientDao).findByVprPid(MOCK_PID);
        verify(mockPatientDao).deleteByPID(MOCK_PID);
        verify(mockSyncErrorDao).deleteByPatientId(MOCK_PID);
        verify(mockSolrDao).deleteByQuery("pid:" + MOCK_PID);
        verify(mockSolrDao).commit();

        // verify unsubscribe
        verify(mockVistaPatientDataService).unsubscribePatient(MOCK_VISTA_ID, MOCK_LOCAL_PATIENT_ID_FOR_MOCK_VISTA_ID, MOCK_HMP_SERVER_ID);
    }

    @Test
    public void testOnMessageWithPatientIdNotInVpr() throws Exception {
        reset(mockPatientDao);
        when(mockPatientDao.findByVprPid(MOCK_PID)).thenReturn(null);

        Map msg = new HashMap();
        msg.put(SyncMessageConstants.PATIENT_ID, MOCK_PID);

        handler.onMessage(msg);

        // verify deletion
        verify(mockPatientDao).findByVprPid(MOCK_PID);
        verifyNoMoreInteractions(mockPatientDao);
        verifyZeroInteractions(mockSyncErrorDao);
        verifyZeroInteractions(mockSolrDao);
        verifyZeroInteractions(mockVistaPatientDataService);
    }

    @Test(expected = AssertionError.class)
    public void testOnMessageMissingPatientId() {
        Map msg = new HashMap();
        handler.onMessage(msg);
    }
}
