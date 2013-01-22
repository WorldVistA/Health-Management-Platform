package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.adapter.record.VistaLinkFaultException;
import org.springframework.dao.DataAccessResourceFailureException;

public class VistaLinkResourceFailureException extends DataAccessResourceFailureException {
    private VistaLinkFaultException fault;

    public VistaLinkResourceFailureException(VistaLinkFaultException fault) {
        super(fault.getErrorMessage(), fault);
        this.fault = fault;
    }

    public String getErrorCode() {
        return fault.getErrorCode();
    }

    public String getErrorMessage() {
        return fault.getErrorMessage();
    }

    public String getErrorType() {
        return fault.getErrorType();
    }

    public String getFaultActor() {
        return fault.getFaultActor();
    }

    public String getFaultCode() {
        return fault.getFaultCode();
    }

    public String getFaultString() {
        return fault.getFaultString();
    }
}
