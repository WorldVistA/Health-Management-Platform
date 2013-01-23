package org.osehra.cpe.auth;

import org.osehra.cpe.HmpProperties;
import org.osehra.cpe.datetime.format.FileManDateTimeFormat;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.conn.ConnectionUserDetails;
import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.osehra.cpe.vista.springframework.security.userdetails.rpc.RpcTemplateUserDetailService;
import org.osehra.cpe.vpr.UserInterfaceRpcConstants;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * TODOC: Provide summary documentation of class RpcTemplateUserDetailService
 */
public class HmpUserDetailsService extends RpcTemplateUserDetailService implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected VistaUserDetails createVistaUserDetails(RpcHost host, String vistaId, String division, ConnectionUserDetails userDetails) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // default spring security convention

        JsonNode json = fetchUserInfo(host, userDetails);

        validateHmpVersion(json, host, vistaId, division);

        addOrderingRoleAuthority(authorities, json);
        addVistaKeyAuthorities(authorities, json);
        addVistaUserClassAuthorities(authorities, json);
        Set<TeamPosition> teamPositions = getTeamPositions(json);

        HmpUser u = new HmpUser(host,
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
                json.get("timeout").asInt(),
                json.get("timeoutCounter").asInt(),
                authorities,
                teamPositions);
        u.setDivisionName(userDetails.getDivisionNames().get(division));
        u.setTitle(userDetails.getTitle());
        u.setServiceSection(userDetails.getServiceSection());
        u.setLanguage(userDetails.getLanguage());
        u.setDTime(userDetails.getDTime());
        u.setVPID(userDetails.getVPID());
        return u;
    }

    private void validateHmpVersion(JsonNode json, RpcHost host, String vistaId, String division) {
        String webHmpVersion = environment.getProperty(HmpProperties.VERSION);
        String vistaHmpVersion = json.get("hmpVersion").asText();
        if (!webHmpVersion.equalsIgnoreCase(vistaHmpVersion)) {
            HmpVersionMismatchException e = new HmpVersionMismatchException(webHmpVersion, vistaHmpVersion, host, vistaId, division);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private void addOrderingRoleAuthority(Set<GrantedAuthority> authorities, JsonNode json) {
        authorities.add(OrderingRole.fromInt(json.get("orderingRole").asInt()));
    }

    private Set<TeamPosition> getTeamPositions(JsonNode json) {
        Set<TeamPosition> teamPositions = new HashSet<TeamPosition>();
        JsonNode positionsNode = json.get("vistaPositions");
        for (JsonNode positionNode : positionsNode) {
            TeamPosition teamPosition = new TeamPosition(positionNode.get("position").asText(),
                    positionNode.get("teamName").asText(),
                    positionNode.get("teamPhone").asText(),
                    FileManDateTimeFormat.parse(positionNode.get("effectiveDate").asText()),
                    FileManDateTimeFormat.parse(positionNode.get("inactiveDate").asText())
            );
            if (StringUtils.hasText(teamPosition.getPosition())) {
                teamPositions.add(teamPosition);
            }
        }
        return teamPositions;
    }

    private void addVistaUserClassAuthorities(Set<GrantedAuthority> authorities, JsonNode json) {
        JsonNode userClasses = json.get("vistaUserClass");
        if (userClasses != null) {
            for (JsonNode userClassNode : userClasses) {
                VistaUserClass userClass = new VistaUserClass(userClassNode.get("role").asText(),
                        FileManDateTimeFormat.parse(userClassNode.get("effectiveDate").asText()),
                        FileManDateTimeFormat.parse(userClassNode.get("expirationDate").asText()));

                authorities.add(userClass);
            }
        }
    }

    private void addVistaKeyAuthorities(Set<GrantedAuthority> authorities, JsonNode json) {
        JsonNode vistaKeysNode = json.get("vistaKeys");
        if (vistaKeysNode == null) return;
        Iterator<String> vistaKeys = vistaKeysNode.fieldNames();
        if (vistaKeys == null) return;
        while (vistaKeys.hasNext()) {
            String key = vistaKeys.next();
            authorities.add(new VistaSecurityKey(key));
        }
    }

    private JsonNode fetchUserInfo(RpcHost host, ConnectionUserDetails userDetails) {
        Map rpcParams = new HashMap();
        rpcParams.put("command", "getUserInfo");
        rpcParams.put("userId", userDetails.getDUZ());
        return getRpcTemplate().executeForJson(host, userDetails.getDivision(), userDetails.getAccessCode(), userDetails.getVerifyCode(), UserInterfaceRpcConstants.VPR_UI_CONTEXT, UserInterfaceRpcConstants.FRONT_CONTROLLER_RPC, rpcParams);
    }

    public void logout(VistaUserDetails user) throws DataAccessException {
        // NOOP
    }
}
