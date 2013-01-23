package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class AuthenticationException extends RpcException {
    public AuthenticationException(String message) {
        super(message);
    }
}
