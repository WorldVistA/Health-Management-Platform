package org.osehra.cpe.vista.rpc.broker.protocol;

import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcIoException;
import org.osehra.cpe.vista.rpc.RpcResponse;
import org.osehra.cpe.vista.rpc.TimeoutWaitingForRpcResponseException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

/**
 * TODOC: Provide summary documentation of class RpcMessageReader
 */
public class DefaultRpcMessageReader implements RpcMessageReader {

    private final Reader reader;

    public DefaultRpcMessageReader(final Reader r) {
        this.reader = r;
    }

    public RpcResponse readResponse() throws RpcException {
        String securitySegment = null;
        String applicationSegment = null;
        try {
            securitySegment = readServerPacket();
            applicationSegment = readServerPacket();
        } catch (RpcException e) {
            if (securitySegment != null && securitySegment.startsWith(ServiceTemporarilyDownException.RPC_SERVICE_TEMPORARILY_DOWN_MESSAGE)) {
                throw new ServiceTemporarilyDownException();
            } else {
                throw e;
            }
        }

        StringBuilder responseBuf = new StringBuilder();

        int c = -1;
        do {
            try {
                c = reader.read();
                if (c < 0) throw new IOException("unexpected end of stream");
            } catch (InterruptedIOException e) {
                throw new TimeoutWaitingForRpcResponseException(e);
            } catch (IOException e) {
                throw new RpcIoException("unable to read response", e);
            }
            responseBuf.append((char) c);
        } while (c != 4);
        responseBuf.deleteCharAt(responseBuf.length() - 1);

        if ("U411".equals(applicationSegment))
            throw new BadReadsException();

        RpcResponse response = new RpcResponse(securitySegment, applicationSegment, responseBuf.toString());
        if (response.length() > 0) {
            String[] lines = response.toLines();
            // TODO: check for empty response here
            if (lines[0].length() > 0) {
                if (lines[0].charAt(0) == (char) 24)
                    throw new InternalServerException(response.toLines()[1]);
            }
        }

        return response;
    }

    public String readServerPacket() throws RpcException {
        try {
            int numChars = reader.read();
            if (numChars == 0) return "";
            if (numChars < 0) throw new EOFException();
            char[] buf = new char[numChars];
            reader.read(buf);
            return new String(buf);
        } catch (InterruptedIOException e) {
            throw new TimeoutWaitingForRpcResponseException(e);
        } catch (IOException e) {
            throw new RpcIoException("unable to read server packet", e);
        }
    }
}
