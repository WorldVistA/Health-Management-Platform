package org.osehra.cpe.auth;

import org.osehra.cpe.vista.rpc.RpcHost;

public class HmpVersionMismatchException extends RuntimeException {
    public HmpVersionMismatchException(String webHmpVersion, String vistaHmpVersion, RpcHost host, String vistaId, String division) {
        super("This is version '" + webHmpVersion + "', VistA server is running version '" + vistaHmpVersion + "'.");
    }
}
