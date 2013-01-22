package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.rpc.RpcNotOkForProxyUseException;

public class VistaLinkRpcNotOkForProxyUseException extends VistaLinkInvalidUsageException {
    public VistaLinkRpcNotOkForProxyUseException(RpcNotOkForProxyUseException fault) {
        super(fault);
    }
}
