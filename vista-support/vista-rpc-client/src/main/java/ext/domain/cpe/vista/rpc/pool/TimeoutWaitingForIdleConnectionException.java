package org.osehra.cpe.vista.rpc.pool;

import org.osehra.cpe.vista.rpc.RpcException;

/**
 * Exception thrown when a when a request for a connection from a ConnectionManager times out.
 */
public class TimeoutWaitingForIdleConnectionException extends RpcException {

    public TimeoutWaitingForIdleConnectionException() {
        super("Timeout waiting for idle connection");
    }
}
