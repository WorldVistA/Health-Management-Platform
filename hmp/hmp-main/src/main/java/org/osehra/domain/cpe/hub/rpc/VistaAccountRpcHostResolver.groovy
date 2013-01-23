package org.osehra.cpe.hub.rpc

import org.osehra.cpe.vista.rpc.RpcHost
import org.osehra.cpe.vista.rpc.RpcHostResolver
import org.osehra.cpe.vista.rpc.broker.conn.VistaIdNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.osehra.cpe.hub.VistaAccount
import org.osehra.cpe.hub.dao.IVistaAccountDao

class VistaAccountRpcHostResolver implements RpcHostResolver {

    @Autowired
    IVistaAccountDao vistaAccountDao

    RpcHost resolve(String vistaId) {
        List<VistaAccount> accounts = vistaAccountDao.findAllByVistaId(vistaId);
        if (!accounts) throw new VistaIdNotFoundException(vistaId)
        return new RpcHost(accounts.get(0).host, accounts.get(0).port);
    }
}
