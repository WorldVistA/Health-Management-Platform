package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class RpcContextNotFoundException extends RpcException {
    public RpcContextNotFoundException(String message) {
        super(message);
    }
}
