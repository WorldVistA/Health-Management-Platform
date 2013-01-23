package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.broker.conn.MockServerSocket;
import org.osehra.cpe.vista.rpc.broker.conn.MockSocket;
import org.osehra.cpe.vista.rpc.broker.conn.Socket;
import org.osehra.cpe.vista.rpc.broker.conn.SocketFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestOldRpcProtocol {
    SocketFactory mockSocketFactory;

    @Before
    public void setUp() {
        mockSocketFactory = EasyMock.createMock(SocketFactory.class);
    }

    @Test
    public void connect() throws RpcException, URISyntaxException, IOException {
        RpcHost host = new RpcHost("127.0.0.1", 9600);

        MockSocket handshakeSocket = new MockSocket("\u0000\u0000" + AbstractRpcProtocol.R_ACCEPT + "\u0004");
        MockSocket callbackSocket = new MockSocket(new byte[0]);
        MockServerSocket mockServerSocket = new MockServerSocket(callbackSocket);

        EasyMock.expect(mockSocketFactory.createSocket(host)).andReturn(handshakeSocket);
        EasyMock.expect(mockSocketFactory.createServerSocket()).andReturn(mockServerSocket);
        EasyMock.replay(mockSocketFactory);

        OldRpcProtocol protocol = new OldRpcProtocol(mockSocketFactory);
        Socket socket = protocol.connect(host, 2000);

        Assert.assertSame(callbackSocket, socket);
        Assert.assertTrue(handshakeSocket.isClosed());
        Assert.assertEquals(2000, handshakeSocket.getSoTimeout());
        Assert.assertEquals(2000, mockServerSocket.getSoTimeout());

        String out = new String(handshakeSocket.getBytesSent(), AbstractRpcProtocol.VISTA_CHARSET);
        Assert.assertTrue(out.contains(Integer.toString(mockServerSocket.getLocalPort())));

        EasyMock.verify(mockSocketFactory);
    }

}
