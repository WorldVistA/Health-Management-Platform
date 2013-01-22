package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.security.m.SecurityUserVerifyCodeException;

public class VistaLinkVerifyCodeExpiredException extends VistaLinkPermissionDeniedException {
    public VistaLinkVerifyCodeExpiredException(SecurityUserVerifyCodeException fault) {
        super(fault);
    }
}
