package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.rpc.NoRpcContextFaultException;

public class VistaLinkNoRpcContextException extends VistaLinkInvalidUsageException {
    public VistaLinkNoRpcContextException(NoRpcContextFaultException fault) {
        super(fault);
    }
}
