package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

/**
 * TODOC: Provide summary documentation of class InternalServerException
 */
public class InternalServerException extends RpcException {
    public InternalServerException(String message) {
        super("M Error - Use ^XTER: " + message);
    }
}
