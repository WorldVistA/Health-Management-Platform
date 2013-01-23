package org.osehra.cpe.vpr.sync.msg;

import org.osehra.cpe.hub.dao.IVistaAccountDao;
import org.osehra.cpe.vpr.dao.ISolrDao;
import org.osehra.cpe.vpr.dao.IVprUpdateDao;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.sync.ISyncService;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
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
