package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;

public class ServerUnavailableException extends RpcException {
    public ServerUnavailableException(RpcHost host) {
        super("The VistA RPC Broker listener on port " + host.getPort() + " at '" + host.getHostname() + "' is not responding and is unavailable");
    }
}
