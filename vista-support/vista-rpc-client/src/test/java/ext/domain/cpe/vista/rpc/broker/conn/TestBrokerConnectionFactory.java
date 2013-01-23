package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.broker.protocol.*;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.Connection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;

public class TestBrokerConnectionFactory {

    private RpcHost host;
    private SocketFactory mockSocketFactory;

    @Before
    public void setUp() throws URISyntaxException {
        host = new RpcHost("localhost", 9001);
        mockSocketFactory = createMock(SocketFactory.class);
    }

    @Test
    public void constructDefaults() {
        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);
        Assert.assertTrue(cf.isBackwardsCompatible());
        Assert.assertFalse(cf.isOldProtocolOnly());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithUnsupportedUriScheme() throws URISyntaxException {
        host = new RpcHost("www.google.com", 80, "http");
        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullSocketFactory() {
        BrokerConnectionFactory cf = new BrokerConnectionFactory(null);
    }

    @Test
    public void getConnectionWithDivisionAccessAndVerifyCodes() throws IOException, RpcException {
        expect(mockSocketFactory.createSocket(host)).andReturn(new MockSocket(getAcceptMessage() +
                getSignOnSetupMessage() +
                getIntroMessage() +
                getBrokerInfoMessage() +
                getAVCodeMessage() +
                getUserInfoMessage() +
                getDivisionGetMessage() +
                getDivisionSetMessage()));
        replay(mockSocketFactory);

        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);

        Connection c = cf.getConnection(host, new AccessVerifyConnectionSpec("960", "foo", "bar"));
        assertNotNull(c);

        verify(mockSocketFactory);
    }

    @Test
    public void getConnectionWithAccessVerifyCodesAndNoDivision() throws IOException, RpcException {
        expect(mockSocketFactory.createSocket(host)).andReturn(new MockSocket(getAcceptMessage() +
                getSignOnSetupMessage() +
                getIntroMessage() +
                getBrokerInfoMessage() +
                getAVCodeMessage() +
                getUserInfoMessage() +
                getDivisionGetMessage()));
        replay(mockSocketFactory);

        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);

        Connection c = cf.getConnection(host, new AccessVerifyConnectionSpec(null, "foo", "bar"));
        assertNotNull(c);

        verify(mockSocketFactory);
    }

    public void getConnectionWithSystemInfoConnectionSpec() {

    }

    @Test
    public void backwardsCompatibility() throws IOException {
        MockSocket mockNoResponseSocket = new MockSocket(new byte[0]);
        MockSocket mockHandshakeSocket = new MockSocket(getAcceptMessage());
        MockSocket mockCallbackSocket = new MockSocket(getSignOnSetupMessage() +
                getIntroMessage() +
                getBrokerInfoMessage() +
                getAVCodeMessage() +
                getUserInfoMessage() +
                getDivisionGetMessage() +
                getDivisionSetMessage());
        expect(mockSocketFactory.createSocket(host)).andReturn(mockNoResponseSocket);
        expect(mockSocketFactory.createSocket(host)).andReturn(mockHandshakeSocket);
        expect(mockSocketFactory.createServerSocket()).andReturn(new MockServerSocket(mockCallbackSocket));
        replay(mockSocketFactory);

        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);

        Connection c = cf.getConnection(host, new AccessVerifyConnectionSpec("960", "foo", "bar"));
        assertNotNull(c);

        verify(mockSocketFactory);

        Assert.assertTrue(mockNoResponseSocket.getBytesSentAsString().startsWith(NewRpcMessageWriter.PREFIX));
        Assert.assertTrue(mockNoResponseSocket.isClosed());
        Assert.assertTrue(mockHandshakeSocket.getBytesSentAsString().startsWith(OldRpcMessageWriter.PREFIX));
        Assert.assertTrue(mockHandshakeSocket.isClosed());
    }

    @Test
    public void oldProtocolOnly() throws IOException {
        MockSocket mockHandshakeSocket = new MockSocket(getAcceptMessage());
        MockSocket mockCallbackSocket = new MockSocket(getSignOnSetupMessage() +
                getIntroMessage() +
                getBrokerInfoMessage() +
                getAVCodeMessage() +
                getUserInfoMessage() +
                getDivisionGetMessage() +
                getDivisionSetMessage());
        MockServerSocket mockServerSocket = new MockServerSocket(mockCallbackSocket);
        expect(mockSocketFactory.createSocket(host)).andReturn(mockHandshakeSocket);
        expect(mockSocketFactory.createServerSocket()).andReturn(mockServerSocket);
        replay(mockSocketFactory);

        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);
        cf.setOldProtocolOnly(true);

        Connection c = cf.getConnection(host, new AccessVerifyConnectionSpec("960", "foo", "bar"));
        assertNotNull(c);

        verify(mockSocketFactory);

        Assert.assertTrue(mockHandshakeSocket.getBytesSentAsString().startsWith(OldRpcMessageWriter.PREFIX));
        Assert.assertTrue(mockHandshakeSocket.isClosed());
    }

    @Test
    public void newProtocolOnly() throws IOException {
        MockSocket mockSocket = new MockSocket(new byte[0]);
        expect(mockSocketFactory.createSocket(host)).andReturn(mockSocket);
        replay(mockSocketFactory);

        BrokerConnectionFactory cf = new BrokerConnectionFactory(mockSocketFactory);
        cf.setBackwardsCompatible(false);
        try {
            cf.getConnection(host, new AccessVerifyConnectionSpec("960", "foo", "bar"));
            Assert.fail("expected " + UnsupportedProtocolException.class);
        } catch (UnsupportedProtocolException e) {
            // NOOP
        }

        verify(mockSocketFactory);
        Assert.assertTrue(mockSocket.isClosed());
    }

    private String getIntroMessage() {
        return TestBrokerConnection.buildMockIntroMessageResponse().toString();
    }

    private String getSignOnSetupMessage() {
        return TestBrokerConnection.buildMockSignOnSetupResponse().toString();
    }

    private String getAcceptMessage() {
        return new RpcResponseBuilder(AbstractRpcProtocol.R_ACCEPT).toString();
    }

    private String getDivisionGetMessage() {
        return TestBrokerConnection.buildMockDivisionGetResponse().toString();
    }

    private String getDivisionSetMessage() {
        return TestBrokerConnection.buildMockDivisionSetResponse().toString();
    }

    private String getAVCodeMessage() {
        return TestBrokerConnection.buildMockAVCodeResponse().toString();
    }

    private String getUserInfoMessage() {
        return TestBrokerConnection.buildMockUserInfoResponse().toString();
    }

    private String getBrokerInfoMessage() {
        return TestBrokerConnection.buildMockBrokerInfoResponse().toString();
    }
}
