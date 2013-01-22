package EXT.DOMAIN.cpe.vista.rpc;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestRpcResponse {
    @Test
    public void construct() {
        RpcResponse r = new RpcResponse("foo", "bar", "baz");
        assertEquals("foo", r.getSecuritySegment());
        assertEquals("bar", r.getApplicationSegment());
        assertEquals("baz", r.toString());
    }

    @Test
    public void toLines() {
        RpcResponse r = new RpcResponse("sec", "app", "foo\r\nbar\r\nbaz\r\n");
        assertArrayEquals(new String[]{"foo", "bar", "baz"}, r.toLines());
    }
}
