package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class RpcContextAccessDeniedException extends RpcException {
    public RpcContextAccessDeniedException(String message) {
        super(message); 
    }
}
