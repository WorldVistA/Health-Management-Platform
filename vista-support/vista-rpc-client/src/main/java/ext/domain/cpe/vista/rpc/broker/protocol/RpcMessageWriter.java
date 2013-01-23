package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcRequest;

/**
 * TODOC: Provide summary documentation of class org.osehra.cpe.vista.protocol.impl.RpcRequestWriter
 */
public interface RpcMessageWriter {
    void writeStartConnection(String hostname, String address, int localPort) throws RpcException;

    void writeStopConnection() throws RpcException;

    void write(RpcRequest request) throws RpcException;

    void flush() throws RpcException;
}
