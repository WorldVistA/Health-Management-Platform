package EXT.DOMAIN.cpe.vista.rpc.broker.conn;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.*;
import EXT.DOMAIN.cpe.vista.rpc.conn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * This class is responsible for opening and authenticating connections to VistA systems using
 * the VistA Remote Procedure Call Broker protocol.
 */
public class BrokerConnectionFactory implements ConnectionFactory {

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLISECONDS = 10000; // milliseconds

    protected Logger log = LoggerFactory.getLogger(getClass());

    private boolean oldProtocolOnly = false;
    private boolean backwardsCompatible = true;
    private SocketFactory socketFactory;

    public BrokerConnectionFactory() {
        this(new DefaultSocketFactory());
    }

    public BrokerConnectionFactory(SocketFactory socketFactory) {
        Assert.notNull(socketFactory, "[Assertion failed] - socketFactory must not be null");
        this.socketFactory = socketFactory;
    }

    public boolean isBackwardsCompatible() {
        return backwardsCompatible;
    }

    public void setBackwardsCompatible(boolean backwardsCompatible) {
        this.backwardsCompatible = backwardsCompatible;
    }

    public boolean isOldProtocolOnly() {
        return oldProtocolOnly;
    }

    public void setOldProtocolOnly(boolean oldProtocolOnly) {
        this.oldProtocolOnly = oldProtocolOnly;
    }

    public synchronized Connection getConnection(RpcHost host, ConnectionSpec auth) throws RpcException {
        if (auth == null) throw new IllegalArgumentException("connectionSpec must not be null");
        if (!(auth instanceof AccessVerifyConnectionSpec) && !(auth instanceof AnonymousConnectionSpec))
            throw new UnsupportedOperationException("only anonymous and access/verify authentication currently supported");

        log.debug("Creating connection to " + host.toString());

        RpcProtocol protocol = isOldProtocolOnly() ? new OldRpcProtocol(socketFactory) : new NewRpcProtocol(socketFactory);

        Socket socket = null;
        try {
            socket = protocol.connect(host, DEFAULT_CONNECT_TIMEOUT_MILLISECONDS);
        } catch (ServiceTemporarilyDownException e) {
            throw e;
        } catch (IOException e) {
            throw new RpcException(e);
        } catch (EOFException e) {
            if (protocol instanceof NewRpcProtocol && isBackwardsCompatible()) {
                protocol = new OldRpcProtocol(socketFactory);
                try {
                    socket = protocol.connect(host, DEFAULT_CONNECT_TIMEOUT_MILLISECONDS);
                } catch (IOException ex) {
                    throw new RpcException(ex);
                }
            } else {
                throw new UnsupportedProtocolException();
            }
        } catch (RpcException e) {
            throw e;
        }
        if (socket == null) throw new RpcException("unable to connect");
        BrokerConnection connection = new BrokerConnection(host, socket, protocol);
        try {
            connection.getSystemInfo();

            if (auth instanceof AccessVerifyConnectionSpec) {
                connection.authenticate((AccessVerifyConnectionSpec) auth);
            }
//            else {
//                TODO: implement other authentication schemes here (appHandle, NTLogon?)
//            }
        } catch (RpcException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

}
