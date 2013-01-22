package EXT.DOMAIN.cpe.vista.rpc.conn;

import EXT.DOMAIN.cpe.vista.rpc.ConnectionCallback;
import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import org.springframework.dao.DataAccessException;

public class SystemInfoConnectionCallback implements ConnectionCallback<SystemInfo> {
    @Override
    public SystemInfo doInConnection(Connection con) throws RpcException, DataAccessException {
        return con.getSystemInfo();
    }
}
