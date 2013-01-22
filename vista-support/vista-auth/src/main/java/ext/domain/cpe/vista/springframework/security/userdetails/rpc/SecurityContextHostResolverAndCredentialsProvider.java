package EXT.DOMAIN.cpe.vista.springframework.security.userdetails.rpc;

import EXT.DOMAIN.cpe.vista.rpc.CredentialsProvider;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.RpcHostResolver;
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.VistaIdNotFoundException;
import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHostResolverAndCredentialsProvider implements RpcHostResolver, CredentialsProvider {

    @Override
    public RpcHost resolve(String vistaId) throws VistaIdNotFoundException {
        return getVistaUserDetails() != null ? getVistaUserDetails().getHost() : null;
    }

    @Override
    public String getCredentials(RpcHost host, String userInfo) {
        VistaUserDetails u = getVistaUserDetails();
        if (u == null) return null;
        return RpcUriUtils.toCredentials(u.getDivision(), u.getAccessCode(), u.getVerifyCode());
    }

    private VistaUserDetails getVistaUserDetails() {
        return (VistaUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
