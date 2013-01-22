package EXT.DOMAIN.cpe.vista.rpc.pool;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.conn.Connection;
import EXT.DOMAIN.cpe.vista.rpc.conn.ConnectionFactory;
import EXT.DOMAIN.cpe.vista.rpc.conn.ConnectionSpec;
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of {@link KeyedPoolableObjectFactory} that uses the supplied {@link ConnectionFactory} to create connections
 * to be pooled by key by a GenericKeyedObjectPool.
 * <p/>
 *
 *
 * @see org.apache.commons.pool.impl.GenericKeyedObjectPool
 */
public class PoolableConnectionFactory implements KeyedPoolableObjectFactory<String, Connection> {

    private static Logger LOG = LoggerFactory.getLogger(PoolableConnectionFactory.class);

    private ConnectionFactory connectionFactory;

    public PoolableConnectionFactory(ConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "connectionFactory must not be null");
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Connection makeObject(String key) throws Exception {
    	ConnectionSpec auth = null;
        try {
            RpcHost host = RpcUriUtils.extractHost(PoolKeyUtils.keyToURI(key));
            auth = PoolKeyUtils.keyToConnectionSpec(key);
            return connectionFactory.getConnection(host, auth);
        } finally {
            LOG.debug("Connection pool: created     {}", RpcUriUtils.sanitize( PoolKeyUtils.keyToURI(key), auth));
        }
    }

    @Override
    public void destroyObject(String key, Connection connection) throws Exception {
        Connection c = (Connection) connection;
        try {
            LOG.debug("Connection pool: destroying  {}", RpcUriUtils.sanitize(PoolKeyUtils.keyToURI(key), PoolKeyUtils.keyToConnectionSpec(key)));
            c.close();

        } catch (RpcException e) {
            throw e;
        } finally {
            LOG.debug("Connection pool: destroyed   {}", RpcUriUtils.sanitize(PoolKeyUtils.keyToURI(key), PoolKeyUtils.keyToConnectionSpec(key)));
        }
    }

    @Override
    public boolean validateObject(String key, Connection connection) {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug("Connection pool: validating  {}", RpcUriUtils.sanitize(PoolKeyUtils.keyToURI(key),PoolKeyUtils.keyToConnectionSpec(key)));
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        Connection c = (Connection) connection;
        if (c.isClosed()) return false;
        return !c.isStale();
    }

    @Override
    public void activateObject(String key, Connection connection) throws Exception {
        LOG.debug("Connection pool: activating  {}", RpcUriUtils.sanitize(PoolKeyUtils.keyToURI(key),PoolKeyUtils.keyToConnectionSpec(key)));
    }

    @Override
    public void passivateObject(String key, Connection connection) throws Exception {
        LOG.debug("Connection pool: passivating {}", RpcUriUtils.sanitize(PoolKeyUtils.keyToURI(key),PoolKeyUtils.keyToConnectionSpec(key)));
    }

}
