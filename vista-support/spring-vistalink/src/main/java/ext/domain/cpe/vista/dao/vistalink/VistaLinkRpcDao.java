package EXT.DOMAIN.cpe.vista.dao.vistalink;

import EXT.DOMAIN.cpe.vistalink.VistaLinkDaoSupport;
import EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails.VistaUserCache;
import EXT.DOMAIN.vistalink.rpc.RpcRequest;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * TODO: Provide summary documentation of class VistaLinkRpcDao
 */
@Repository
public class VistaLinkRpcDao extends VistaLinkDaoSupport /*implements RpcOperations*/ {

    private VistaUserCache userCache;

    @Required
    public void setUserCache(VistaUserCache userCache) {
        this.userCache = userCache;
    }

//    public String executeRpc(String division, String accessCode, String verifyCode, String rpcContext, String rpcName, Object... params) throws DataAccessException {
//        VistaUserDetails user = (VistaUserDetails) userCache.getUserFromCache(accessCode, verifyCode);
//        if (user == null) {
//            try {
//                user = (VistaUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            } catch (NullPointerException e) {
//                throw new DataRetrievalFailureException("couldn't match a/v pair to login", e);
//            }
//        }
//        if (!division.equals(user.getDivision()) || !accessCode.equals(user.getAccessCode()) || !verifyCode.equals(user.getVerifyCode())) {
//            throw new InvalidDataAccessApiUsageException("mismatch between arguments and user details");
//        }
//        return getRpcTemplate().rpcAsUser(division, user.getDUZ(), rpcContext, rpcName, buildRpcParameterList(params));
//    }
//
//    public String executeRpc(String division, String accessCode, String verifyCode, VistaRpcDescriptor rpc, Object... params) throws DataAccessException {
//        return executeRpc(division, accessCode, verifyCode, rpc.getContext(), rpc.getName(), params);
//    }

    private List buildRpcParameterList(Object... params) {
        List paramList = new ArrayList();
        for (Object param : params) {
            if (param instanceof Map)
                paramList.add(modifyMapRpcParameter((Map) param));
            else
                paramList.add(param);
        }
        return paramList;
    }

    public static Map modifyMapRpcParameter(Map params) {
        Map modifiedParams = new HashMap();
        for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if (e.getValue() instanceof List) {
                List l = (List) e.getValue();
                for (int j = 0; j < l.size(); j++) {
                    modifiedParams.put(RpcRequest.buildMultipleMSubscriptKey("\"" + e.getKey() + "\"," + (j + 1)), l.get(j));
                }
            } else {
                modifiedParams.put(e.getKey(), e.getValue());
            }
        }
        return modifiedParams;
    }
}
