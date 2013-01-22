package EXT.DOMAIN.cpe.vista.springframework.security.authentication;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.springframework.util.StringUtils.hasLength;

public class VistaAuthenticationToken extends UsernamePasswordAuthenticationToken {

    static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    private static final String SEP = ";";

    private String vistaId;
    private String accessCode;
    private String verifyCode;
    private String newVerifyCode;
    private String confirmVerifyCode;

    public VistaAuthenticationToken(String vistaId, String division, String accessCode, String verifyCode, String remoteAddress) {
        this(vistaId, division, accessCode, verifyCode, null, null, remoteAddress);
    }

    public VistaAuthenticationToken(String vistaId, String division, String accessCode, String verifyCode, String newVerifyCode, String confirmVerifyCode, String remoteAddress) {
        super(UNAUTHENTICATED + "@" + vistaId + SEP + division, (hasLength(accessCode) && hasLength(verifyCode) && hasLength(remoteAddress) ? accessCode + SEP + verifyCode + SEP + remoteAddress : null));
        this.vistaId = vistaId;
        this.accessCode = hasLength(accessCode) ? accessCode : null;
        this.verifyCode = hasLength(verifyCode) ? verifyCode : null;
        this.newVerifyCode = hasLength(newVerifyCode) ? newVerifyCode : null;
        this.confirmVerifyCode = hasLength(confirmVerifyCode) ? confirmVerifyCode : null;
        super.setDetails(hasLength(remoteAddress) ? remoteAddress : null);
    }

    public VistaAuthenticationToken(VistaUserDetails user, String accessCode, String verifyCode, String remoteAddress, Collection<? extends GrantedAuthority> authorities) {
        super(user, (hasLength(accessCode) && hasLength(verifyCode) && hasLength(remoteAddress) ? accessCode + SEP + verifyCode + SEP + remoteAddress : null), authorities);
        this.vistaId = user.getVistaId();
        this.accessCode = hasLength(accessCode) ? accessCode : null;
        this.verifyCode = hasLength(verifyCode) ? verifyCode : null;
        super.setDetails(hasLength(remoteAddress) ? remoteAddress : null);
    }

    public String getVistaId() {
        return vistaId;
    }

    public VistaUserDetails getVistaUserDetails() {
        if (isAuthenticated())
            return (VistaUserDetails) getPrincipal();
        return null;
    }

    public String getDuz() {
        if (isAuthenticated())
            return getVistaUserDetails().getDUZ();
        return null;
    }

    public String getDivision() {
        if (isAuthenticated())
            return getVistaUserDetails().getDivision();
        else
            return ((String) getPrincipal()).substring(((String) getPrincipal()).lastIndexOf(SEP) + 1);
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getNewVerifyCode() {
        return newVerifyCode;
    }

    public String getConfirmVerifyCode() {
        return confirmVerifyCode;
    }

    public String getRemoteAddress() {
        return (String) getDetails();
    }

    public void setDetails(Object details) {
        // NOOP
    }
}
