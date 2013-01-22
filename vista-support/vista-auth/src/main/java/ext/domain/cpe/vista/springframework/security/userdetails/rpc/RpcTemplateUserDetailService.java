package EXT.DOMAIN.cpe.vista.springframework.security.userdetails.rpc;

import EXT.DOMAIN.cpe.vista.rpc.ConnectionCallback;
import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.RpcContextAccessDeniedException;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.VerifyCodeExpiredException;
import EXT.DOMAIN.cpe.vista.rpc.conn.Connection;
import EXT.DOMAIN.cpe.vista.rpc.conn.ConnectionUserDetails;
import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUser;
import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetailsService;
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.springframework.util.StringUtils.hasLength;

/**
 * TODOC: Provide summary documentation of class RpcTemplateUserDetailService
 */
public class RpcTemplateUserDetailService implements VistaUserDetailsService {

    private RpcOperations rpcTemplate;

    public RpcOperations getRpcTemplate() {
        return rpcTemplate;
    }

    @Required
    public void setRpcTemplate(RpcOperations rpcTemplate) {
        this.rpcTemplate = rpcTemplate;
    }

    public final VistaUserDetails login(final String vistaId, final String division, final String accessCode, final String verifyCode, String newVerifyCode, String confirmNewVerifyCode, final String remoteAddress) throws BadCredentialsException, DataAccessException {
        if (!hasLength(vistaId)) throw new BadCredentialsException("missing vistaId");
        if (!hasLength(division)) throw new BadCredentialsException("missing division");
        if (!hasLength(accessCode)) throw new BadCredentialsException("missing access code");
        if (!hasLength(verifyCode)) throw new BadCredentialsException("missing verify code");
        if (!hasLength(remoteAddress)) throw new BadCredentialsException("missing remote address");

        /*
         * We're using this special variable to get around nested connection requests (our connection pool only allows one connection for a given key at a time.)
         */
        ConnectionInfoCallback cb = new ConnectionInfoCallback();
        try {
            getRpcTemplate().execute(cb, RpcUriUtils.VISTA_RPC_BROKER_SCHEME + "://" + RpcUriUtils.toAuthority(vistaId, division, accessCode, verifyCode, newVerifyCode, confirmNewVerifyCode));
            return createVistaUserDetails(cb.h, vistaId, division, cb.ud);
        } catch (PermissionDeniedDataAccessException e) {
            translateException(e);
        }
        return null;
    }

    protected void translateException(PermissionDeniedDataAccessException e) throws DataAccessException {
        if (e.getCause() instanceof EXT.DOMAIN.cpe.vista.rpc.broker.protocol.BadCredentialsException) {
            throw new BadCredentialsException(e.getCause().getMessage());
        } else if (e.getCause() instanceof VerifyCodeExpiredException) {
            throw new CredentialsExpiredException(e.getCause().getMessage());
        } else if (e.getCause() instanceof RpcContextAccessDeniedException) {
            throw new AuthenticationServiceException(e.getCause().getMessage(), e.getCause());
        } else {
            throw e;
        }
    }

    protected VistaUserDetails createVistaUserDetails(RpcHost host, String vistaId, String division, ConnectionUserDetails userDetails) {
        VistaUser u = new VistaUser(host,
                vistaId,
                division,
                userDetails.getDUZ(),
                userDetails.getAccessCode(),
                userDetails.getVerifyCode(),
                userDetails.getName(),
                true,
                true,
                true,
                true,
                Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority("ROLE_USER")));
        u.setDivisionName(userDetails.getDivisionNames().get(division));
        u.setTitle(userDetails.getTitle());
        u.setServiceSection(userDetails.getServiceSection());
        u.setLanguage(userDetails.getLanguage());
        u.setDTime(userDetails.getDTime());
        u.setVPID(userDetails.getVPID());
        return u;
    }

    public void logout(VistaUserDetails user) throws DataAccessException {
        // NOOP
    }
    
    private class ConnectionInfoCallback implements ConnectionCallback<VistaUserDetails>
    {
    	public RpcHost h = null;
    	public ConnectionUserDetails ud = null;
    	@Override
    	public VistaUserDetails doInConnection(Connection con) throws RpcException, DataAccessException {
    		h = con.getHost();
    		ud = con.getUserDetails();
    		return null;
    	}
    }
}
