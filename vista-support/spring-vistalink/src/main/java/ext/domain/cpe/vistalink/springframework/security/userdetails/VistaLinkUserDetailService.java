package org.osehra.cpe.vistalink.springframework.security.userdetails;

import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetailsService;
import org.osehra.cpe.vistalink.ConnectionFactoryLocator;
import org.osehra.cpe.vistalink.VistaLinkDaoSupport;
import org.osehra.cpe.vistalink.VistaLinkTemplate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.StringUtils.hasLength;

public class VistaLinkUserDetailService extends VistaLinkDaoSupport implements VistaUserDetailsService {

    static final int DEFAULT_TIMEOUT = 600;

    static final String RPC_CONTEXT = "XUS KAAJEE WEB LOGON";
    static final String GET_USER_INFO_RPC = "XUS KAAJEE GET USER INFO";
    static final String LOGOUT_RPC_NAME = "XUS KAAJEE LOGOUT";

    private String applicationName;

    private VistaUserCache userCache;

    @Required
    public void setUserCache(VistaUserCache userCache) {
        this.userCache = userCache;
    }

    protected VistaLinkTemplate createRpcTemplate(ConnectionFactoryLocator connectionFactoryLocator) {
        VistaLinkTemplate template = super.createRpcTemplate(connectionFactoryLocator);
        template.setTimeOut(DEFAULT_TIMEOUT);
        return template;
    }

    public VistaUserDetails login(String vistaId, String division, String accessCode, String verifyCode, String newVerifyCode, String confirmNewVerifyCode, String remoteAddress) throws BadCredentialsException, DataAccessException {
        if (!hasLength(division)) throw new BadCredentialsException("missing station number");
        if (!hasLength(accessCode)) throw new BadCredentialsException("missing access code");
        if (!hasLength(verifyCode)) throw new BadCredentialsException("missing verify code");
        if (!hasLength(remoteAddress)) throw new BadCredentialsException("missing remote address");
        try {
            String result = getRpcTemplate().rpc(new VistaLinkAccessVerifyConnectionSpec(division, accessCode, verifyCode, remoteAddress), division, null, RPC_CONTEXT, GET_USER_INFO_RPC, createLoginParams(remoteAddress));
            VistaUserDetails user = createVistaUserDetails(result, accessCode, verifyCode);
            userCache.putUserInCache(user);
            return user;
        } catch (DataAccessException e) {
            throw new BadCredentialsException("couldn't log in", e);
        }
    }

    public void logout(VistaUserDetails user) throws DataAccessException {
        userCache.removeUserFromCache(user.getAccessCode(), user.getVerifyCode());
        getRpcTemplate().rpcAsUser(user.getDivision(), user.getDUZ(), RPC_CONTEXT, LOGOUT_RPC_NAME, createLogoutParams((VistaLinkVistaUserDetails) user));
    }

    private List createLoginParams(String remoteAddress) {
        List params = new ArrayList();
        params.add(remoteAddress);
        params.add(getApplicationName());
        return params;
    }


    private List createLogoutParams(VistaLinkVistaUserDetails user) {
        List params = new ArrayList();
        params.add(user.getSignonLogInternalEntryNumber());
        return params;
    }

    /*
     * Result(0) is the userCache DUZ.
     * Result(1) is the user name from the .01 field.
     * Result(2) is the userCache full name from the name standard file.
     * Result(3) is the FAMILY (LAST) NAME (or ^ if null)
     * Result(4) is the GIVEN (FIRST) NAME (or ^ if null)
     * Result(5) is the MIDDLE NAME (or ^ if null)
     * Result(6) is the PREFIX (or ^ if null)
     * Result(7) is the SUFFIX (or ^ if null)
     * Result(8) is the DEGREE (or ^ if null)
     * Result(9) is station # of the division that the user is working in.
     * Result(10) is the station # of the parent facility for the login division
     * Result(11) is the station # of the computer system "parent" from the KSP file.
     * Result(12) is the IEN of the signon log entry
     * Result(13) = # of permissible divisions
     * Result(14-n) are the permissible divisions for user login, in the format:
     *             IEN of file 4^Station Name^Station Number^default? (1 or 0)
     */
    protected VistaUserDetails createVistaUserDetails(String result, String accessCode, String verifyCode) {
        String[] results = result.split("\n");
        VistaLinkVistaUser u = new VistaLinkVistaUser(results[12].trim(),
                results[9].trim(),
                results[0].trim(),
                accessCode,
                verifyCode,
                true,
                true,
                true,
                true,
                Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority("ROLE_USER")}));
        u.setPersonName(results[1].trim());
        u.setDisplayName(results[2].trim());
        u.setFamilyName(nullSafeGet(results[3].trim()));
        u.setGivenName(nullSafeGet(results[4].trim()));
        u.setMiddleName(nullSafeGet(results[5].trim()));
        u.setPrefix(nullSafeGet(results[6].trim()));
        u.setSuffix(nullSafeGet(results[7].trim()));
        u.setDegree(nullSafeGet(results[8].trim()));
        return u;
    }

    private String nullSafeGet(String value) {
        if (value.equals("^")) return null;
        return value;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
