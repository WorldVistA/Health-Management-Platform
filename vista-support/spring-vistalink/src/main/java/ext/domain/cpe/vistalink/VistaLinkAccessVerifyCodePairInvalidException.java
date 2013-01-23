package org.osehra.cpe.vistalink;

import org.osehra.vistalink.security.m.SecurityAccessVerifyCodePairInvalidException;

public class VistaLinkAccessVerifyCodePairInvalidException extends VistaLinkPermissionDeniedException {
    public VistaLinkAccessVerifyCodePairInvalidException(SecurityAccessVerifyCodePairInvalidException e) {
        super(e);
    }
}
