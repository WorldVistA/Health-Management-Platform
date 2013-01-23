package org.osehra.cpe.vistalink;

import org.osehra.vistalink.adapter.record.LoginsDisabledFaultException;

public class VistaLinkLoginsDisabledException extends VistaLinkResourceFailureException {
    public VistaLinkLoginsDisabledException(LoginsDisabledFaultException fault) {
        super(fault);
    }
}
