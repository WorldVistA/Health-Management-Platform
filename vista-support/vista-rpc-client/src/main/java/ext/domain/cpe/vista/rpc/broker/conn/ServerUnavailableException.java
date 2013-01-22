package EXT.DOMAIN.cpe.vista.rpc.broker.conn;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;

public class ServerUnavailableException extends RpcException {
    public ServerUnavailableException(RpcHost host) {
        super("The VistA RPC Broker listener on port " + host.getPort() + " at '" + host.getHostname() + "' is not responding and is unavailable");
    }
}
