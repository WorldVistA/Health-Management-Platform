package EXT.DOMAIN.cpe.param;

import static EXT.DOMAIN.cpe.vpr.UserInterfaceRpcConstants.CONTROLLER_RPC_URI;

import com.fasterxml.jackson.databind.JsonNode;
import EXT.DOMAIN.cpe.auth.HmpUserDetails;
import EXT.DOMAIN.cpe.auth.UserContext;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponseExtractionException;
import EXT.DOMAIN.cpe.vista.util.VistaStringUtils;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.vistasvc.CacheMgr;
import EXT.DOMAIN.cpe.vpr.vistasvc.CacheMgr.CacheType;
import EXT.DOMAIN.cpe.vpr.vistasvc.ICacheMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is a general purpose interface to fetch/store/retrive user parameters to/from VISTA.
 * 
 * The values are cached so you should be able to fetch/store values efficiently w/o excessive RPC traffic.
 * 
 * Currently only supports user-specific parameters.
 * 
 * Has some helpful functionality for dealing with parameters stored as key/value pairs in JSON, but does
 * not force you to store all parameters as JSON.
 * 
 * TODO: finish the system-level parameters.
 * TODO: mash system and user level parameters togeather? (if a user value doesn't exist, then default to system level)?
 * TODO: support looking up values for user other than the current user.
 * 
 * @author Brian Bray
 */
@Service
public class ParamService {
	private static final String ENTITY_USR = "USR";
	private static final String ENTITY_SYS = "SYS";

	@Autowired
	protected RpcOperations rpcTemplate;

	@Autowired
	protected UserContext userContext;

	protected ICacheMgr<String> cache = new CacheMgr<String>("ParamCache", CacheType.SESSION_MEMORY);

	// Get/fetch functions ----------------------------------------------------

	/**
	 * Same as getUserParam(id, null);
	 */
	public String getUserParam(String id) {
		return getUserParam(id, null);
	}

	public Object getUserParamVal(String id, String key) {
		return getUserParamVal(id, key, null);
	}

	/**
	 * Returns a specific key from the specified user parameter.  Assumes the parameter is stored as a JSON document.
	 * 
	 * @param id The name of the parameter to fetch
	 * @param key The name of the parameter key to return
	 * @param instance The instance of the parameter, may be null for default (0)
	 * @return Returns the key's value, or null if the param or key doesn't exist.
	 */
	public Object getUserParamVal(String id, String key, String instance) {
		Map<String, ?> map = getUserParamMap(id, instance);
		if (map == null || !map.containsKey(key)) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * Returns the user parameter.  Assumes its stored as a JSON document.
	 * 
	 * @param id The name of the parameter to fetch
	 * @param instance The instance of the parameter, may be null for default (0)
	 * @return Returns a map of all the parameters values, or null if it doesn't exist.
	 */
	public Map<String, Object> getUserParamMap(String id, String instance) {
		String val = getUserParam(id, instance);
		if (val == null || val.length() == 0) {
			return null;
		}
		return POMUtils.parseJSONtoMap(val);
	}

	/**
	 * Returns the user parameter.  Assumes nothing about what format the parameter is in (JSON, XML, etc.)
	 * 
	 * @param id The name of the parameter to fetch
	 * @param instance The instance of the parameter, may be null for default (0)
	 * @return Returns a string of the parameters value, or null if it doesn't exist.
	 */
	public String getUserParam(String id, String instance) {
		if (userContext.getCurrentUser() == null) {
			// anonymous user, return null
			return null;
		}


		String uid = getUid(id, instance, ENTITY_USR);
		String val = cache.fetch(uid);
		if (val == null) {
			Map<String, String> params = new HashMap<String,String>();
			params.put("command", "getParam");
			params.put("uid", uid);
			val = rpcTemplate.executeForString(CONTROLLER_RPC_URI, params);
			if (val != null && val.length() > 0) {
				cache.store(uid, val);
			} else {
				val = null;
			}
		}
		return val;
	}

	public String getSystemParam(String id, String instance) {
		throw new UnsupportedOperationException();
	}

	// set/Store functions ----------------------------------------------------

	/**
	 * Sets the value of the user parameter, replaces any existing value.
	 * 
	 * @param id The name of the parameter to set
	 * @param inst The name of the instance to update, if null then updates the default instance(0)
	 * @param value The value to set.
	 */
	public void setUserParam(String id, String inst, String value) {
		String uid = getUid(id, inst, ENTITY_USR);

		Map<String, Object> params = new HashMap<String,Object>();
		params.put("command", "saveByUid");
		params.put("uid", uid);
		params.put("value", VistaStringUtils.splitLargeStringIfNecessary(value));
		
		rpcTemplate.executeForString(CONTROLLER_RPC_URI, params);
		cache.store(uid, value);
	}

	public void setUserParamVal(String id, String inst, String key, Object val) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(key, val);
		setUserParamVals(id, inst, map);
	}

	/**
	 * Sets/merges/updates the parameter with the key/values specified.  Assumes that the parameter is a JSON document.
	 * 
	 * If this is the first time a value is set in the specified param, it will be created.
	 * 
	 * @param id The name of the parameter to set
	 * @param inst The name of the instance to update, if null then updates the default instance(0)
	 * @param vals Updates/replaces the existing values with these values
	 */
	public void setUserParamVals(String id, String inst, Map<String,Object> vals) {
		Map<String, Object> map = getUserParamMap(id, inst);
		if (map == null) {
			map = vals;
		} else {
			map.putAll(vals);
		}
		setUserParam(id, inst, POMUtils.toJSON(map));
	}

	public void setSystemParam(String id, String inst, String value) {
		throw new UnsupportedOperationException();
	}

	// misc functions ---------------------------------------------------------

	public String getUserUID(String id, String inst) {
		return getUid(id, inst, ENTITY_USR);
	}

	public String getSystemUID(String id, String inst) {
		return getUid(id, inst, ENTITY_SYS);
	}

	/**
	 * URN format for user param is "urn:va:param:{vistaID}:{user DUZ}:{PARAM NAME}:{instance ID}
	 * URN format for system param is "urn:va:param:{vistaID}:SYS:{PARAM NAME}:{instance ID}
	 * 	
	 * Instance ID defaults to 0 if not specified, entity defaults to user if not specified
	 */
	protected String getUid(String paramId, String inst, String entity) {
		HmpUserDetails user = userContext.getCurrentUser();
		String vistaId = (user != null) ? user.getVistaId() : null;
		String instance = (inst == null) ? "0" : inst;
		if (entity == null || entity.equals(ENTITY_USR)) {
			entity = (user != null) ? user.getDUZ() : null;
		} else if (!entity.equals(ENTITY_SYS)) {
			throw new IllegalArgumentException("Unrecognized parameter entity: " + entity);
		}
		return "urn:va:param:" + vistaId + ':' + entity + ':' + paramId + ':' + instance;
	}

	/**
	 * Returns just a list of the unique param ID's (not the full URN) for this user.
	 */
	public String[] getUserParamIDs() {
		List<String> all = listUserParams();
		Set<String> ret = new HashSet<String>();
		for (String s : all) {
			String[] parts = s.split(":");
			ret.add(parts[5]);
		}
		return ret.toArray(new String[0]);
	}

	/**
	 * Returns a list of all the unique instance ID's (not the full URN) for a 
	 * specific parameter ID for the current user.
	 */
	public List<String> getUserParamInstanceIDs(String id) {
		List<String> all = listUserParams();
		List<String> ret = new ArrayList<String>();
		for (String uid : all) {
			if (uid.contains(":" + id + ":")) {
				String[] parts = uid.split(":");
				ret.add(parts[6]);
			}
		}
		return ret;
	}

	/**
	 * Lists all the URN's of all the users params and param instances
	 * @return
	 */
	public List<String> listUserParams() {
		HmpUserDetails user = userContext.getCurrentUser();
		String duz = (user != null) ? user.getDUZ() : null;

		Map<String, Object> params = new HashMap<String,Object>();
		params.put("command", "getAllParam");
		params.put("entity", "USR");
		params.put("entityId", duz);
		params.put("getValues", true);
		// TODO: cache this
        ArrayList<String> ret = new ArrayList<String>();
        try {
            JsonNode data = rpcTemplate.executeForJson(CONTROLLER_RPC_URI, params);
            data = data.get("params");
           for (int i = 0; i < data.size(); i++) {
                JsonNode val = data.get(i);
                ret.add(val.get("uid").asText());
            }
        } catch (RpcResponseExtractionException e) { // params were probably blank, so JSON couldn't be constructed
            // NOOP
        }
		return ret;
	}

	/**
	 * Clears/deletes the specified user parameter including all of its instances 
	 * 
	 * @param id The name of the user param to delete.
	 */
	public void clearUserParam(String id, String inst) {
		String uid = getUid(id, inst, ENTITY_USR);
		cache.remove(uid);
		Map<String, String> params = new HashMap<String,String>();
		params.put("command", "clearParam");
		params.put("uid", uid);
		rpcTemplate.executeForString(CONTROLLER_RPC_URI, params);
	}
}
