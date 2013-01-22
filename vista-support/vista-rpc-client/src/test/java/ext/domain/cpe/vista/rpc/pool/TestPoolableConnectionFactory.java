package EXT.DOMAIN.cpe.vista.rpc.pool;

import EXT.DOMAIN.cpe.vista.rpc.conn.Connection;
import EXT.DOMAIN.cpe.vista.rpc.conn.ConnectionFactory;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class TestPoolableConnectionFactory {

    ConnectionFactory mockConnectionFactory;
    PoolableConnectionFactory poolableConnectionFactory;

    @Before
    public void setUp() throws Exception {
        mockConnectionFactory = EasyMock.createMock(ConnectionFactory.class);
        poolableConnectionFactory = new PoolableConnectionFactory(mockConnectionFactory);
    }

    @Test
    public void testMakeObject() throws Exception {
        Connection c = (Connection) poolableConnectionFactory.makeObject("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/960");
    }
}
