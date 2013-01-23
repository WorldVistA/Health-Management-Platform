package org.osehra.cpe.vista.rpc;

import org.osehra.cpe.vista.rpc.broker.conn.VistaIdNotFoundException;
import org.osehra.cpe.vista.rpc.broker.protocol.VerifyCodeExpiredException;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.Connection;
import org.osehra.cpe.vista.rpc.conn.ConnectionFactory;
import org.osehra.cpe.vista.rpc.conn.ConnectionUserDetails;
import org.osehra.cpe.vista.util.RpcUriUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;

import java.io.IOException;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestRpcTemplate {
    private ConnectionFactory mockConnectionFactory;
    private Connection mockConnection;
    private ConnectionUserDetails mockUserDetails;

    @Before
    public void setUp() {
        mockConnectionFactory = createMock(ConnectionFactory.class);
        mockConnection = createMock(Connection.class);
        mockUserDetails = createMock(ConnectionUserDetails.class);
    }

    @Test
    public void execute() throws RpcException, IOException {
        RpcResponse mockResponse = new RpcResponse("fred\r\nbarney\r\nwilma\r\nbetty");
        RpcRequest rpc = new RpcRequest("vrpcb://960:foo;bar@localhost:1234/FOOBAR");

        expectRpc(rpc, mockResponse);
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);

        RpcResponse response = t.execute(rpc);
        assertSame(mockResponse, response);

        verify(mockConnectionFactory, mockConnectionFactory, mockConnection, mockUserDetails);
    }

