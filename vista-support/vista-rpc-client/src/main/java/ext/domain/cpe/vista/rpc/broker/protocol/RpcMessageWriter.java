package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcRequest;

/**
 * TODOC: Provide summary documentation of class EXT.DOMAIN.cpe.vista.protocol.impl.RpcRequestWriter
 */
public interface RpcMessageWriter {
    void writeStartConnection(String hostname, String address, int localPort) throws RpcException;

    void writeStopConnection() throws RpcException;

    void write(RpcRequest request) throws RpcException;

    void flush() throws RpcException;
}
