package org.osehra.cpe.vista.springframework.security.web;

import org.osehra.cpe.vista.springframework.security.authentication.VistaAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VistaAccessVerifyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String VISTA_ID_KEY = "j_vistaId";
    public static final String DIVISION_KEY = "j_division";
    public static final String ACCESS_CODE_KEY = "j_access";
    public static final String VERIFY_CODE_KEY = "j_verify";
    public static final String NEW_VERIFY_CODE_KEY = "j_newVerify";
    public static final String CONFIRM_VERIFY_CODE_KEY = "j_confirmVerify";

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String vistaId = obtainVistaId(request);
        String division = obtainStationNumber(request);
        String accessCode = obtainAccessCode(request);
        String verifyCode = obtainVerifyCode(request);
        String newVerifyCode = obtainNewVerifyCode(request);
        String confirmVerifyCode = obtainConfirmVerifyCode(request);
        String remoteAddress = obtainRemoteAddress(request);

        if (accessCode == null) {
            accessCode = "";
        }

        if (verifyCode == null) {
            verifyCode = "";
        }

//        accessCode = accessCode.trim();

        VistaAuthenticationToken authRequest = createToken(vistaId, division, accessCode, verifyCode, newVerifyCode, confirmVerifyCode, remoteAddress);

        // Place the last username attempted into HttpSession for views
//        request.getSession().setAttribute(ACEGI_SECURITY_LAST_USERNAME_KEY, accessCode);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        logger.debug("Attempting authentication with token: " + authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected VistaAuthenticationToken createToken(String vistaId, String division, String accessCode, String verifyCode, String newVerifyCode, String confirmVerifyCode, String remoteAddress) {
        return new VistaAuthenticationToken(vistaId, division, accessCode, verifyCode, newVerifyCode, confirmVerifyCode, remoteAddress);
    }

    protected String obtainVistaId(HttpServletRequest request) {
        return request.getParameter(VISTA_ID_KEY);
    }

    protected String obtainStationNumber(HttpServletRequest request) {
        return request.getParameter(DIVISION_KEY);
    }

    protected String obtainAccessCode(HttpServletRequest request) {
        return request.getParameter(ACCESS_CODE_KEY);
    }

    protected String obtainVerifyCode(HttpServletRequest request) {
        return request.getParameter(VERIFY_CODE_KEY);
    }

    protected String obtainNewVerifyCode(HttpServletRequest request) {
        return request.getParameter(NEW_VERIFY_CODE_KEY);
    }

    protected String obtainConfirmVerifyCode(HttpServletRequest request) {
        return request.getParameter(CONFIRM_VERIFY_CODE_KEY);
    }

    protected String obtainRemoteAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }


}
