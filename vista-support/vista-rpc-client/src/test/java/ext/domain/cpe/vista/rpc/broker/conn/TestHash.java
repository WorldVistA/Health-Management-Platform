package org.osehra.cpe.vista.rpc.broker.conn;

import junit.framework.TestCase;
import org.junit.Test;

public class TestHash extends TestCase {

    @Test
    public void testHashIdempotent() {
        String foo = "foo";
        assertEquals(foo, Hash.decrypt(Hash.encrypt(foo)));
    }
}
