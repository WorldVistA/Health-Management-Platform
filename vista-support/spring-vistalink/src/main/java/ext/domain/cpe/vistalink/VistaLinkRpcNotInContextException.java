package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.rpc.RpcNotInContextFaultException;

public class VistaLinkRpcNotInContextException extends VistaLinkInvalidUsageException {
    public VistaLinkRpcNotInContextException(RpcNotInContextFaultException fault) {
        super(fault);
    }
}
