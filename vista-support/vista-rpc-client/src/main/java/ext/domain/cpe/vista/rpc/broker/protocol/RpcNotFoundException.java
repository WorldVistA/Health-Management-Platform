package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

public class RpcNotFoundException extends RpcException {
    public RpcNotFoundException(String message) {
        super(message);
    }
}
