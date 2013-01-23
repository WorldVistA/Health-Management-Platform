package org.osehra.cpe.vista.springframework.security.userdetails.rpc;

import org.osehra.cpe.vista.rpc.CredentialsProvider;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.RpcHostResolver;
import org.osehra.cpe.vista.rpc.broker.conn.VistaIdNotFoundException;
import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.osehra.cpe.vista.util.RpcUriUtils;
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
