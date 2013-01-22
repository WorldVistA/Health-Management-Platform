package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.adapter.record.NoJobSlotsAvailableFaultException;

public class VistaLinkNoJobSlotsAvailableException extends VistaLinkTransientDataAccessResourceException {
    public VistaLinkNoJobSlotsAvailableException(NoJobSlotsAvailableFaultException fault) {
        super(fault);
    }
}
