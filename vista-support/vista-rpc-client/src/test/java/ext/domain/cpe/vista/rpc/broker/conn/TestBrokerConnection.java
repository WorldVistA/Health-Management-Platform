package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.RpcRequest;
import org.osehra.cpe.vista.rpc.RpcResponse;
import org.osehra.cpe.vista.rpc.broker.protocol.*;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.ChangeVerifyCodeConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.ConnectionUserDetails;
import org.osehra.cpe.vista.rpc.conn.SystemInfo;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URISyntaxException;

import static org.osehra.cpe.vista.rpc.RpcResponse.LINE_DELIMITER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * TODO: Document org.osehra.cpe.vista.protocol.impl
 */
public class TestBrokerConnection {
    private RpcHost host;
    private RpcProtocol mockProtocol;
    private RpcMessageReader mockReader;
    private RpcMessageWriter mockWriter;
    private MockSocket mockSocket;
    private BrokerConnection c;

    @Before
    public void setUp() throws RpcException, URISyntaxException {
        host = new RpcHost("127.0.0.1");

        mockSocket = new MockSocket(new byte[0]);
        mockProtocol = createMock(RpcProtocol.class);
        mockReader = createStrictMock(RpcMessageReader.class);
        mockWriter = createStrictMock(RpcMessageWriter.class);

        expect(mockProtocol.createReader(mockSocket)).andReturn(mockReader);
        expect(mockProtocol.createWriter(mockSocket)).andReturn(mockWriter);
        replay(mockProtocol);
        c = new BrokerConnection(host, mockSocket, mockProtocol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullSocket() throws RpcException {
        c = new BrokerConnection(host, null, mockProtocol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullProtocol() throws RpcException {
        c = new BrokerConnection(host, mockSocket, null);
    }

    @Test
    public void construct() {
        verify(mockProtocol);
        try {
            assertNull(c.getUserDetails());
            assertFalse(c.isAuthenticated());
        } catch (RpcException e) {
            e.printStackTrace();  // TODO: replace default exception handling
        }
        assertNull(c.getCurrentRpcContext());
    }

    @Test
    public void stop() throws RpcException {
        mockWriter.writeStopConnection();
        expectLastCall();
        mockWriter.flush();
        expectLastCall();
        expect(mockReader.readResponse()).andReturn(new RpcResponse(AbstractRpcProtocol.R_ACCEPT));
        replay(mockWriter, mockReader);

        c.stop();

        verify(mockProtocol, mockReader, mockWriter);
    }

    @Ignore
    @Test
    public void close() {

    }

    @Test
    public void send() throws RpcException {
        ReflectionTestUtils.setField(c, "user", new ConnectionUser()); // just so connection is considered authenticated

        expectCreateContext("BAZ", new RpcResponse("1"));

        RpcRequest request = new RpcRequest("BAZ/FOO BAR");
        RpcResponse response = new RpcResponse("foobar");
        expectSend(request, response);

        replay(mockWriter, mockReader);

        RpcResponse r = c.send(request);
        Assert.assertSame(response, r);

        verify(mockProtocol, mockWriter, mockReader);

        assertEquals("BAZ", c.getCurrentRpcContext());
    }

    @Test
    public void sendNoContext() throws RpcException {
        ReflectionTestUtils.setField(c, "user", new ConnectionUser()); // just so connection is considered authenticated

        expectCreateContext("BAZ", new RpcResponse("1"));
        expectCreateContext("", new RpcResponse("1"));

        RpcRequest request = new RpcRequest("FOO BAR");
        RpcResponse response = new RpcResponse("foobar");
        expectSend(request, response);

        replay(mockWriter, mockReader);
        c.setCurrentRpcContext("BAZ");

        RpcResponse r = c.send(request);
        Assert.assertSame(response, r);

        verify(mockProtocol, mockWriter, mockReader);

        assertNull(c.getCurrentRpcContext());
    }

    @Test
    public void unknownRpcContext() throws RpcException {
        ReflectionTestUtils.setField(c, "user", new ConnectionUser()); // just so connection is considered authenticated

        expectCreateContext("FOO", new RpcResponse("The context 'FOO' does not exist on server.", "", "The context 'FOO' does not exist on server."));
        replay(mockWriter, mockReader);
        try {
            c.send(new RpcRequest("FOO/BAR"));
            Assert.fail("expected " + RpcContextNotFoundException.class);
        } catch (RpcContextNotFoundException e) {
            // NOOP
        }
        verify(mockProtocol, mockWriter, mockReader);
        assertNull(c.getCurrentRpcContext());
    }

    @Test
    public void unknownRpc() throws RpcException {
        ReflectionTestUtils.setField(c, "user", new ConnectionUser()); // just so connection is considered authenticated

        expectCreateContext("FOO", new RpcResponse("1"));
        RpcRequest request = new RpcRequest("FOO/BAR");
        RpcResponse response = new RpcResponse("Remote Procedure 'BAR' doesn't exist on the server.", "", "");
        expectSend(request, response);
        replay(mockWriter, mockReader);
        try {
            c.send(request);
            Assert.fail("expected " + RpcNotFoundException.class);
        } catch (RpcNotFoundException e) {
            // NOOP
        }
        verify(mockProtocol, mockWriter, mockReader);
        assertEquals("FOO", c.getCurrentRpcContext());
    }

    @Test
    public void rpcContextAccessDenied() throws RpcException {
        ReflectionTestUtils.setField(c, "user", new ConnectionUser()); // just so connection is considered authenticated

        expectCreateContext("FOO", new RpcResponse("User FOO,BAR does not have access to option BAZ", "", "User FOO,BAR does not have access to option BAZ"));
        replay(mockWriter, mockReader);
        try {
            c.send(new RpcRequest("FOO/BAR"));
            Assert.fail("expected " + RpcContextAccessDeniedException.class);
        } catch (RpcContextAccessDeniedException e) {
            // NOOP
        }
        verify(mockProtocol, mockWriter, mockReader);
        assertNull(c.getCurrentRpcContext());
    }

    @Test
    public void fetchSystemInfo() throws RpcException {
        RpcRequest request = new RpcRequest("XUS SIGNON SETUP");
        expectSend(request, buildMockSignOnSetupResponse().toRpcResponse());

        request = new RpcRequest("XUS INTRO MSG");
        expectSend(request, buildMockIntroMessageResponse().toRpcResponse());

        request = new RpcRequest("XWB GET BROKER INFO");
        expectSend(request, buildMockBrokerInfoResponse().toRpcResponse());

        replay(mockWriter, mockReader);

        SystemInfo info = c.fetchSystemInfo();
        assertEquals("foobar.vha.DOMAIN.EXT", info.getServer());
        assertEquals("/dev/null:25522", info.getDevice());
        assertEquals("FOO", info.getUCI());
        assertEquals("FOO", info.getVolume());
        assertEquals("FOOBAR.FO_SLC.DOMAIN.EXT", info.getDomainName());
        assertEquals((char) 13 + "Hello world" + LINE_DELIMITER, info.getIntroText());

        verify(mockProtocol, mockWriter, mockReader);
    }

    @Test
    public void authenticateSuccessfully() throws RpcException {
        RpcRequest request = new RpcRequest("XUS AV CODE", new RpcParam(Hash.encrypt("foo;bar")));
        mockWriter.write(encryptedArgRpcEq(request));
        expectLastCall();
        mockWriter.flush();
        expectLastCall();
        expect(mockReader.readResponse()).andReturn(buildMockAVCodeResponse().toRpcResponse());

        request = new RpcRequest("XUS GET USER INFO");
        expectSend(request, buildMockUserInfoResponse().toRpcResponse());

        request = new RpcRequest("XUS DIVISION GET");
        expectSend(request, buildMockDivisionGetResponse().toRpcResponse());

        request = new RpcRequest("XUS DIVISION SET", new RpcParam("960"));
        expectSend(request, buildMockDivisionSetResponse().toRpcResponse());

        replay(mockWriter, mockReader);

        assertNull(c.getUserDetails());
        c.authenticate(new AccessVerifyConnectionSpec("960", "foo", "bar"));

        verify(mockProtocol, mockWriter, mockReader);

        ConnectionUserDetails user = c.getUserDetails();

        assertNotNull(user);
        assertEquals("12345", user.getDUZ());
        assertEquals("foo", user.getAccessCode());
        assertEquals("bar", user.getVerifyCode());
        assertEquals("BAR,FOO", user.getName());
        assertEquals("Foo Bar", user.getStandardName());
        assertEquals("MEDICINE", user.getServiceSection());
        assertEquals("960", user.getDivision());
        assertEquals("SLC-FO EDIS DEV", user.getDivisionNames().get("960"));
        assertEquals("", user.getLanguage());
        assertEquals("Scholar Extraordinaire", user.getTitle());
        assertEquals("5400", user.getDTime());
    }

    @Ignore
    @Test
    public void authenticateAndChangeVerifyCode() throws RpcException {
        RpcRequest request = new RpcRequest("XUS AV CODE", new RpcParam(Hash.encrypt("foo;bar")));
        mockWriter.write(encryptedArgRpcEq(request));
        expectLastCall();
        mockWriter.flush();
        expectLastCall();
        expect(mockReader.readResponse()).andReturn(buildMockAVCodeResponse().toRpcResponse());

        request = new RpcRequest("XUS CVC");
        expectSend(request, buildMockChangeVerifyCodeResponse().toRpcResponse());

        request = new RpcRequest("XUS GET USER INFO");
        expectSend(request, buildMockUserInfoResponse().toRpcResponse());

        request = new RpcRequest("XUS DIVISION GET");
        expectSend(request, buildMockDivisionGetResponse().toRpcResponse());

        request = new RpcRequest("XUS DIVISION SET", new RpcParam("960"));
        expectSend(request, buildMockDivisionSetResponse().toRpcResponse());

        replay(mockWriter, mockReader);

        assertNull(c.getUserDetails());
        c.authenticate(new ChangeVerifyCodeConnectionSpec("960", "foo", "bar", "baz", "baz"));
//
//        verify(mockProtocol, mockWriter, mockReader);
//
//        ConnectionUserDetails user = c.getUserDetails();
//
//        assertNotNull(user);
//        assertEquals("12345", user.getDUZ());
//        assertEquals("foo", user.getAccessCode());
//        assertEquals("bar", user.getVerifyCode());
//        assertEquals("BAR,FOO", user.getName());
//        assertEquals("Foo Bar", user.getStandardName());
//        assertEquals("MEDICINE", user.getServiceSection());
//        assertEquals("960", user.getDivision());
//        assertEquals("SLC-FO EDIS DEV", user.getDivisionNames().get("960"));
//        assertEquals("", user.getLanguage());
//        assertEquals("Scholar Extraordinaire", user.getTitle());
//        assertEquals("5400", user.getDTime());
    }

    public static RpcResponseBuilder buildMockSignOnSetupResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        rb.appendLine("foobar.vha.DOMAIN.EXT");
        rb.appendLine("FOO");
        rb.appendLine("FOO");
        rb.appendLine("/dev/null:25522");
        rb.appendLine("5");
        rb.appendLine("0");
        rb.appendLine("FOOBAR.FO_SLC.DOMAIN.EXT");
        rb.appendLine("0");
        return rb;
    }

    public static RpcResponseBuilder buildMockIntroMessageResponse() {
        return new RpcResponseBuilder(NewRpcMessageWriter.SPack("Hello world" + LINE_DELIMITER));
    }

    public static RpcResponseBuilder buildMockAVCodeResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        rb.appendLine("12345");
        rb.appendLine("0");
        rb.appendLine("0");
        rb.appendLine();
        rb.appendLine("0");
        rb.appendLine("0");
        rb.appendLine();
        rb.appendLine("Good evening foo");
        rb.appendLine("     You last signed on today at 17:21");
        return rb;
    }

    public static RpcResponseBuilder buildMockDivisionGetResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        rb.appendLine("2");
        rb.appendLine("500^CAMP MASTER^500^1");
        rb.appendLine("21787^SLC-FO EDIS DEV^960");
        return rb;
    }

    public static RpcResponseBuilder buildMockDivisionSetResponse() {
        return new RpcResponseBuilder("1");
    }

    public static RpcResponseBuilder buildMockUserInfoResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        rb.appendLine("12345");
        rb.appendLine("BAR,FOO");
        rb.appendLine("Foo Bar");
        rb.appendLine("12345^SLC-FO EDIS DEV^960");
        rb.appendLine("Scholar Extraordinaire");
        rb.appendLine("MEDICINE");
        rb.appendLine();
        rb.appendLine("5400");
        rb.appendLine();
        return rb;
    }

