package EXT.DOMAIN.cpe.vpr.sync.msg;

import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao;
import EXT.DOMAIN.cpe.vpr.dao.ISolrDao;
import EXT.DOMAIN.cpe.vpr.dao.IVprUpdateDao;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.sync.ISyncService;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class VprUpdateCompleteMessageHandlerTests {

    private VprUpdateCompleteMessageHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new VprUpdateCompleteMessageHandler();
        handler.setSolrService(mock(ISolrDao.class));
        handler.setSyncService(mock(ISyncService.class));
        handler.setPatientDao(mock(IPatientDAO.class));
        handler.setVistaAccountDao(mock(IVistaAccountDao.class));
        handler.setLastUpdateDao(mock(IVprUpdateDao.class));
    }

    @Test
    public void testOnMessage() throws Exception {
        Map<String, String> msg = new HashMap<String, String>();
        msg.put(SyncMessageConstants.VISTA_LAST_UPDATED, "foo");
        msg.put(SyncMessageConstants.VISTA_ID, "A1B2");

        handler.onMessage(msg);


    }
}
