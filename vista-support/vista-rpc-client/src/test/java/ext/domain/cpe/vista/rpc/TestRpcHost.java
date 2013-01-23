package org.osehra.cpe.vista.rpc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRpcHost {

    private RpcHost host;

    @Before
    public void setUp() throws Exception {
        host = new RpcHost("vhaislbll2.vha.DOMAIN.EXT", 9060);
    }

    @Test
    public void testToURI() {
        assertEquals("vrpcb://vhaislbll2.vha.DOMAIN.EXT:9060", host.toURI());
    }

    @Test
    public void testToHostString() {
        assertEquals("vhaislbll2.vha.DOMAIN.EXT:9060", host.toHostString());
    }

    @Test
    public void testEquals() {
        assertTrue(host.equals(new RpcHost("VHAISLBLL2.VHA.DOMAIN.EXT", 9060)));
        assertFalse(host.equals(new RpcHost("VHAISLBLL22.VHA.DOMAIN.EXT", 9060))); // different hostname
        assertFalse(host.equals(new RpcHost("VHAISLBLL2.VHA.DOMAIN.EXT", 9063))); // different port
    }
}
