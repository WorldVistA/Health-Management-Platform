package EXT.DOMAIN.cpe.vista.util;

import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.RpcRequest;
import EXT.DOMAIN.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import EXT.DOMAIN.cpe.vista.rpc.conn.AnonymousConnectionSpec;
import EXT.DOMAIN.cpe.vista.rpc.conn.AppHandleConnectionSpec;

import org.junit.Test;

import java.net.URI;
import java.util.Arrays;

import static EXT.DOMAIN.cpe.vista.util.RpcUriUtils.VISTA_RPC_BROKER_SCHEME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class TestRpcUriUtils {

//    @Test
//    public void testToURIWithHostAndAccessVerifyConnectionSpec() {
//        URI uri = RpcUriUtils.toURI(new RpcHost("vhaislbll2.vha.DOMAIN.EXT", 9060), new AccessVerifyConnectionSpec("960", "foo", "bar"));
//        assertEquals(VISTA_RPC_BROKER_SCHEME + "://960:foo;bar@vhaislbll2.vha.DOMAIN.EXT:9060", uri.toString());
//    }

    @Test
    public void testToCredentials() {
        assertEquals("960:10vehu;vehu10", RpcUriUtils.toCredentials("960", "10vehu", "vehu10"));
        assertEquals("10vehu;vehu10", RpcUriUtils.toCredentials(null, "10vehu", "vehu10"));
        assertNull(RpcUriUtils.toCredentials(null, "10vehu", null));
        assertNull(RpcUriUtils.toCredentials(null, null, "vehu10"));
        assertNull(RpcUriUtils.toCredentials(null, null, null));
    }

    @Test
    public void testToCredentialsWithNewVerifyCode() {
        assertEquals("960:10vehu;vehu10", RpcUriUtils.toCredentials("960", "10vehu", "vehu10", null, null));
        assertEquals("10vehu;vehu10", RpcUriUtils.toCredentials(null, "10vehu", "vehu10", null, null));
        assertEquals("960:10vehu;vehu10;newvehu10;newvehu10", RpcUriUtils.toCredentials("960", "10vehu", "vehu10", "newvehu10", "newvehu10"));
        assertEquals("10vehu;vehu10;newvehu10;newvehu10", RpcUriUtils.toCredentials(null, "10vehu", "vehu10", "newvehu10", "newvehu10"));
        assertEquals("10vehu;vehu10", RpcUriUtils.toCredentials(null, "10vehu", "vehu10", "newvehu10", null));
        assertEquals("10vehu;vehu10", RpcUriUtils.toCredentials(null, "10vehu", "vehu10", null, "newvehu10"));

        assertNull(RpcUriUtils.toCredentials(null, "10vehu", null, null, null));
        assertNull(RpcUriUtils.toCredentials(null, null, "vehu10", null, null));
        assertNull(RpcUriUtils.toCredentials(null, null, null, null, null));
        assertNull(RpcUriUtils.toCredentials(null, "10vehu", null, "newvehu10", "newvehu10"));
        assertNull(RpcUriUtils.toCredentials(null, null, "vehu10", "newvehu10", "newvehu10"));
    }

    @Test
    public void testToAuthority() {
        assertThat(RpcUriUtils.toAuthority("9F2B", "960", "10vehu", "vehu10"), equalTo("960:10vehu;vehu10@9F2B"));
        assertThat(RpcUriUtils.toAuthority("9F2B", "960", "10vehu", "vehu10", "newvehu10", "newvehu10"), equalTo("960:10vehu;vehu10;newvehu10;newvehu10@9F2B"));
        assertThat(RpcUriUtils.toAuthority(new RpcHost("example.org", 9060), "960", "10vehu", "vehu10"), equalTo("960:10vehu;vehu10@example.org:9060"));
        assertThat(RpcUriUtils.toAuthority(new RpcHost("example.org", 9060), "foo;bar"), equalTo("foo;bar@example.org:9060"));
        assertThat(RpcUriUtils.toAuthority(new RpcHost("example.org", 9060), AnonymousConnectionSpec.ANONYMOUS), equalTo(AnonymousConnectionSpec.ANONYMOUS + "@example.org:9060"));
    }

    @Test
    public void testToPath() {
        assertEquals("/FOO/BAR", RpcUriUtils.toPath("FOO", "BAR"));
        assertEquals("/BAR", RpcUriUtils.toPath(null, "BAR"));
        assertNull(RpcUriUtils.toPath(null, null));
    }

    @Test
    public void testToQuery() {
        assertEquals("[1]=baz&[2]=spaz&timeout=30", RpcUriUtils.toQuery(new RpcRequest("FOO/BAR", Arrays.asList(new String[]{"baz", "spaz"}))));
    }

    @Test
    public void testExtractScheme() {
        assertEquals("vrpcb", RpcUriUtils.extractScheme("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("vlink", RpcUriUtils.extractScheme("vlink://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractAccessCode() {
        assertEquals("10vehu", RpcUriUtils.extractAccessCode("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractVerifyCode() {
        assertEquals("vehu10", RpcUriUtils.extractVerifyCode("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractHostname() {
        assertEquals("vhaislbll2.vha.DOMAIN.EXT", RpcUriUtils.extractHostname("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("9F06", RpcUriUtils.extractHostname("vrpcb://9F06/FOO/BAR"));
    }

    @Test
    public void testExtractHost() {
        RpcHost host = RpcUriUtils.extractHost("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR");
        assertEquals("vhaislbll2.vha.DOMAIN.EXT", host.getHostname());
        assertEquals(9060, host.getPort());
        assertEquals("vrpcb", host.getScheme());
        host = RpcUriUtils.extractHost("vrpcb://9F06/FOO/BAR");
        assertEquals("9F06", host.getHostname());
        assertEquals(-1, host.getPort());
        host = RpcUriUtils.extractHost("vrpcb://9F06/FOO BAR/BAZ SPAZ");
        assertEquals("9F06", host.getHostname());
        assertEquals(-1, host.getPort());
    }

    @Test
    public void testExtractPort() {
        assertEquals(9060, RpcUriUtils.extractPort("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/960"));
        assertEquals(RpcUriUtils.DEFAULT_PORT, RpcUriUtils.extractPort("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT"));
    }

    @Test
    public void testExtractDivision() {
        assertEquals("960", RpcUriUtils.extractDivision("vrpcb://960:10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/BAR"));
    }

    @Test
    public void testExtractDivisionWhenCredentialsHasPercentSign() {
        assertEquals("960", RpcUriUtils.extractDivision("vrpcb://960:10vehu;vehu10%@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10%@vhaislbll2.vha.DOMAIN.EXT:9060"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10%@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertNull(RpcUriUtils.extractDivision("vrpcb://10vehu;vehu10%@vhaislbll2.vha.DOMAIN.EXT:9060/BAR"));
    }

    @Test
    public void testExtractDivisionFromURIWhenCredentialsHasPercentSign() {
        assertEquals("960", RpcUriUtils.extractDivision(URI.create("vrpcb://960%3A10vehu%3Bvehu10%25@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR")));
        assertNull(RpcUriUtils.extractDivision(URI.create("vrpcb://10vehu%3Bvehu10%25@vhaislbll2.vha.DOMAIN.EXT:9060")));
        assertNull(RpcUriUtils.extractDivision(URI.create("vrpcb://10vehu%3Bvehu10%25@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR")));
        assertNull(RpcUriUtils.extractDivision(URI.create("vrpcb://10vehu%3Bvehu10%25@vhaislbll2.vha.DOMAIN.EXT:9060/BAR")));
    }

    @Test
    public void testExtractCredentials() {
        assertEquals("10vehu;vehu10", RpcUriUtils.extractCredentials("vrpcb://960:10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("10vehu;vehu10", RpcUriUtils.extractCredentials("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractCredentialsWithAtSignInThem() {
        assertEquals("10@vehu;vehu@10", RpcUriUtils.extractCredentials("vrpcb://10@vehu;vehu@10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractCredentialsWithPercentSignInThem() {
        assertEquals("10%vehu;vehu%10", RpcUriUtils.extractCredentials("vrpcb://10%vehu;vehu%10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractCredentialsWithNewVerifyCodeAndConfirmVerifyCodeInThem() {
        assertEquals("10vehu;vehu10;newvehu10;newvehu10", RpcUriUtils.extractCredentials("vrpcb://960:10vehu;vehu10;newvehu10;newvehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("10vehu;vehu10;newvehu10;newvehu10", RpcUriUtils.extractCredentials("vrpcb://10vehu;vehu10;newvehu10;newvehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractCredentialsWhenCredentialsAreMissing() {
        RpcUriUtils.extractCredentials("vrpcb://vhaislbll2.vha.DOMAIN.EXT:9060/BAR");
    }

    @Test
    public void testExtractUserInfoWithAtSignInThem() {
        assertEquals("10@vehu;vehu@10", RpcUriUtils.extractUserInfo("vrpcb://10@vehu;vehu@10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("960:10@vehu;vehu@10", RpcUriUtils.extractUserInfo("vrpcb://960:10@vehu;vehu@10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractUserInfoWithPercentSignInThem() {
        assertEquals("10%vehu;vehu%10", RpcUriUtils.extractUserInfo("vrpcb://10%vehu;vehu%10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("960:10%vehu;vehu%10", RpcUriUtils.extractUserInfo("vrpcb://960:10%vehu;vehu%10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
    }

    @Test
    public void testExtractRpcContext() {
        assertEquals("FOO", RpcUriUtils.extractRpcContext("vrpcb://960:10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("FOO", RpcUriUtils.extractRpcContext("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("FOO BAR", RpcUriUtils.extractRpcContext("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO BAR/BAZ SPAZ"));
        assertNull(RpcUriUtils.extractRpcContext("vrpcb://960:10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060//BAR"));
        assertNull(RpcUriUtils.extractRpcContext("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/BAR"));
        assertNull(RpcUriUtils.extractRpcContext("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060//BAR"));
    }

    @Test
    public void testExtractRpcName() {
        assertEquals("BAR", RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/960/FOO/BAR"));
        assertEquals("BAR", RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR"));
        assertEquals("BAZ SPAZ", RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO BAR/BAZ SPAZ"));
        assertEquals("FOO", RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO"));
        assertEquals("FOO BAR", RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO BAR"));
        assertNull(RpcUriUtils.extractRpcName("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060"));
    }

    @Test
    public void testSanitize() {
//        String uri = RpcUriUtils.sanitize(URI.create("vrpcb://960:10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//        assertTrue(uri.startsWith("vrpcb://960:"));
//        assertTrue(uri.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
        
        String uri1 = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"), AccessVerifyConnectionSpec.create("960:10vehu;vehu10"));
        assertTrue(uri1.startsWith("vrpcb://960:"));
        assertTrue(uri1.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//        assertEquals(uri,uri1);

    }

    @Test
    public void testSanitizeNoDivision() {
//        String uri = RpcUriUtils.sanitize(URI.create("vrpcb://10vehu;vehu10@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//        assertTrue(uri.startsWith("vrpcb://"));
//        assertTrue(uri.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
        
        String uri1 = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"), AccessVerifyConnectionSpec.create("10vehu;vehu10"));
        assertTrue(uri1.startsWith("vrpcb://"));
        assertTrue(uri1.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));

//        assertEquals(uri,uri1);
    }
    
    @Test
    public void testSanitizeNoCredentials() {
//    	String uri = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//    	assertTrue(uri.startsWith("vrpcb://"));
//    	assertTrue(uri.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
    	
    	String uri1 = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"), new AnonymousConnectionSpec());
    	System.out.println(uri1);
    	assertTrue(uri1.startsWith("vrpcb://"));
    	assertTrue(uri1.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
    	
    }

    @Test
    public void testSanitizeWithPercentSignInCredentials() {
//        String uri = RpcUriUtils.sanitize(URI.create("vrpcb://960%3A10vehu%3Bvehu10%25@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//        assertTrue(uri.startsWith("vrpcb://960:"));
//        assertTrue(uri.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
        
        // So here is what we are trying to fix - special character '%' in credentials.
        // For example password such us 'vehu10%#' will cause a URI Malformed exception and so will not be encoded.
        try{
        	new URI("vrpcb://960/vehu10,vehu10%#@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ");
        	assertFalse(true);// should never get here
        }catch (Exception e){
        	assertEquals("Malformed escape pair at index 25: vrpcb://960/vehu10,vehu10%#@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ",e.getMessage());
        }
        
        String uri1 = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"), AccessVerifyConnectionSpec.create("960/10vehu;vehu10%#"));
        assertTrue(uri1.startsWith("vrpcb://"));
        assertTrue(uri1.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));        

    }

    @Test
    public void testSanitizeWithPercentSignInCredentialsAndNoDivision() {
//        String uri = RpcUriUtils.sanitize(URI.create("vrpcb://10vehu@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
//        assertTrue(uri.startsWith("vrpcb://"));
//        assertTrue(uri.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));
        
        // So here is what we are trying to fix - special character '%' in credentials.
        // For example password such us 'vehu10%#' will cause a URI Malformed exception and so will not be encoded.
        try{
        	new URI("vrpcb://vehu10,vehu10%#@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ");
        	assertFalse(true);// should never get here
        }catch (Exception e){
        	assertEquals("Malformed escape pair at index 21: vrpcb://vehu10,vehu10%#@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ",e.getMessage());
        }

        String uri1 = RpcUriUtils.sanitize(URI.create("vrpcb://@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"), AccessVerifyConnectionSpec.create("960%3A10vehu%3;Bvehu10%25"));
        assertTrue(uri1.startsWith("vrpcb://"));
        assertTrue(uri1.endsWith("@vhaislbll2.vha.DOMAIN.EXT:9060/FOO/BAR%20BAZ"));        

    }

    @Test
    public void testToURIurlEncodesSpaces() {
        URI uri = RpcUriUtils.toURI("vrpcb://example.org:1234/FOO BAR/BAZ SPAZ");
        assertEquals("vrpcb://example.org:1234/FOO%20BAR/BAZ%20SPAZ", uri.toString());
    }

    @Test
    public void testToURIWithCredentialsContainingAtSigns() {
        URI uri = RpcUriUtils.toURI("vrpcb://10@vehu;vehu@10@example.org:1234/FOO BAR/BAZ SPAZ");
        assertEquals("vrpcb://10%40vehu;vehu%4010@example.org:1234/FOO%20BAR/BAZ%20SPAZ", uri.toString());
    }
}