    public static RpcResponseBuilder buildMockBrokerInfoResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        rb.appendLine("3600");
        return rb;
    }

    public static RpcResponseBuilder buildMockChangeVerifyCodeResponse() {
        RpcResponseBuilder rb = new RpcResponseBuilder();
        return rb;
    }

//    @Test
//    public void testRpcRequestEncoding() {
//        MockSocket s = new MockSocket(new byte[0]);
//        VistaConnectionImpl c = new VistaConnectionImpl(s);
//
//        List<ParamRecord> params = new ArrayList<ParamRecord>();
//        params.add(new ParamRecord("baz"));
//        params.add(new ParamRecord("spaz"));
//        c.send(new RpcRequest("FOO BAR", params));
//
//        assertEquals(32, s.getBytesSent().length);
//    }

    public void expectSend(RpcRequest request, RpcResponse response) throws RpcException {
        mockWriter.write(eq(request));
        expectLastCall();
        mockWriter.flush();
        expectLastCall();
        expect(mockReader.readResponse()).andReturn(response);
    }

    public void expectCreateContext(String rpcContext, RpcResponse response) throws RpcException {
        RpcRequest request = new RpcRequest("XWB CREATE CONTEXT", new RpcParam(Hash.encrypt(rpcContext)));
        mockWriter.write(encryptedArgRpcEq(request));
        expectLastCall();
        mockWriter.flush();
        expectLastCall();
        expect(mockReader.readResponse()).andReturn(response);
    }

    public static RpcRequest encryptedArgRpcEq(RpcRequest in) {
        EasyMock.reportMatcher(new EncryptedArgRpcRequestEquals(in));
        return null;
    }

    public static class EncryptedArgRpcRequestEquals implements IArgumentMatcher {

        private RpcRequest expected;

        public EncryptedArgRpcRequestEquals(RpcRequest expected) {
            this.expected = expected;
        }

        public boolean matches(Object actual) {
            RpcRequest that = (RpcRequest) actual;

            if (expected.equals(that)) return true;
            if (expected.getParams().size() != that.getParams().size()) return false;

            String expectedValue = Hash.decrypt(expected.getParams().get(0).getValue());
            String thatValue = Hash.decrypt(that.getParams().get(0).getValue());

            return expectedValue.equals(thatValue);
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("encryptedArgRpcEq(");
            buffer.append(expected.getRpcName());
            buffer.append(" with arg \"");
            buffer.append(Hash.decrypt(expected.getParams().get(0).getValue()));
            buffer.append("\")");
        }
    }

}
