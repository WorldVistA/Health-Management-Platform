package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.broker.conn.EndOfTransmissionInputStream;
import org.osehra.cpe.vista.rpc.broker.conn.Socket;
import org.osehra.cpe.vista.rpc.broker.conn.SocketFactory;
import org.osehra.cpe.vista.rpc.support.Wire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

public abstract class AbstractRpcProtocol implements RpcProtocol {

    public static final Charset VISTA_CHARSET = Charset.forName("ISO-8859-1");
    public static final int READ_BUFFER_SIZE = 32767;

    public static final String R_ACCEPT = "accept";

    private static final Logger wireLog = LoggerFactory.getLogger("org.osehra.cpe.vista.rpc.wire");

    protected Logger log = LoggerFactory.getLogger(getClass());
    protected SocketFactory socketFactory;

    private Wire wire;

    public AbstractRpcProtocol(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
        if (wireLog.isDebugEnabled())
            wire = new Wire(wireLog);
    }

    @Override
    public abstract Socket connect(RpcHost host, int timeout) throws IOException;

    @Override
    public final RpcMessageReader createReader(Socket socket) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new EndOfTransmissionInputStream(socket.in()), READ_BUFFER_SIZE);
            return createReader(new InputStreamReader(wire != null ? new LoggingInputStream(bufferedInputStream, wire) : bufferedInputStream, VISTA_CHARSET));
        } catch (IOException e) {
            throw new RpcException("unable to create rpc message reader", e);
        }
    }

    @Override
    public final RpcMessageWriter createWriter(Socket socket) {
        try {
            return createWriter(new BufferedWriter(new OutputStreamWriter(wire != null ? new LoggingOutputStream(socket.out(), wire) : socket.out(), VISTA_CHARSET)));
        } catch (IOException e) {
            throw new RpcException("unable to create rpc message writer", e);
        }
    }

    protected abstract RpcMessageReader createReader(Reader r);

    protected abstract RpcMessageWriter createWriter(Writer w);
}
