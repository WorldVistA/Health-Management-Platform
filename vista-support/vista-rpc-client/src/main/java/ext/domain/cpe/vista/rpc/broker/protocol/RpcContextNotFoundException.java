package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

public class RpcContextNotFoundException extends RpcException {
    public RpcContextNotFoundException(String message) {
        super(message);
    }
}
