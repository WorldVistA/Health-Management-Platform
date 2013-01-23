package org.osehra.cpe.vistalink;

import org.osehra.vistalink.rpc.RpcNotInContextFaultException;

public class VistaLinkRpcNotInContextException extends VistaLinkInvalidUsageException {
    public VistaLinkRpcNotInContextException(RpcNotInContextFaultException fault) {
        super(fault);
    }
}
