package EXT.DOMAIN.cpe.vista.rpc.broker.conn;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

public class ConnectionClosedException extends RpcException {

    public ConnectionClosedException() {
        this("connection has already been closed");
    }

    public ConnectionClosedException(String message) {
        super(message);
    }
}
