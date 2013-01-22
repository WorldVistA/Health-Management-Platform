package EXT.DOMAIN.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import EXT.DOMAIN.cpe.vista.rpc.RpcEvent;
import EXT.DOMAIN.cpe.vista.rpc.RpcHost;
import EXT.DOMAIN.cpe.vista.rpc.RpcRequest;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static EXT.DOMAIN.cpe.vpr.sync.vista.SynchronizationRpcConstants.VPR_GET_VISTA_DATA_JSON;
import static EXT.DOMAIN.cpe.vpr.sync.vista.SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VprUpdateRpcLogFilterTests {

    private VprUpdateRpcLogFilter filter = new VprUpdateRpcLogFilter();

    @Test
    public void testExcludeEmptyUpdateRpc() throws Exception {
        RpcRequest request = createRequest();
        RpcEvent rpc = new RpcEvent(request, new RpcResponse("{\"apiVersion\":\"foo\",\"data\":{\"lastUpdate\":\"3121212:238\",\"totalItems\":0,\"items\":[]}}"));

        assertThat(filter.isLoggable(rpc), is(false));
    }

    @Test
    public void testIncludeUpdateRpc() throws Exception {
        RpcRequest request = createRequest();
        RpcEvent rpc = new RpcEvent(request, new RpcResponse("{\"apiVersion\":\"foo\",\"data\":{\"lastUpdate\":\"3121212:238\",\"totalItems\":1,\"items\":[{\"bar\":\"baz\"}]}}"));

        assertThat(filter.isLoggable(rpc), is(true));
    }

    private RpcRequest createRequest() {
        Map<String, String> params = new HashMap<String,String>();
        params.put("domain", "new");
        return new RpcRequest(new RpcHost("example.com", 1234), "foo;bar", VPR_SYNCHRONIZATION_CONTEXT, VPR_GET_VISTA_DATA_JSON, params);
    }

}
