package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.Connection;
import org.osehra.cpe.vista.rpc.conn.ConnectionFactory;
import org.osehra.cpe.vista.rpc.conn.ConnectionSpec;
import org.osehra.cpe.vista.rpc.pool.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements {@link ConnectionManager} by delegating directly to an instance of {@link ConnectionFactory} which
 * provides a non-pooling implementation of {@link ConnectionManager}.
 */
public class DirectConnectionManager implements ConnectionManager {

    private static Logger LOG = LoggerFactory.getLogger(DirectConnectionManager.class);

    private ConnectionFactory connectionFactory;

    public DirectConnectionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public synchronized Connection requestConnection(RpcHost host, String credentials) throws RpcException {
        return this.connectionFactory.getConnection(host, getConnectionSpec(credentials));
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch (RpcException e) {
            LOG.warn("Exception while releasing connection", e);
            // NOOP: ignore exceptions on close
        }
    }

    @Override
    public void invalidateConnection(Connection connection) {
        try {
            connection.close();
        } catch (RpcException e) {
            LOG.warn("Exception while invalidating connection", e);
            // NOOP: ignore exceptions on close
        }
    }

    @Override
    public void closeIdleConnections() {
        // NOOP: Nothing to close, since there are no idle connections
    }

    @Override
    public void closeExpiredConnections() {
        // NOOP: Nothing to close, since there are no expired connections
    }

    @Override
    public synchronized void shutdown() {
        connectionFactory = null;
    }

    private ConnectionSpec getConnectionSpec(String credentials) {
        return AccessVerifyConnectionSpec.create(credentials);
    }
}
