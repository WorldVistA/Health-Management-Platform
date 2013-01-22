package EXT.DOMAIN.cpe.vista.rpc.broker.protocol;

import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.Socket;

import java.io.IOException;

/**
 * TODOC: Provide summary documentation of class RpcProtocol
 */
public interface RpcProtocol {

    // TODO: consider adding TimeUnit to signature of connect()
    Socket connect(RpcHost host, int timeout) throws IOException;

    RpcMessageReader createReader(Socket socket);

    RpcMessageWriter createWriter(Socket socket);
}
