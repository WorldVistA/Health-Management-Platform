package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcResponse;

/**
 * TODOC: Provide summary documentation of class org.osehra.cpe.vista.protocol.impl.RpcProtocolReader
 */
public interface RpcMessageReader {
    RpcResponse readResponse() throws RpcException;
}
