package org.osehra.cpe.vistalink;

import org.osehra.vistalink.rpc.NoRpcContextFaultException;

public class VistaLinkNoRpcContextException extends VistaLinkInvalidUsageException {
    public VistaLinkNoRpcContextException(NoRpcContextFaultException fault) {
        super(fault);
    }
}
