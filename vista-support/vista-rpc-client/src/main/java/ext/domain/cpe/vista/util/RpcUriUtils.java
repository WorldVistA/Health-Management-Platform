package org.osehra.cpe.vista.util;

import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.RpcRequest;
import org.osehra.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.AnonymousConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.ChangeVerifyCodeConnectionSpec;
import org.osehra.cpe.vista.rpc.conn.ConnectionSpec;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Utility methods for resolving information about VistA RPCs to a URI string.
 * Mainly for internal use within the framework.
 * <p/>
 * VistA RPC URIs are of the form:
 * <code>[vrpcb|vlink]://[{division}:][{accessCode};{verifyCode}@]{host}[:port]/[{rpcContext}]/{rpcName}</code>
 * If a user's verify code has expired, the new verify code can be included like so:
 * <code>[vrpcb|vlink]://[{division}:][{accessCode};{verifyCode}[;{newVerifyCode};{confirmNewVerifyCode}]@]{host}[:port]/[{rpcContext}]/{rpcName}</code>
 *
 * @see java.net.URI
 */
public class RpcUriUtils {

    public static final String VISTA_RPC_BROKER_SCHEME = "vrpcb";
    public static final String VISTALINK_SCHEME = "vlink";
    public static final int DEFAULT_PORT = 9200;
    public static final String DIVISION_CREDENTIALS_DELIMITER = ":";
    public static final String ACCESS_VERIFY_CODE_DELIMITER = ";";
    
	static final String PROTOCOL_TOKEN ="://";
	static final String HOST_TOKEN ="@";


    public static void isVistaRpcUri(URI uri) throws IllegalArgumentException {
        Assert.isTrue(VISTA_RPC_BROKER_SCHEME.equals(uri.getScheme()), "[Assertion failed] - the uri scheme must be '" + VISTA_RPC_BROKER_SCHEME + "', was '" + uri.getScheme() + "'");
    }

    public static void isVistaLinkUri(URI uri) throws IllegalArgumentException {
        Assert.isTrue(VISTALINK_SCHEME.equals(uri.getScheme()), "[Assertion failed] - the uri scheme must be '" + VISTALINK_SCHEME + "', was '" + uri.getScheme() + "'");
    }

    public static URI toURI(RpcRequest request) {
    	/* Do not pass credentials to URI - different rules apply to validation of credentials and uri itself.
    	 * We don't want user name and password to be encoded*/
        //return toURI(request.getHost(), request.getCredentials(), request.getRpcContext(), request.getRpcName(), request.getParams(), request.getTimeout());
        return toURI(request.getHost(), request.getRpcContext(), request.getRpcName(), request.getParams(), request.getTimeout());
    }

