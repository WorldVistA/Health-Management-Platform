package org.osehra.cpe.vistalink;

import org.osehra.vistalink.adapter.record.VistaLinkFaultException;
import org.springframework.dao.DataRetrievalFailureException;

public class VistaLinkDataRetrievalFailureException extends DataRetrievalFailureException {

    private VistaLinkFaultException fault;

    public VistaLinkDataRetrievalFailureException(VistaLinkFaultException fault) {
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
