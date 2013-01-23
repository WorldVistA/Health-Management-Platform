package org.osehra.cpe.vista.rpc.conn;

import org.junit.Test;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class TestAccessVerifyConnectionSpec {
    @Test
    public void testEquals() {
        AccessVerifyConnectionSpec av1 = new AccessVerifyConnectionSpec("960", "foo", "bar");
        AccessVerifyConnectionSpec av2 = new AccessVerifyConnectionSpec("960", "foo", "bar");
        AccessVerifyConnectionSpec av3 = new AccessVerifyConnectionSpec("960", "foo", "baz");
        AccessVerifyConnectionSpec av4 = new AccessVerifyConnectionSpec("960", "bar", "foo");

        assertTrue(av1.equals(av1));

        assertTrue(av2.equals(av1));
        assertTrue(av1.equals(av2));

        assertFalse(av3.equals(av1));
        assertFalse(av1.equals(av3));

        assertFalse(av4.equals(av1));
        assertFalse(av1.equals(av4));
    }

    @Test
    public void testToString() {
        AccessVerifyConnectionSpec av = new AccessVerifyConnectionSpec("960", "foo", "bar");
        assertEquals("960:foo;bar", av.toString());
    }

    @Test
    public void testChangeVerifyCodeToString() {
        ChangeVerifyCodeConnectionSpec cvc = new ChangeVerifyCodeConnectionSpec("960", "foo", "bar", "baz", "baz");
        assertEquals("960:foo;bar;baz;baz", cvc.toString());
    }

    @Test
    public void testToStringNoDivision() {
        AccessVerifyConnectionSpec av = new AccessVerifyConnectionSpec(null, "foo", "bar");
        assertEquals("foo;bar", av.toString());
    }

    @Test
    public void testGetCredentials() {
        AccessVerifyConnectionSpec av = new AccessVerifyConnectionSpec("960", "foo", "bar");
        assertEquals("foo;bar", av.getCredentials());
    }

    @Test
    public void testCreate() {
        AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create("960:foo;bar");
        assertEquals("960", av.getDivision());
        assertEquals("foo", av.getAccessCode());
        assertEquals("bar", av.getVerifyCode());
    }

    @Test
    public void testCreateNoDivision() {
        AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create("foo;bar");
        assertNull(av.getDivision());
        assertEquals("foo", av.getAccessCode());
        assertEquals("bar", av.getVerifyCode());
    }

    @Test
    public void testCreateNewVerifyCode() {
        AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create("960:foo;bar;baz;baz");
        assertThat(av, instanceOf(ChangeVerifyCodeConnectionSpec.class));
        ChangeVerifyCodeConnectionSpec cvc = (ChangeVerifyCodeConnectionSpec) av;
        assertEquals("960", cvc.getDivision());
        assertEquals("foo", cvc.getAccessCode());
        assertEquals("bar", cvc.getVerifyCode());
        assertEquals("baz", cvc.getNewVerifyCode());
        assertEquals("baz", cvc.getConfirmNewVerifyCode());
    }

    @Test
    public void testCreateNewVerifyCodeWithNoDivision() {
        AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create("960:foo;bar;baz;baz");
        assertThat(av, instanceOf(ChangeVerifyCodeConnectionSpec.class));
        ChangeVerifyCodeConnectionSpec cvc = (ChangeVerifyCodeConnectionSpec) av;
        assertEquals("960", cvc.getDivision());
        assertEquals("foo", cvc.getAccessCode());
        assertEquals("bar", cvc.getVerifyCode());
        assertEquals("baz", cvc.getNewVerifyCode());
        assertEquals("baz", cvc.getConfirmNewVerifyCode());
    }
    
    @Test
    public void testCreateNewVerifyCodeWithSpecialCharacters() {
    	// This used to blowup trying to encode in uri. '%' is a reserved character in uri encoding.
    	AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create("960:foo;%#ar");
    	assertEquals("960", av.getDivision());
    	assertEquals("foo", av.getAccessCode());
    	assertEquals("%#ar", av.getVerifyCode());
    }
}
