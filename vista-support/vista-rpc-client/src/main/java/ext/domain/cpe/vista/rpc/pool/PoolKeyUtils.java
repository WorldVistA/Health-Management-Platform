package org.osehra.cpe.vista.rpc.pool;

import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.AnonymousConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.ConnectionSpec;
import org.osehra.cpe.vista.util.RpcUriUtils;
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
