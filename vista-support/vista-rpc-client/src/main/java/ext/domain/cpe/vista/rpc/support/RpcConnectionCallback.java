package org.osehra.cpe.vista.rpc.support;

import org.osehra.cpe.vista.rpc.ConnectionCallback;
import org.osehra.cpe.vista.rpc.RpcException;
import org.osehra.cpe.vista.rpc.RpcRequest;
import org.osehra.cpe.vista.rpc.RpcResponse;
import org.osehra.cpe.vista.rpc.conn.Connection;
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
