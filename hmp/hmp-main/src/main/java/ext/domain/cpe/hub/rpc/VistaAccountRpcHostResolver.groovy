package EXT.DOMAIN.cpe.hub.rpc

import EXT.DOMAIN.cpe.vista.rpc.RpcHost
import EXT.DOMAIN.cpe.vista.rpc.RpcHostResolver
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.VistaIdNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import EXT.DOMAIN.cpe.hub.VistaAccount
import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao

class VistaAccountRpcHostResolver implements RpcHostResolver {

    @Autowired
    IVistaAccountDao vistaAccountDao

    RpcHost resolve(String vistaId) {
        List<VistaAccount> accounts = vistaAccountDao.findAllByVistaId(vistaId);
        if (!accounts) throw new VistaIdNotFoundException(vistaId)
        return new RpcHost(accounts.get(0).host, accounts.get(0).port);
    }
}
