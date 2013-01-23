package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcIoException;

/**
 * Signals that an end of stream has been reached unexpectedly during reading an RPC response.
 */
public class EOFException extends RpcIoException {

    public EOFException() {
        super("unexpected end of stream in RPC response");
    }

    public EOFException(String detail) {
        super(detail);
    }
}
