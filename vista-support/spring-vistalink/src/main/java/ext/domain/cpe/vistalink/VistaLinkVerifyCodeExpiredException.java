package org.osehra.cpe.vistalink;

import org.osehra.vistalink.security.m.SecurityUserVerifyCodeException;

public class VistaLinkVerifyCodeExpiredException extends VistaLinkPermissionDeniedException {
    public VistaLinkVerifyCodeExpiredException(SecurityUserVerifyCodeException fault) {
        super(fault);
    }
}
