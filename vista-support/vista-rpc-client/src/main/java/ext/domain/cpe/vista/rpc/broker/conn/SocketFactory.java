package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcHost;

import java.io.IOException;

/**
 * TODOC: Provide summary documentation of class SocketFactory
 */
public interface SocketFactory {
    Socket createSocket(RpcHost host) throws IOException;

    ServerSocket createServerSocket() throws IOException;
}
