package org.osehra.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import org.osehra.cpe.vista.rpc.JacksonRpcResponseExtractor;
import org.osehra.cpe.vista.rpc.RpcEvent;
import org.osehra.cpe.vista.rpc.RpcRequest;
import org.osehra.cpe.vista.rpc.broker.protocol.Mult;
import org.osehra.cpe.vista.rpc.broker.protocol.RpcParam;
import org.osehra.cpe.vista.rpc.support.RpcLogFilter;

import static org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants.VPR_GET_VISTA_DATA_JSON;
import static org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT;

/**
 * Filters out VPR Update RPCs from the log when there are no updates
 */
public class VprUpdateRpcLogFilter implements RpcLogFilter {

    private JacksonRpcResponseExtractor jsonExtractor = new JacksonRpcResponseExtractor();

    @Override
    public boolean isLoggable(RpcEvent rpcEvent) {
        RpcRequest request = rpcEvent.getRequest();
        if (VPR_SYNCHRONIZATION_CONTEXT.equals(request.getRpcContext()) && VPR_GET_VISTA_DATA_JSON.equals(request.getRpcName()) && request.getParams().size() >= 1) {
            RpcParam param = request.getParams().get(0);
            Mult mult = param.getMult();
            if (mult != null && mult.get("\"domain\"").equalsIgnoreCase("new")) {
                JsonNode json = jsonExtractor.extractData(rpcEvent.getResponse());
                if (json.path("data").path("totalItems").asInt() == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
