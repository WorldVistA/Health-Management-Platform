package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class LockedException extends RpcException {
    public LockedException(String message) {
        super(message); 
    }
}
