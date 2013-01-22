package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcIoException;

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
