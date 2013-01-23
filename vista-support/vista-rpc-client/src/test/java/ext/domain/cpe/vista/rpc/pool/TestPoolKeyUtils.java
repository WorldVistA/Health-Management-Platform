package org.osehra.cpe.vista.rpc.pool;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.conn.AnonymousConnectionSpec;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

public class TestPoolKeyUtils {

    @Test
    public void testGetKeyWithHostAndAccessVerifyCode() throws Exception {
        assertThat(PoolKeyUtils.getKey(new RpcHost("example.org", 9060), "foo;bar"), equalTo("vrpcb://foo;bar@example.org:9060"));
    }

    @Test
    public void testGetKeyWithHostAndAccessVerifyCodeAndDivision() throws Exception {
        assertThat(PoolKeyUtils.getKey(new RpcHost("example.org", 9060), "960:foo;bar"), equalTo("vrpcb://960:foo;bar@example.org:9060"));
    }

    @Test
    public void testGetKeyWithAccessVerifyCodeAndNewVerifyCode() throws Exception {
        assertThat(PoolKeyUtils.getKey(new RpcHost("example.org", 9060), "foo;bar;baz;baz"), equalTo("vrpcb://foo;bar;baz;baz@example.org:9060"));
    }

    @Test
    public void testGetKeyWithHostAndAccessVerifyCodeAndDivisionAndNewVerifyCode() throws Exception {
        assertThat(PoolKeyUtils.getKey(new RpcHost("example.org", 9060), "960:foo;bar;baz;baz"), equalTo("vrpcb://960:foo;bar;baz;baz@example.org:9060"));
    }

    @Test
    public void testGetKeyWithHostAndAnonymousCredentials() throws Exception {
        assertThat(PoolKeyUtils.getKey(new RpcHost("example.org", 9060), AnonymousConnectionSpec.ANONYMOUS), equalTo("vrpcb://" + AnonymousConnectionSpec.ANONYMOUS + "@example.org:9060"));
    }

    @Test
    public void testKeyToURI() throws Exception {
        assertThat(PoolKeyUtils.keyToURI("vrpcb://foo;bar@example.org:9060"), equalTo(ResourceUtils.toURI("vrpcb://@example.org:9060")));
        assertThat(PoolKeyUtils.keyToConnectionSpec("vrpcb://foo;bar@example.org:9060").toString(), equalTo("foo;bar"));
    }

    @Test
    public void testAnonymousKeyToURI() throws Exception {
        assertThat(PoolKeyUtils.keyToURI("vrpcb://" + AnonymousConnectionSpec.ANONYMOUS + "@example.org:9060").toString(), equalTo("vrpcb://@example.org:9060"));
        assertThat(PoolKeyUtils.keyToConnectionSpec("vrpcb://" + AnonymousConnectionSpec.ANONYMOUS + "@example.org:9060").toString(), equalTo("ANONYMOUS"));
    }

    @Test
    public void testKeyToURIWithSpecialCharacters() throws Exception {
        assertThat(PoolKeyUtils.keyToURI("vrpcb://accesscode1;verifycode1&@example.org:9060"), equalTo(ResourceUtils.toURI("vrpcb://@example.org:9060")));
        assertThat(PoolKeyUtils.keyToConnectionSpec("vrpcb://accesscode1;verifycode1&@example.org:9060").toString(), equalTo("accesscode1;verifycode1&"));

    }
    
    @Test
    public void testKeyToURIWithSpecialCharactersPercent() throws Exception {
    	assertThat(PoolKeyUtils.keyToURI("vrpcb://accesscode1;verifycode%&@example.org:9060"), equalTo(ResourceUtils.toURI("vrpcb://@example.org:9060")));
    	assertThat(PoolKeyUtils.keyToConnectionSpec("vrpcb://accesscode1;verifycode%&@example.org:9060").toString(), equalTo("accesscode1;verifycode%&"));
    	
    }
}