    public static URI toURI(RpcHost host,  String rpcContext, String rpcName, List params, int timeout) {
        try {
            if (host == null) {
                return new URI(null, null, toPath(rpcContext, rpcName), toQuery(params, timeout), null);
            } else {
                return new URI(host.getScheme(), null, host.getHostname(), host.getPort(), toPath(rpcContext, rpcName), toQuery(params, timeout), null);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

//    public static URI toURI(RpcHost host, ConnectionSpec auth) {
//        try {
//            return new URI(host.getScheme(), toCredentials(auth), host.getHostname(), host.getPort(), null, null, null);
//        } catch (URISyntaxException e) {
//            throw new IllegalArgumentException(e);
//        }
//    }
    /*
     * Because we allow special characters like '%' in user credentials, we can't always create URI object.
     * This is a workaround.
     */
    public static String toURIString(RpcHost host, ConnectionSpec auth) {
    		return host.getScheme() + "://"+ toCredentials(auth) + "@"+ host.getHostname() + ":" + host.getPort();
    }

    public static String toAuthority(String vistaId, String division, String accessCode, String verifyCode) {
        return toCredentials(division, accessCode, verifyCode) + "@" + vistaId;
    }

    public static String toAuthority(String vistaId, String division, String accessCode, String verifyCode, String newVerifyCode, String confirmNewVerifyCode) {
        return toCredentials(division, accessCode, verifyCode, newVerifyCode, confirmNewVerifyCode) + "@" + vistaId;
    }


    public static String toAuthority(RpcHost host, String division, String accessCode, String verifyCode) {
        return toCredentials(division, accessCode, verifyCode) + "@" + host.toHostString();
    }

    public static String toAuthority(RpcHost host, String credentials) {
        return credentials + "@" + host.toHostString();
    }

    public static String toPath(String rpcContext, String rpcName) {
        if (!StringUtils.hasText(rpcName) && !StringUtils.hasText(rpcContext)) return null;

        StringBuilder sb = new StringBuilder("/");
        if (StringUtils.hasText(rpcContext)) {
            sb.append(rpcContext);
            sb.append('/');
        }
        sb.append(rpcName);
        return sb.toString();
    }

    public static String toQuery(RpcRequest request) {
        if (request == null) return null;
        return toQuery(request.getParams(), request.getTimeout());
    }

    public static String toQuery(List params, int timeout) {
        if (params == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append("[");
            sb.append(i + 1);
            sb.append("]=");
            sb.append(params.get(i).toString());
            sb.append("&");
        }
        sb.append("timeout=");
        sb.append(timeout);
        return sb.toString();
    }

    public static String toCredentials(ConnectionSpec auth) {
        if (auth instanceof ChangeVerifyCodeConnectionSpec) {
            ChangeVerifyCodeConnectionSpec cvc = (ChangeVerifyCodeConnectionSpec) auth;
            return toCredentials(cvc.getDivision(), cvc.getAccessCode(), cvc.getVerifyCode(), cvc.getNewVerifyCode(), cvc.getConfirmNewVerifyCode());
        } else if (auth instanceof AccessVerifyConnectionSpec) {
            AccessVerifyConnectionSpec av = (AccessVerifyConnectionSpec) auth;
            return toCredentials(av.getDivision(), av.getAccessCode(), av.getVerifyCode());
        } else if (auth instanceof AnonymousConnectionSpec) {
            return AnonymousConnectionSpec.ANONYMOUS;
        }
        return null;
    }

    public static String toCredentials(String division, String accessCode, String verifyCode) {
        return toCredentials(division, accessCode, verifyCode, null, null);
    }

    public static String toCredentials(String division, String accessCode, String verifyCode, String newVerifyCode, String confirmNewVerifyCode) {
        if (StringUtils.hasText(division)) {
            String password = toCredentials(accessCode, verifyCode, newVerifyCode, confirmNewVerifyCode);
            return division + (StringUtils.hasText(password) ? DIVISION_CREDENTIALS_DELIMITER + password : "");
        } else {
            return toCredentials(accessCode, verifyCode, newVerifyCode, confirmNewVerifyCode);
        }
    }

    private static String toCredentials(String accessCode, String verifyCode, String newVerifyCode, String confirmNewVerifyCode) {
        if (accessCode == null || verifyCode == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(accessCode);
        sb.append(ACCESS_VERIFY_CODE_DELIMITER);
        sb.append(verifyCode);
        if (StringUtils.hasText(newVerifyCode) && StringUtils.hasText(confirmNewVerifyCode)) {
            sb.append(ACCESS_VERIFY_CODE_DELIMITER);
            sb.append(newVerifyCode);
            sb.append(ACCESS_VERIFY_CODE_DELIMITER);
            sb.append(confirmNewVerifyCode);
        }
        return sb.toString();
    }

    public static String extractUserInfo(String uri) {
//        String userInfo = toURI(uri).getUserInfo();
    	ConnectionSpec auth = extractConnectionSpec(uri);
        return (auth !=null)?auth.toString():null;
    }

    public static String extractDivision(String uri) {
        return extractDivision(toURI(uri));
    }

    public static String extractDivision(URI uri) {
        if (!StringUtils.hasText(uri.getUserInfo())) throw new IllegalArgumentException("expected userInfo in URI");
        String userInfo = uri.getUserInfo();
        String division = userInfo; // URLDecoder.decode(userInfo, "UTF8");
        int colon = division.indexOf(DIVISION_CREDENTIALS_DELIMITER);
        if (colon == -1) return null;
        division = division.substring(0, colon);
        return division;
    }

    public static String extractAccessCode(String uri) {
        return extractAccessCode(toURI(uri));
    }

    public static String extractVerifyCode(String uri) {
        return extractVerifyCode(toURI(uri));
    }

    public static String extractCredentials(String uri) {
        return extractCredentials(toURI(uri));
    }

    public static String extractCredentials(URI uri) {
        try {
            return extractAccessCode(uri) + ACCESS_VERIFY_CODE_DELIMITER + extractVerifyCode(uri);
        } catch (IllegalArgumentException e) {
            if (!StringUtils.hasText(uri.getUserInfo())) throw new IllegalArgumentException("expected userInfo in URI");
            String userInfo = uri.getUserInfo();
            int colon = userInfo.indexOf(DIVISION_CREDENTIALS_DELIMITER);
            userInfo = colon == -1 ? userInfo : userInfo.substring(colon + 1);
            return userInfo;
        }
    }

    public static String extractAccessCode(URI uri) {
        try {
            if (!StringUtils.hasText(uri.getUserInfo())) throw new IllegalArgumentException("expected userInfo in URI");
            String accessCode = URLDecoder.decode(uri.getUserInfo(), "UTF8");
            int colon = accessCode.indexOf(DIVISION_CREDENTIALS_DELIMITER);
            int semicolon = accessCode.indexOf(ACCESS_VERIFY_CODE_DELIMITER);
            if (semicolon == -1)
                throw new IllegalArgumentException("expected '" + ACCESS_VERIFY_CODE_DELIMITER + "' in userInfo of URI");
            accessCode = colon == -1 ? accessCode.substring(0, semicolon) : accessCode.substring(colon + 1, semicolon);
            return accessCode;
        } catch (UnsupportedEncodingException e) {
            // NOOP - should never happen (UTF8 built into JVM)
            return null;
        }
    }

    public static String extractVerifyCode(URI uri) {
        try {
            if (!StringUtils.hasText(uri.getUserInfo())) throw new IllegalArgumentException("expected userInfo in URI");
            String verifyCode = URLDecoder.decode(uri.getUserInfo(), "UTF8");
            int semicolon = verifyCode.indexOf(ACCESS_VERIFY_CODE_DELIMITER);
            if (semicolon == -1)
                throw new IllegalArgumentException("expected '" + ACCESS_VERIFY_CODE_DELIMITER + "' in userInfo of URI");
            verifyCode = verifyCode.substring(semicolon + 1);
            return verifyCode;
        } catch (UnsupportedEncodingException e) {
            // NOOP - should never happen (UTF8 built into JVM)
            return null;
        }
    }

    public static String extractRpcContext(URI uri) {
        String[] path = getPath(uri);
        if (path.length <= 1) return null;
        String rpcContext = path[path.length - 2];
        if (!StringUtils.hasText(rpcContext)) return null;
        return rpcContext;
    }

    public static String extractRpcName(URI uri) {
        String[] path = getPath(uri);
        if (path.length == 0) return null;
        String rpcName = path[path.length - 1];
        if (!StringUtils.hasText(rpcName)) return null;
        return rpcName;
    }

    public static String extractScheme(String uri) {
        try {
            return extractScheme(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String extractScheme(URI uri) {
        return uri.getScheme();
    }

    public static String extractHostname(String uri) {
        try {
            return extractHostname(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String extractHostname(URI uri) {
        return uri.getHost();
    }

    public static int extractPort(String uri) {
        try {
            return extractPort(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static int extractPort(URI uri) {
        return uri.getPort() == -1 ? DEFAULT_PORT : uri.getPort();
    }

    public static RpcHost extractHost(String uri) {
        return extractHost(toURI(uri));
    }

    public static RpcHost extractHost(URI uri) {
        if (!StringUtils.hasText(uri.getHost())) return null;
        return new RpcHost(uri.getHost(), uri.getPort(), uri.getScheme());
    }
//
//    public static AccessVerifyConnectionSpec extractAccessVerifyConnectionSpec(URI uri) {
//        return AccessVerifyConnectionSpec.create(uri.getUserInfo());//new AccessVerifyConnectionSpec(extractDivision(uri), extractAccessCode(uri), extractVerifyCode(uri));
//    }

    public static String extractRpcContext(String uri) {
        return extractRpcContext(toURI(uri));
    }

    public static String extractRpcName(String uri) {
        return extractRpcName(toURI(uri));
    }

    private static String[] getPath(URI uri) {
        String path = uri.getPath();
        if (!StringUtils.hasText(path)) return new String[0];
        if (path.startsWith("/"))
            path = path.substring(1);
        return path.split("/");
    }

//    public static String sanitize(URI uri) {
//        try {
//            String userInfo = uri.getUserInfo();
//            if (StringUtils.hasText(userInfo)) {
//                String division = extractDivision(uri);
//                String password = extractCredentials(uri);
//                password = DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
//                if (StringUtils.hasText(division)) {
//                    userInfo = division + DIVISION_CREDENTIALS_DELIMITER + password;
//                } else {
//                    userInfo = password;
//                }
//            }
//            URI sanitizedUri = new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
//            return sanitizedUri.toString();
//        } catch (URISyntaxException e) {
//            throw new IllegalArgumentException(e);
//        } catch (UnsupportedEncodingException e) {
//            // NOOP: should never happen (UTF-8 built into JVM)
//            return null;
//        }
//    }
    
    public static String sanitize(URI uri, ConnectionSpec auth) {
        try {
        	String userInfo = "";
            if (StringUtils.hasText(auth.toString())) {
                if(auth instanceof AccessVerifyConnectionSpec){
                	String password = ((AccessVerifyConnectionSpec)auth).getCredentials();
                	password = DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
                	String division = ((AccessVerifyConnectionSpec) auth).getDivision();
                	if (StringUtils.hasText(division)) {
                		userInfo = division + DIVISION_CREDENTIALS_DELIMITER + password;
                	} else {
                		userInfo = password;
                	}
                }
            }
            URI sanitizedUri = new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
            return sanitizedUri.toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        } catch (UnsupportedEncodingException e) {
            // NOOP: should never happen (UTF-8 built into JVM)
            return null;
        }
    }


    public static URI toURI(String location) {
        try {
            int ssp = location.indexOf("://");
            int at = location.lastIndexOf("@");
            if (ssp != -1 && at != -1) {
                String userInfo = location.substring(ssp + 3, at);
                int slash = location.indexOf("/", at);
                String hostAndPort = (slash != -1 ? location.substring(at + 1, slash) : location.substring(at + 1));
                int colon = hostAndPort.indexOf(":");
                String host = hostAndPort;
                String port = null;
                if (colon != -1) {
                    host = hostAndPort.substring(0, colon);
                    port = hostAndPort.substring(colon + 1);
                }
                String path = (slash != -1 ? location.substring(slash) : null);
                UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                        .scheme(location.substring(0, ssp))
                        .userInfo(userInfo)
                        .host(host);
                if (StringUtils.hasText(port)) {
                    builder.port(Integer.parseInt(port));
                }
                builder.path(path);
                UriComponents uri = builder.build();
                return uri.toUri();
            } else {
                return ResourceUtils.toURI(location);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static URI toURI(String uri, List params) {
        return new RpcRequest(uri, params).getURI();
    }
    
    public static URI toSafeURI(String uriString) {
    	String uri = null;
    	ConnectionSpec auth = extractConnectionSpec(uriString);
    	
        try {
        	uri = (auth != null)?uriString.replace(auth.toString(),""):uriString;
			return ResourceUtils.toURI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
    }
       
	public static ConnectionSpec extractConnectionSpec(String uriString) {
		int startIndex = uriString.toString().indexOf(PROTOCOL_TOKEN) + PROTOCOL_TOKEN.length();
		int endIndex = uriString.toString().lastIndexOf(HOST_TOKEN);
		ConnectionSpec auth = null;
		try {
			auth = AccessVerifyConnectionSpec.create(uriString.toString().substring(startIndex, endIndex));
		}catch (IllegalArgumentException e) {
			auth = new AnonymousConnectionSpec();
		}catch (Exception e) {
			auth = null;
		}
		
		return auth;
	}

}
