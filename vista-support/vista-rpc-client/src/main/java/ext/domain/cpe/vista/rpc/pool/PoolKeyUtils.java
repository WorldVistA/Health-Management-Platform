package EXT.DOMAIN.cpe.vista.rpc.pool;

import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import EXT.DOMAIN.cpe.vista.rpc.conn.AnonymousConnectionSpec;
import EXT.DOMAIN.cpe.vista.rpc.conn.ConnectionSpec;
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils;
import org.springframework.util.ResourceUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class PoolKeyUtils {

    public static String getKey(RpcHost host, String credentials) {
        if (AnonymousConnectionSpec.ANONYMOUS.equalsIgnoreCase(credentials)) {
            return RpcUriUtils.toURIString(host, new AnonymousConnectionSpec());
        } else {
            return RpcUriUtils.toURIString(host, AccessVerifyConnectionSpec.create(credentials));
        }
    }

    public static URI keyToURI(Object key) throws URISyntaxException {
        return RpcUriUtils.toSafeURI((String)key);
    }
   
	public static ConnectionSpec keyToConnectionSpec(Object key) {
		return RpcUriUtils.extractConnectionSpec((String)key);
	}
}
