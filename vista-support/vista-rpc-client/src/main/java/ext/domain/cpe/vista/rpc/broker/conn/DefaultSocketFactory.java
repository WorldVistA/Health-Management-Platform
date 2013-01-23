package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcHost;

import java.io.IOException;

/**
 * TODOC: Provide summary documentation of class DefaultSocketFactory
 */
public class DefaultSocketFactory implements SocketFactory {
    public Socket createSocket(RpcHost host) throws IOException {
        return new DefaultSocket(host.getHostname(), host.getPort());
    }

    public ServerSocket createServerSocket() throws IOException {
        return new DefaultServerSocket(new java.net.ServerSocket(0));
    }
}
