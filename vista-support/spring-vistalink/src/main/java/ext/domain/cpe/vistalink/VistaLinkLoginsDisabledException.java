package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.adapter.record.LoginsDisabledFaultException;

public class VistaLinkLoginsDisabledException extends VistaLinkResourceFailureException {
    public VistaLinkLoginsDisabledException(LoginsDisabledFaultException fault) {
        super(fault);
    }
}
