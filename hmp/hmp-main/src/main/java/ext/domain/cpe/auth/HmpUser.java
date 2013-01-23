package org.osehra.cpe.auth;

import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.springframework.security.userdetails.VistaUser;
import org.osehra.cpe.vpr.UidUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HmpUser extends VistaUser implements HmpUserDetails {

    private int timeoutSeconds;
    private int timeoutCountdownSeconds;

    private OrderingRole orderingRole = OrderingRole.NONE;

    private Set<VistaSecurityKey> securityKeys = new HashSet<VistaSecurityKey>();
    private Set<VistaUserClass> userClasses = new HashSet<VistaUserClass>();

    private Set<TeamPosition> teamPositions;

    public HmpUser(RpcHost host,
                   String vistaId,
                   String division,
                   String duz,
                   String accessCode,
                   String verifyCode,
                   String displayName,
                   boolean enabled,
                   boolean accountNonExpired,
                   boolean credentialsNonExpired,
                   boolean accountNonLocked,
                   int timeoutSeconds,
                   int timeoutCountdownSeconds,
                   Collection<GrantedAuthority> authorities,
                   Collection<TeamPosition> teamPositions) {
        super(host, vistaId, division, duz, accessCode, verifyCode, displayName, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);

        this.timeoutSeconds = timeoutSeconds;
        this.timeoutCountdownSeconds = timeoutCountdownSeconds;
        this.teamPositions = Collections.unmodifiableSet(new HashSet<TeamPosition>(teamPositions));

        for (GrantedAuthority authority : authorities) {
            if (authority instanceof OrderingRole) {
                this.orderingRole = (OrderingRole) authority;
            }
            if (authority instanceof VistaSecurityKey) {
                securityKeys.add((VistaSecurityKey) authority);
            }
            if (authority instanceof VistaUserClass) {
                userClasses.add((VistaUserClass) authority);
            }
        }
    }

    @Override
    public String getUid() {
        return UidUtils.getUserUid(getVistaId(), getDUZ());
    }

    @Override
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    @Override
    public int getTimeoutCountdownSeconds() {
        return timeoutCountdownSeconds;
    }

    @Override
    public OrderingRole getOrderingRole() {
        return orderingRole;
    }

    @Override
    public boolean hasAuthority(String authority) {
        boolean hasAuthority = false;
        for (GrantedAuthority grantedAuthority : authorities) {
            hasAuthority = grantedAuthority.getAuthority().equals(authority);
            if (hasAuthority) break;
        }
        return hasAuthority;
    }

    @Override
    public boolean hasVistaKey(String key) {
        if (key.startsWith(VistaSecurityKey.VISTA_KEY_PREFIX)) {
            key = key.substring(VistaSecurityKey.VISTA_KEY_PREFIX.length());
            key = key.replace('_', ' ');
        }
        for (VistaSecurityKey k : securityKeys) {
            if (k.getKey().equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    @Override
    public Set<VistaSecurityKey> getSecurityKeys() {
        return Collections.unmodifiableSet(securityKeys);
    }

    @Override
    public Set<VistaUserClass> getUserClasses() {
        return Collections.unmodifiableSet(userClasses);
    }

    @Override
    public Set<TeamPosition> getTeamPositions() {
        return teamPositions;
    }
}
