package org.osehra.cpe.vistalink;

import org.osehra.exception.FoundationsException;
import org.osehra.vistalink.adapter.cci.VistaLinkAppProxyConnectionSpec;
import org.osehra.vistalink.adapter.cci.VistaLinkConnection;
import org.osehra.vistalink.adapter.cci.VistaLinkDuzConnectionSpec;
import org.osehra.vistalink.rpc.*;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.internal.matchers.Equals;
import org.springframework.util.FileCopyUtils;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVistaLinkConnectionTest extends TestCase {

    protected ConnectionFactory mockConnectionFactory;
    protected VistaLinkConnection mockVistaLinkConnection;
    protected MockConnectionFactoryLocator mockConnectionFactoryLocator;

    private int expectedTimeOut = VistaLinkTemplate.DEFAULT_TIMEOUT;

    protected void setUp() throws Exception {
        mockConnectionFactory = EasyMock.createMock(ConnectionFactory.class);

        mockVistaLinkConnection = EasyMock.createMock(VistaLinkConnection.class);

        mockConnectionFactoryLocator = new MockConnectionFactoryLocator();
        mockConnectionFactoryLocator.put(getStationNumber(), mockConnectionFactory);
    }

    protected abstract String getStationNumber();

    protected String getResourceAsString(String resource) throws IOException {
        StringWriter w = new StringWriter();
        InputStreamReader r = new InputStreamReader(getClass().getResourceAsStream(resource));
        FileCopyUtils.copy(r, w);
        String resourceStr = w.toString();
        resourceStr.replaceAll("(\r\n|\r|\n|\n\r)", "\n");
        return resourceStr;
    }

    protected int getExpectedTimeOut() {
        return expectedTimeOut;
    }

    protected void setExpectedTimeOut(int expectedTimeOut) {
        this.expectedTimeOut = expectedTimeOut;
    }

    protected void expectVistaLinkDuzConnection(String duz) {
        try {
            EasyMock.expect(mockConnectionFactory.getConnection(new VistaLinkDuzConnectionSpec(getStationNumber(), duz))).andReturn(mockVistaLinkConnection);
        } catch (ResourceException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }

    protected void expectVistaLinkAppProxyConnection(String appProxyName) {
        try {
            EasyMock.expect(mockConnectionFactory.getConnection(new VistaLinkAppProxyConnectionSpec(getStationNumber(), appProxyName))).andReturn(mockVistaLinkConnection);
        } catch (ResourceException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }

    protected void expectRpcAndReturn(String rpcContext, String rpc, List params, String response) {
        try {
            RpcRequest request = RpcRequestFactory.getRpcRequest(rpcContext, rpc);
            if (params != null) request.setParams(params);
            expectRpcAndReturn(request, new TestRpcResponse(response));
        } catch (FoundationsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void expectRpcAndReturn(RpcRequest request, RpcResponse response) throws FoundationsException {
        mockVistaLinkConnection.setTimeOut(getExpectedTimeOut());
        EasyMock.expect(mockVistaLinkConnection.executeRPC(eqRpcRequest(request))).andReturn(response);
    }

    protected void expectRpcAndReturnXmlResource(String rpcContext, String rpc, List params, String resource) throws IOException {
        try {
            RpcRequest request = RpcRequestFactory.getRpcRequest(rpcContext, rpc);
            if (params != null) request.setParams(params);
            RpcResponseFactory responseFactory = new RpcResponseFactory();
            RpcResponse response = (RpcResponse) responseFactory.handleResponse(getResourceAsString(resource), request);
            expectRpcAndReturn(request, response);
        } catch (FoundationsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void expectRpcAndDefaultThrow(String rpcContext, String rpc, List params, Throwable t) {
        mockVistaLinkConnection.setTimeOut(getExpectedTimeOut());
        try {
            RpcRequest request = RpcRequestFactory.getRpcRequest(rpcContext, rpc);
            if (params != null) request.setParams(params);
            EasyMock.expect(mockVistaLinkConnection.executeRPC(eqRpcRequest(request))).andThrow(t);
        } catch (FoundationsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void replay() {
        EasyMock.replay(mockConnectionFactory, mockVistaLinkConnection);
    }

    protected void verify() {
        EasyMock.verify(mockConnectionFactory, mockVistaLinkConnection);
    }

    static String buildXmlResponse(String rpcResult) {
        StringBuffer buf = new StringBuffer();

        return buf.toString();
    }

    protected List createParams(Object... args) {
        List l = new ArrayList();
        for (Object arg : args) {
            l.add(arg);
        }
        return l;
    }

    static class TestRpcResponse extends RpcResponse {
        protected TestRpcResponse(String rpcResult) {
            super(buildXmlResponse(rpcResult), buildXmlResponse(rpcResult), null, "org.osehra.foundations.rpc.response", rpcResult, "flee");
        }
    }

    static class RpcRequestEquals implements IArgumentMatcher {
        private RpcRequest expected;
        private RpcRequestParamsEquals paramMatcher;

        public RpcRequestEquals(RpcRequest expected) {
            this.expected = expected;
            this.paramMatcher = new RpcRequestParamsEquals(expected.getParams());
        }

        public boolean matches(Object request) {
            if (!(request instanceof RpcRequest)) {
                return false;
            }

            RpcRequest actual = (RpcRequest) request;
            return expected.getRpcName().equals(actual.getRpcName()) &&
                    expected.getRpcContext().equals(actual.getRpcContext()) &&
                    expected.getRpcClientTimeOut() == actual.getRpcClientTimeOut() &&
                    expected.getTimeOut() == actual.getTimeOut() &&
                    expected.isXmlResponse() == actual.isXmlResponse() &&
                    paramMatcher.matches(actual.getParams());
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("eqRpcRequest(");
            buffer.append(expected.toString());
            buffer.append("\")");
        }
    }

    static class RpcRequestParamsEquals implements IArgumentMatcher {
        private RpcRequestParams expected;

        public RpcRequestParamsEquals(RpcRequestParams expected) {
            this.expected = expected;
        }

        public boolean matches(Object request) {
            if (!(request instanceof RpcRequestParams)) {
                return false;
            }

            RpcRequestParams actual = (RpcRequestParams) request;

            int position = 1;
            Object expectedValue = expected.getParam(position);
            Object actualValue = expected.getParam(position);
            if (expectedValue == null && actualValue == null) return true;

            while (expectedValue != null && actualValue != null) {
                if (!(new Equals(expectedValue).matches(actualValue))) {
                    return false;
                }
                position++;
                expectedValue = expected.getParam(position);
                actualValue = actual.getParam(position);
            }
            if ((expectedValue == null && actualValue != null) || (expectedValue != null && actualValue == null))
                return false;
            return true;
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("eqRpcRequest(");
            buffer.append(expected.toString());
            buffer.append("\")");
        }
    }

    public static <T extends RpcRequest> T eqRpcRequest(T in) {
        EasyMock.reportMatcher(new RpcRequestEquals(in));
        return null;
    }
}
