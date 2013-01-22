package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.adapter.record.LoginsDisabledFaultException;
import EXT.DOMAIN.vistalink.adapter.record.VistaLinkFaultException;
import EXT.DOMAIN.vistalink.rpc.NoRpcContextFaultException;
import EXT.DOMAIN.vistalink.rpc.RpcNotInContextFaultException;
import EXT.DOMAIN.vistalink.rpc.RpcNotOkForProxyUseException;
import EXT.DOMAIN.vistalink.security.m.SecurityAccessVerifyCodePairInvalidException;
import org.springframework.dao.DataAccessResourceFailureException;

public class VistaLinkTemplateTest extends AbstractVistaLinkConnectionTest {

    private static final String TEST_DUZ = "1234567";
    protected static final String TEST_RPC_CONTEXT = "FOO RPC CONTEXT";
    protected static final String TEST_RPC = "FOO BAR RPC";
    protected static final String TEST_DIVISION = "500";

    public void testExecuteRpcAsUserNoParams() {
        expectVistaLinkDuzConnection(TEST_DUZ);
        expectRpcAndReturn(TEST_RPC_CONTEXT, TEST_RPC, null, "<foo/>");

        replay();

        VistaLinkTemplate t = new VistaLinkTemplate(mockConnectionFactoryLocator);

        String result = t.rpcAsUser(TEST_DIVISION, TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC);
        assertEquals("<foo/>", result);

        verify();
    }

    public void testExecuteRpcAsApplicationNoParams() {
        expectVistaLinkAppProxyConnection(TEST_DUZ);
        expectRpcAndReturn(TEST_RPC_CONTEXT, TEST_RPC, null, "<foo/>");

        replay();

        VistaLinkTemplate t = new VistaLinkTemplate(mockConnectionFactoryLocator);

        String result = t.rpcAsApplication(TEST_DIVISION, TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC);
        assertEquals("<foo/>", result);

        verify();
    }

    public void testDataAccessResourceFailureExceptionFromLocator() {
        VistaLinkTemplate t = new VistaLinkTemplate(mockConnectionFactoryLocator);
        try {
            t.rpcAsUser("600", TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC); // locator can only find station 500
            fail("expected data access resource failure exception");
        } catch (DataAccessResourceFailureException e) {
            // NOOP
        }
    }

    public void testVistaLinkFaultException() {
        assertExceptionDuringRpc(VistaLinkDataRetrievalFailureException.class, new VistaLinkFaultException("foo bar baz"));
    }

    public void testSecurityFaultException() {
        assertExceptionDuringRpc(VistaLinkPermissionDeniedException.class, new SecurityAccessVerifyCodePairInvalidException(new VistaLinkFaultException("foo bar baz")));
    }

    public void testRpcNotInContextException() {
        assertExceptionDuringRpc(VistaLinkRpcNotInContextException.class, new RpcNotInContextFaultException(new VistaLinkFaultException("foo bar baz")));
    }


    public void testNoRpcContextException() {
        assertExceptionDuringRpc(VistaLinkNoRpcContextException.class, new NoRpcContextFaultException(new VistaLinkFaultException("foo bar baz")));
    }

    public void testRpcNotOkForProxyUseException() {
        expectVistaLinkAppProxyConnection(TEST_DUZ);
        expectRpcAndDefaultThrow(TEST_RPC_CONTEXT, TEST_RPC, null, new RpcNotOkForProxyUseException(new VistaLinkFaultException("foo bar baz")));

        replay();

        VistaLinkTemplate t = new VistaLinkTemplate(mockConnectionFactoryLocator);
        try {
            t.rpcAsApplication(TEST_DIVISION, TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC);
            fail("expected rpc not OK for proxy use exception");
        } catch (VistaLinkRpcNotOkForProxyUseException e) {
            // NOOP
        }
        verify();
    }

    public void testLoginsDisabledException() {
        assertExceptionDuringRpc(VistaLinkLoginsDisabledException.class, new LoginsDisabledFaultException(new VistaLinkFaultException("foo bar baz")));
    }

    public void assertExceptionDuringRpc(Class expectedException, Throwable throwDuringRpc) {
        expectVistaLinkDuzConnection(TEST_DUZ);
        expectVistaLinkAppProxyConnection(TEST_DUZ);
        expectRpcAndDefaultThrow(TEST_RPC_CONTEXT, TEST_RPC, null, throwDuringRpc);
        expectRpcAndDefaultThrow(TEST_RPC_CONTEXT, TEST_RPC, null, throwDuringRpc);

        replay();

        VistaLinkTemplate t = new VistaLinkTemplate(mockConnectionFactoryLocator);
        try {
            t.rpcAsUser(TEST_DIVISION, TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC);
            fail("expected " + expectedException.getName());
        } catch (Exception e) {
            assertTrue(expectedException.isAssignableFrom(e.getClass()));
        }

        try {
            t.rpcAsApplication(TEST_DIVISION, TEST_DUZ, TEST_RPC_CONTEXT, TEST_RPC);
            fail("expected " + expectedException.getName());
        } catch (Exception e) {
            assertTrue(expectedException.isAssignableFrom(e.getClass()));
        }

        verify();
    }

    protected String getStationNumber() {
        return TEST_DIVISION;
    }
}
