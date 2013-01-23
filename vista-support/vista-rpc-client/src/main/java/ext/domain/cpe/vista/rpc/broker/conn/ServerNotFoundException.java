package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;

public class ServerNotFoundException extends RpcException {

    public ServerNotFoundException(RpcHost host) {
        super("Can't find the server '" + host.getHostname() + "'");
    }
}
