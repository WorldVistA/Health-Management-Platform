package EXT.DOMAIN.cpe.vista.rpc.broker.conn;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;

public class ServerNotFoundException extends RpcException {

    public ServerNotFoundException(RpcHost host) {
        super("Can't find the server '" + host.getHostname() + "'");
    }
}
