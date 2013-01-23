package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.broker.conn.MockSocket;
import org.osehra.cpe.vista.rpc.broker.conn.SocketFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestNewRpcProtocol {

    SocketFactory mockSocketFactory;
    MockSocket mockSocket;

    @Before
    public void setUp() {
        mockSocketFactory = EasyMock.createMock(SocketFactory.class);

    }

    @Test
    public void connect() throws RpcException, URISyntaxException, IOException {
        RpcHost host = new RpcHost("127.0.0.1", 9600);

        mockSocket = new MockSocket("\u0000\u0000" + AbstractRpcProtocol.R_ACCEPT + "\u0004");
        EasyMock.expect(mockSocketFactory.createSocket(host)).andReturn(mockSocket);
        EasyMock.replay(mockSocketFactory);

        NewRpcProtocol protocol = new NewRpcProtocol(this.mockSocketFactory);
        Assert.assertSame(mockSocket, protocol.connect(host, 2000));
        Assert.assertEquals(2000, mockSocket.getSoTimeout());
        EasyMock.verify(mockSocketFactory);
    }

}
