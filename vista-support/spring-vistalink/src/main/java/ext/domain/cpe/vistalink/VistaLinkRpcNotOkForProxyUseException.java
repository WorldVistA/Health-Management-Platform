package org.osehra.cpe.vistalink;

import org.osehra.vistalink.rpc.RpcNotOkForProxyUseException;

public class VistaLinkRpcNotOkForProxyUseException extends VistaLinkInvalidUsageException {
    public VistaLinkRpcNotOkForProxyUseException(RpcNotOkForProxyUseException fault) {
        super(fault);
    }
}
