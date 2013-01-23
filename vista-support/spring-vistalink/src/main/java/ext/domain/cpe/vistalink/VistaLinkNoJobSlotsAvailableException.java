package org.osehra.cpe.vistalink;

import org.osehra.vistalink.adapter.record.NoJobSlotsAvailableFaultException;

public class VistaLinkNoJobSlotsAvailableException extends VistaLinkTransientDataAccessResourceException {
    public VistaLinkNoJobSlotsAvailableException(NoJobSlotsAvailableFaultException fault) {
        super(fault);
    }
}
