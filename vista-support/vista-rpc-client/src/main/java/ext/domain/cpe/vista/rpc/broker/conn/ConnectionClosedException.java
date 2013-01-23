package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcException;

public class ConnectionClosedException extends RpcException {

    public ConnectionClosedException() {
        this("connection has already been closed");
    }

    public ConnectionClosedException(String message) {
        super(message);
    }
}
