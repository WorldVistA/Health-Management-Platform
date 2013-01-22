package EXT.DOMAIN.cpe.vpr.sync.msg;

import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.sync.SyncAction;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ClearItemMessageHandlerTests {

    private IGenericPatientObjectDAO mockGenericDao;
    private ClearItemMessageHandler handler;

    @Before
    public void setUp() throws Exception {
        mockGenericDao = mock(IGenericPatientObjectDAO.class);

        handler = new ClearItemMessageHandler();
        handler.setGenericDao(mockGenericDao);
    }

    @Test
    public void testOnMessage() throws Exception {
        Map msg = new HashMap();
        msg.put(SyncMessageConstants.ACTION, SyncAction.ITEM_CLEAR);
        msg.put(SyncMessageConstants.UID, "foo");

        handler.onMessage(msg);

        verify(mockGenericDao).deleteByUID(null, "foo");
    }
}
