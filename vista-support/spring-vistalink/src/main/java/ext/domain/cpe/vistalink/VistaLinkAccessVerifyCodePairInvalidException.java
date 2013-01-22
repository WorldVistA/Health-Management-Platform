package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.vistalink.security.m.SecurityAccessVerifyCodePairInvalidException;

public class VistaLinkAccessVerifyCodePairInvalidException extends VistaLinkPermissionDeniedException {
    public VistaLinkAccessVerifyCodePairInvalidException(SecurityAccessVerifyCodePairInvalidException e) {
        super(e);
    }
}
