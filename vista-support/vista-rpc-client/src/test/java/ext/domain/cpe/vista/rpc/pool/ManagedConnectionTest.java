package EXT.DOMAIN.cpe.vista.rpc.pool;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import EXT.DOMAIN.cpe.vista.rpc.RpcIoException;
import EXT.DOMAIN.cpe.vista.rpc.RpcRequest;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;
import EXT.DOMAIN.cpe.vista.rpc.TimeoutWaitingForRpcResponseException;
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.ConnectionClosedException;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.InternalServerException;
import EXT.DOMAIN.cpe.vista.rpc.conn.Connection;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.junit.Before;
import org.junit.Test;

public class ManagedConnectionTest {
    private ConnectionManager connectionManager;
    private ManagedConnection managedConnection;
    private Connection connection;
    private RpcRequest request;


    @Before
    public void setUp() throws IOException {
        connectionManager = mock(ConnectionManager.class);
        connection = mock(Connection.class);
        request = mock(RpcRequest.class);
        managedConnection = new ManagedConnection(connectionManager, "", connection);
    }

    @Test
    public void testSendNormalFlow() throws IOException {
        RpcResponse response = new RpcResponse("");
        when(connection.send((RpcRequest) any())).thenReturn(response);

        try {
            RpcResponse response1 = managedConnection.send(request);
            assertNotNull(response1);
        } catch (Throwable t) {
            fail("Should not thrown Exception");
        }
    }

    @Test
    public void testSendThrowRpcIoException() throws IOException {
        when(connection.send(any(RpcRequest.class))).thenThrow(new RpcIoException());

        try {
            managedConnection.send(request);
            fail("Should have thrown RpcIoException");
        } catch (Throwable t) {
            assertTrue(t instanceof RpcIoException);
        }
        verify(connectionManager).invalidateConnection(managedConnection);
        verify(connectionManager).closeExpiredConnections();
        assertThat(managedConnection.isClosed(), is(true));
    }

    @Test
    public void testSendThrowInternalServerException() throws InternalServerException {
        when(connection.send(any(RpcRequest.class))).thenThrow(new InternalServerException("whoops"));

        try {
            managedConnection.send(request);
            fail("Should have thrown " + InternalServerException.class);
        } catch (Throwable t) {
            assertTrue(t instanceof InternalServerException);
        }
        verify(connectionManager).invalidateConnection(managedConnection);
        verify(connectionManager).closeExpiredConnections();
        assertThat(managedConnection.isClosed(), is(true));
    }

    @Test
    public void testSendThrowTimeoutWaitingForRpcResponseException() throws InternalServerException {
        when(connection.send(any(RpcRequest.class))).thenThrow(new TimeoutWaitingForRpcResponseException(new InterruptedIOException("too long!")));

        try {
            managedConnection.send(request);
            fail("Should have thrown " + TimeoutWaitingForRpcResponseException.class);
        } catch (Throwable t) {
            assertTrue(t instanceof TimeoutWaitingForRpcResponseException);
        }
        verify(connectionManager).invalidateConnection(managedConnection);
        verify(connectionManager).closeExpiredConnections();
        assertThat(managedConnection.isClosed(), is(true));
    }

    @Test
    public void testCloseIsClosedAndIsStale() {
        managedConnection.close();

        assertThat(managedConnection.isClosed(), is(true));
        assertThat(managedConnection.isStale(), is(false));
        verify(connectionManager).releaseConnection(managedConnection);
    }

    @Test(expected = ConnectionClosedException.class)
    public void testGetHostAfterClose() {
        managedConnection.close();
        managedConnection.getSystemInfo();
    }

    @Test(expected = ConnectionClosedException.class)
    public void testGetSystemInfoAfterClose() {
        managedConnection.close();
        managedConnection.getSystemInfo();
    }

    @Test(expected = ConnectionClosedException.class)
    public void testGetUserDetailsAfterClose() {
        managedConnection.close();
        managedConnection.getUserDetails();
    }

    @Test(expected = ConnectionClosedException.class)
    public void testSendAfterClose() {
        managedConnection.close();
        managedConnection.send(request);
    }

    @Test(expected = ConnectionClosedException.class)
    public void testGetMetricsAfterClose() {
        managedConnection.close();
        managedConnection.getMetrics();
    }
}
