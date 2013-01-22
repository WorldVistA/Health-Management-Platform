package EXT.DOMAIN.cpe.vista.rpc.support;

import EXT.DOMAIN.cpe.vista.rpc.ConnectionCallback;
import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.RpcRequest;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;
import EXT.DOMAIN.cpe.vista.rpc.conn.Connection;
import org.springframework.dao.DataAccessException;

public class RpcConnectionCallback implements ConnectionCallback<RpcResponse> {

    private RpcRequest request;

    public RpcConnectionCallback(RpcRequest request) {
        this.request = request;
    }

    @Override
    public RpcResponse doInConnection(Connection con) throws RpcException, DataAccessException {
        return con.send(request);
    }
}
