package EXT.DOMAIN.cpe.vpr.sync.vista;

import EXT.DOMAIN.cpe.hub.VistaAccount;
import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao;
import EXT.DOMAIN.cpe.vpr.dao.IVprUpdateDao;
import EXT.DOMAIN.cpe.vpr.sync.ISyncService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class VprUpdateJobTests {
    static final String MOCK_SERVER_ID = "MOCK_HMP";
    static final String MOCK_VISTA1_ID = "A1B2";
    static final String MOCK_VISTA2_ID = "C3D4";

    static final String MOCK_VISTA1_TIMESTAMP = "foo";
    static final String MOCK_VISTA2_TIMESTAMP = "";

    static final String NEW_VISTA1_TIMESTAMP = "foo1";
    static final String NEW_VISTA2_TIMESTAMP = "bar";

    private VprUpdateJob updateJob;

    @Before
    public void setUp() throws Exception {
        updateJob = new VprUpdateJob();
        updateJob.setVistaPatientDataService(mock(IVistaPatientDataService.class));
        updateJob.setSyncService(mock(ISyncService.class));
        updateJob.setVistaAccountDao(mock(IVistaAccountDao.class));
        updateJob.setLastUpdateDao(mock(IVprUpdateDao.class));
        updateJob.setServerId(MOCK_SERVER_ID);
    }

    @Test
    public void testRun() throws Exception {
        List<VistaAccount> mockAccounts = new ArrayList<VistaAccount>();
        mockAccounts.add(createMockVistaAccount(MOCK_VISTA1_ID));
        mockAccounts.add(createMockVistaAccount(MOCK_VISTA2_ID));

        when(updateJob.vistaAccountDao.findAllByVistaIdIsNotNull()).thenReturn(mockAccounts);
        when(updateJob.lastUpdateDao.findOneBySystemId(MOCK_VISTA1_ID)).thenReturn(new VprUpdate(MOCK_VISTA1_ID, MOCK_VISTA1_TIMESTAMP));
        when(updateJob.lastUpdateDao.findOneBySystemId(MOCK_VISTA2_ID)).thenReturn(null);

        List<VistaDataChunk> mockChunks = new ArrayList<VistaDataChunk>();

        VprUpdateData updatesFromVista1 = new VprUpdateData();
        updatesFromVista1.setLastUpdate(NEW_VISTA1_TIMESTAMP);
        updatesFromVista1.setChunks(mockChunks);

        VprUpdateData updatesFromVista2 = new VprUpdateData();
        updatesFromVista2.setLastUpdate(NEW_VISTA2_TIMESTAMP);
        updatesFromVista2.setChunks(Collections.<VistaDataChunk>emptyList());

        when(updateJob.vistaPatientDataService.fetchUpdates(MOCK_VISTA1_ID, MOCK_SERVER_ID, MOCK_VISTA1_TIMESTAMP)).thenReturn(updatesFromVista1);
        when(updateJob.vistaPatientDataService.fetchUpdates(MOCK_VISTA2_ID, MOCK_SERVER_ID, MOCK_VISTA2_TIMESTAMP)).thenReturn(updatesFromVista2);

        updateJob.run();

        verify(updateJob.vistaAccountDao).findAllByVistaIdIsNotNull();

        verify(updateJob.lastUpdateDao).findOneBySystemId(MOCK_VISTA1_ID);
        verify(updateJob.lastUpdateDao).findOneBySystemId(MOCK_VISTA2_ID);

        verify(updateJob.vistaPatientDataService).fetchUpdates(MOCK_VISTA1_ID, MOCK_SERVER_ID, MOCK_VISTA1_TIMESTAMP);
        verify(updateJob.vistaPatientDataService).fetchUpdates(MOCK_VISTA2_ID, MOCK_SERVER_ID, "");

        verify(updateJob.syncService).sendUpdateVprCompleteMsg(MOCK_SERVER_ID, MOCK_VISTA1_ID, NEW_VISTA1_TIMESTAMP, null);
        verify(updateJob.syncService).sendUpdateVprCompleteMsg(MOCK_SERVER_ID, MOCK_VISTA2_ID, NEW_VISTA2_TIMESTAMP, null);
    }

    @Test
    public void testRunDisabled() {
        updateJob.setDisabled(true);

        updateJob.run();

        verifyZeroInteractions(updateJob.vistaPatientDataService);
        verifyZeroInteractions(updateJob.syncService);
        verifyZeroInteractions(updateJob.vistaAccountDao);
    }

    private VistaAccount createMockVistaAccount(String vistaId) {
        VistaAccount a = new VistaAccount();
        a.setVistaId(vistaId);
        return a;
    }

}
