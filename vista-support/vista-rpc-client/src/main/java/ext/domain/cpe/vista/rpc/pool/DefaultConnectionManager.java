package EXT.DOMAIN.cpe.vista.rpc.pool;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.BrokerConnectionFactory;
import EXT.DOMAIN.cpe.vista.rpc.conn.*;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * This class manages open connections in a pool so that every RPC call doesn't have to open (and close) a new connection.
 */
public class DefaultConnectionManager implements DisposableBean, ConnectionManager, ConnectionFactory {

    public static final int DEFAULT_PULSE = 81000; // milliseconds; 45% of 3 minutes
    public static final int PULSE_PERCENTAGE = 45; // percentage of timeout for pulse frequency

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionManager.class);

    private ConnectionFactory connectionFactory;
    private GenericKeyedObjectPool<String, Connection> connectionPool;

    public DefaultConnectionManager() {
        this(new BrokerConnectionFactory());
    }

    public DefaultConnectionManager(ConnectionFactory connectionFactory) {
        this(connectionFactory, createDefaultPoolConfig());
    }

    public DefaultConnectionManager(ConnectionFactory connectionFactory, GenericKeyedObjectPool.Config poolConfig) {
        this.connectionFactory = connectionFactory;
        this.connectionPool = new GenericKeyedObjectPool<String, Connection>(new PoolableConnectionFactory(this.connectionFactory), poolConfig);
    }

    public DefaultConnectionManager(ConnectionFactory connectionFactory, int maxActive) {
        this.connectionFactory = connectionFactory;
        this.connectionPool = new GenericKeyedObjectPool<String, Connection>(new PoolableConnectionFactory(this.connectionFactory), createPoolConfig(maxActive));
    }

    public void setConfig(GenericKeyedObjectPool.Config poolConfig) {
        this.connectionPool.setConfig(poolConfig);
    }

    public int getMaxActive() {
        return connectionPool.getMaxActive();
    }

    public void setMaxActive(int maxActive) {
        connectionPool.setMaxActive(maxActive);
    }

    public long getMaxWait() {
        return connectionPool.getMaxWait();
    }

    public void setMaxWait(long maxWait) {
        connectionPool.setMaxWait(maxWait);
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return connectionPool.getTimeBetweenEvictionRunsMillis();
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public long getMinEvictableIdleTimeMillis() {
        return connectionPool.getMinEvictableIdleTimeMillis();
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public int getNumActive() {
        return connectionPool.getNumActive();
    }

    public int getNumIdle() {
        return connectionPool.getNumIdle();
    }

    @Override
    public Connection getConnection(RpcHost host, ConnectionSpec auth) throws RpcException {
        return requestConnection(host, auth.toString());
    }

    @Override
    public Connection requestConnection(RpcHost host, String credentials) throws RpcException {
        try {
            String key = PoolKeyUtils.getKey(host, credentials);
            Connection connection = (Connection) connectionPool.borrowObject(key);
            return new ManagedConnection(this, key, connection);
        } catch (RpcException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw new TimeoutWaitingForIdleConnectionException();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            ManagedConnection managedConnection = (ManagedConnection) connection;
            String key = managedConnection.getConnectionKey();
            connectionPool.returnObject(key, managedConnection.getConnection());
        } catch (Exception e) {
            LOGGER.error("Exception occured releasing connection", e);
        }
    }

    @Override
    public void invalidateConnection(Connection connection) {
        try {
            ManagedConnection managedConnection = (ManagedConnection) connection;
            String key = managedConnection.getConnectionKey();
            connectionPool.invalidateObject(key, managedConnection.getConnection());
        } catch (Exception e) {
            LOGGER.warn("Exception occured invalidating connection", e);
        }
    }

    @Override
    public void closeIdleConnections() {
        try {
            connectionPool.clear();
        } catch (Exception e) {
            LOGGER.warn("Exception occurred closing idle connections", e);
        }
    }

    @Override
    public void closeExpiredConnections() {
        try {
            connectionPool.evict();
        } catch (Exception e) {
            LOGGER.warn("Exception occurred closing expired connections", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            connectionPool.close();
        } catch (Exception e) {
            LOGGER.warn("Exception occurred shutting down connection manager", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    static GenericKeyedObjectPool.Config createDefaultPoolConfig() {
        GenericKeyedObjectPool.Config poolConfig = new GenericKeyedObjectPool.Config();
        poolConfig.maxActive = 1; // one object per key
        poolConfig.maxWait = TimeUnit.SECONDS.toMillis(10);
        poolConfig.testWhileIdle = true;
        poolConfig.timeBetweenEvictionRunsMillis = DEFAULT_PULSE;
        poolConfig.minEvictableIdleTimeMillis = TimeUnit.MINUTES.toMillis(5);
        return poolConfig;
    }

    static GenericKeyedObjectPool.Config createPoolConfig(int maxActive) {
        GenericKeyedObjectPool.Config poolConfig = createDefaultPoolConfig();
        poolConfig.maxActive = maxActive;
        return poolConfig;
    }
}
