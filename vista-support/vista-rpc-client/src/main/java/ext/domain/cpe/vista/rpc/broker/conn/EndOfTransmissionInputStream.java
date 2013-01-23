package org.osehra.cpe.vista.rpc.broker.conn;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODOC: Provide summary documentation of class org.osehra.cpe.vista.impl.BufferedInputStream
 */
public class EndOfTransmissionInputStream extends InputStream {

    private static final int EOT = 4;

    private volatile InputStream in;
    private boolean readEOT = false;
    private byte[] resumeBuf;

    public EndOfTransmissionInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        if (readEOT) {
            readEOT = false;
            return -1;
        }
        int l = in.read();
        if (l == EOT) {
            readEOT = true;
        }
        return l;
    }

//    @Override               
//    public int read(byte[] b, int off, int len) throws IOException {
//        if (readEOT) {
//            readEOT = false;
//            return -1;
//        }
//        byte[] buf = new byte[len];
//        int bytesRead = in.read(buf);
//        for (int i = 0; i < bytesRead; i++) {
//            b[off + i] = buf[i];
//            if (buf[i] == EOT) {
//                readEOT = true;
//                if (i + 1 < bytesRead){
//                    resumeBuf = Arrays.copyOfRange(buf, 0, i+1);
//                }
//                return i + 1;
//            }
//        }
//        return bytesRead;
//    }
}
