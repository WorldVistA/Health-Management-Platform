package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

public class ServiceTemporarilyDownException extends RpcException {
    public static final String RPC_SERVICE_TEMPORARILY_DOWN_MESSAGE = "21 Service temporarily down.";

    public ServiceTemporarilyDownException() {
        super("Service temporarily down. This is due to either insufficient license units or logins having been disabled.");
    }
}
