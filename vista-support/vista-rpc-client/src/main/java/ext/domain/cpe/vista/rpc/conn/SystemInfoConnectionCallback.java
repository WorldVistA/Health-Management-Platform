package org.osehra.cpe.vista.rpc.conn;

import org.osehra.cpe.vista.rpc.ConnectionCallback;
import org.osehra.cpe.vista.rpc.RpcException;
import org.springframework.dao.DataAccessException;

public class SystemInfoConnectionCallback implements ConnectionCallback<SystemInfo> {
    @Override
    public SystemInfo doInConnection(Connection con) throws RpcException, DataAccessException {
        return con.getSystemInfo();
    }
}
