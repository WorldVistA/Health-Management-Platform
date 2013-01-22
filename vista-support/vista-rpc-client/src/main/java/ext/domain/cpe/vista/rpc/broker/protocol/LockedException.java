package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;

public class LockedException extends RpcException {
    public LockedException(String message) {
        super(message); 
    }
}
