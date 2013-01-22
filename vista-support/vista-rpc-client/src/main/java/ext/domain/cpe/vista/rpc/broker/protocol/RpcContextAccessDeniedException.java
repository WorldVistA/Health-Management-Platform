package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

public class RpcContextAccessDeniedException extends RpcException {
    public RpcContextAccessDeniedException(String message) {
        super(message); 
    }
}
