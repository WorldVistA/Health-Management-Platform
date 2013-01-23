package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class RpcNotFoundException extends RpcException {
    public RpcNotFoundException(String message) {
        super(message);
    }
}