//    @Test
//    public void execute() throws RpcException {
//        RpcResponse mockResponse = new RpcResponse("fred\r\nbarney\r\nwilma\r\nbetty");
//        RpcRequest rpc = new RpcRequest("", "FOOBAR");
//
//        expectRpc("960", "foo", "bar", rpc, mockResponse);
//        replay(mockConnectionFactory, mockConnection, mockUserDetails);
//
//        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
//
//        RpcResponse response = t.execute("960", "foo", "bar", rpc);
//        assertSame(mockResponse, response);
//
//        verify(mockConnectionFactory, mockConnectionFactory, mockConnection, mockUserDetails);
//    }

    private void expectRpc(RpcRequest rpc, RpcResponse response) throws RpcException, IOException {
        RpcHost host = RpcUriUtils.extractHost(rpc.getURI());
        AccessVerifyConnectionSpec auth = AccessVerifyConnectionSpec.create(rpc.getCredentials());
//        AccessVerifyConnectionSpec auth = RpcUriUtils.extractAccessVerifyConnectionSpec(rpc.getURI());

        expect(mockUserDetails.getDUZ()).andReturn("12345").anyTimes();
        expect(mockUserDetails.getDivision()).andReturn(auth.getDivision()).anyTimes();
        expect(mockUserDetails.getAccessCode()).andReturn(auth.getAccessCode()).anyTimes();
        expect(mockUserDetails.getVerifyCode()).andReturn(auth.getVerifyCode()).anyTimes();

        expect(mockConnectionFactory.getConnection(host, auth)).andReturn(mockConnection);
        expect(mockConnection.getUserDetails()).andReturn(mockUserDetails).anyTimes();
        expect(mockConnection.getHost()).andReturn(host).anyTimes();
        expect(mockConnection.send(rpc)).andReturn(response);
        mockConnection.close();
        expectLastCall();
    }

    @Test
    public void executeWithTimeout() throws RpcException, IOException {
        RpcRequest expectedRequest = new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR");
        expectedRequest.setTimeout(7);
        expectRpc(expectedRequest, new RpcResponse("fred\r\nbarney\r\nwilma\r\nbetty"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setTimeout(7);

        t.execute(new RpcHost("localhost", 9200), "960", "foo", "bar", "", "FOOBAR");

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test(expected = PermissionDeniedDataAccessException.class)
    public void verifyCodeNeedsChanging() throws RpcException, IOException {
        expect(mockConnectionFactory.getConnection(new RpcHost("localhost"), new AccessVerifyConnectionSpec("960", "foo", "bar"))).andThrow(new VerifyCodeExpiredException(VerifyCodeExpiredException.VERIFY_CODE_EXPIRED_MESSAGE));
        replay(mockConnectionFactory, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);

        t.execute("vrpcb://960:foo;bar@localhost:9200/FOOBAR");
    }

    @Test
    public void executeWithConnectionCallback() throws RpcException, IOException {
        expect(mockConnectionFactory.getConnection(new RpcHost("localhost", 9060), new AccessVerifyConnectionSpec("960", "foo", "bar"))).andReturn(mockConnection);
        mockConnection.close();
        expectLastCall();
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);

        ConnectionCallback<String> callback = new ConnectionCallback<String>() {
            @Override
            public String doInConnection(Connection con) throws RpcException, DataAccessException {
                assertSame(mockConnection, con);
                return "w00t";
            }
        };
        String result = t.execute(callback, "vrpcb://960:foo;bar@localhost:9060");
        assertEquals("w00t", result);

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeWithLineMapper() throws RpcException, IOException {
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR"), new RpcResponse("fred\r\nbarney\r\nwilma\r\nbetty"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);

        List<Foo> fooList = t.execute(new FooLineMapper(), new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR"));
        assertEquals(4, fooList.size());
        assertEquals("fred", fooList.get(0).getText());
        assertEquals("barney", fooList.get(1).getText());
        assertEquals("wilma", fooList.get(2).getText());
        assertEquals("betty", fooList.get(3).getText());

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeForLong() throws IOException {
        assertLongResult(3147483647L, "3147483647");
        assertLongResult(42L, "42");
        assertLongResult(-23L, "-23");
        assertLongResult(0L, "0");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void executeForLongEmptyResponse() throws IOException {
        assertLongResult(4L, "");
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void executeForLongIncorrectResultSize() throws IOException {
        assertLongResult(4L, "1\r\n2");
    }

    private void assertLongResult(long value, String response) throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR"), new RpcResponse(response));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        assertEquals(value, t.executeForLong(new RpcHost("localhost"), "960", "foo", "bar", "", "FOOBAR"));
        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeForInt() throws IOException {
        assertIntResult(4, "4");
        assertIntResult(42, "42");
        assertIntResult(-23, "-23");
        assertIntResult(0, "0");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void executeForIntEmptyResponse() throws IOException {
        assertIntResult(4, "");
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void executeForIntIncorrectResultSize() throws IOException {
        assertIntResult(4, "1\r\n2");
    }

    private void assertIntResult(int value, String response) throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR"), new RpcResponse(response));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        assertEquals(value, t.executeForInt(new RpcHost("localhost"), "960", "foo", "bar", "", "FOOBAR"));
        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeForBoolean() throws RpcException, IOException {
        assertBooleanResult(true, "true");
        assertBooleanResult(false, "false");
        assertBooleanResult(false, "yes");
        assertBooleanResult(false, "no");
        assertBooleanResult(false, "off");
        assertBooleanResult(false, "on");
        assertBooleanResult(false, "1");
        assertBooleanResult(false, "0");
    }

    private void assertBooleanResult(boolean value, String response) throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9200/FOOBAR"), new RpcResponse(response));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);

        boolean b = t.executeForBoolean(new RpcHost("localhost"), "960", "foo", "bar", "", "FOOBAR");
        assertEquals(value, b);

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeWithHostResolver() throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@fubar:1234/FOOBAR"), new RpcResponse("foo"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setHostResolver(new RpcHostResolver() {
            @Override
            public RpcHost resolve(String vistaId) throws VistaIdNotFoundException {
                return new RpcHost("fubar", 1234);
            }
        });

        t.execute("vrpcb://960:foo;bar@3B1D/FOOBAR");

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithAmbiguousHostAndNoHostResolver() throws IOException {
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.execute("vrpcb://960:foo;bar@3B1D/FOOBAR");
    }

    @Test
    public void executeWithCredentialsProvider() throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9060/FOOBAR"), new RpcResponse("foo"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public String getCredentials(RpcHost host, String userInfo) {
                return "960:foo;bar";
            }
        });

        t.execute("vrpcb://localhost:9060/FOOBAR");

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithNoCredentialsAndNoCredentialsProvider() throws IOException {
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.execute("vrpcb://localhost:9060/FOOBAR");
    }

    @Test
    public void executeRelativeUriWithHostResolverAndCredentialsProvider() throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@fubar:1234/FOO/BAR"), new RpcResponse("foo"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setHostResolver(new RpcHostResolver() {
            @Override
            public RpcHost resolve(String vistaId) throws VistaIdNotFoundException {
                return new RpcHost("fubar", 1234);
            }
        });
        t.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public String getCredentials(RpcHost host, String userInfo) {
                return "960:foo;bar";
            }
        });
        t.execute("/FOO/BAR");
        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test
    public void executeWithConnectionCallbackAndHostResolver() throws IOException {
        expect(mockConnectionFactory.getConnection(new RpcHost("fubar", 1234), new AccessVerifyConnectionSpec("960", "foo", "bar"))).andReturn(mockConnection);
        mockConnection.close();
        expectLastCall();
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setHostResolver(new RpcHostResolver() {
            @Override
            public RpcHost resolve(String vistaId) throws VistaIdNotFoundException {
                return new RpcHost("fubar", 1234);
            }
        });
        t.execute(new ConnectionCallback<String>() {
            @Override
            public String doInConnection(Connection con) throws RpcException, DataAccessException {
                assertSame(mockConnection, con);
                return "w00t";
            }
        }, "vrpcb://960:foo;bar@3B1D");

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithWithConnectionCallbackAmbiguousHostAndNoHostResolver() throws IOException {
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection con) throws RpcException, DataAccessException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, "vrpcb://960:foo;bar@3B1D");
    }

    @Test
    public void executeWithConnectionCallbackAndCredentialsProvider() throws IOException {
        reset(mockConnectionFactory, mockConnection, mockUserDetails);
        expectRpc(new RpcRequest("vrpcb://960:foo;bar@localhost:9060"), new RpcResponse("foo"));
        replay(mockConnectionFactory, mockConnection, mockUserDetails);

        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public String getCredentials(RpcHost host, String userInfo) {
                return "960:foo;bar";
            }
        });

        t.execute("vrpcb://localhost:9060");

        verify(mockConnectionFactory, mockConnection, mockUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithConnectionCallbackNoCredentialsAndNoCredentialsProvider() throws IOException {
        RpcTemplate t = new RpcTemplate(mockConnectionFactory);
        t.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection con) throws RpcException, DataAccessException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, "vrpcb://localhost:9060");
    }
}
