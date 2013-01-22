package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;

/**
 * TODOC: Provide summary documentation of class EXT.DOMAIN.cpe.vista.protocol.impl.RpcProtocolReader
 */
public interface RpcMessageReader {
    RpcResponse readResponse() throws RpcException;
}
