package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;

/**
 * TODOC: Provide summary documentation of class InternalServerException
 */
public class InternalServerException extends RpcException {
    public InternalServerException(String message) {
        super("M Error - Use ^XTER: " + message);
    }
}
