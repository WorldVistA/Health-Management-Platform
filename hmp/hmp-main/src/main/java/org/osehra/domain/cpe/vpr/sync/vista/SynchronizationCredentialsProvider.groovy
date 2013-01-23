package org.osehra.cpe.vpr.sync.vista

import org.osehra.cpe.hub.VistaAccount
import org.osehra.cpe.hub.dao.IVistaAccountDao
import org.osehra.cpe.vista.rpc.CredentialsProvider
import org.osehra.cpe.vista.rpc.RpcHost

import org.osehra.cpe.hub.VistaAccountNotFoundException
import org.springframework.beans.factory.annotation.Required

class SynchronizationCredentialsProvider implements CredentialsProvider {

    private IVistaAccountDao vistaAccountDao

    @Required
    void setVistaAccountDao(IVistaAccountDao vistaAccountDao) {
        this.vistaAccountDao = vistaAccountDao
    }

    String getCredentials(RpcHost host, String userInfo) {
        List<VistaAccount> accounts = vistaAccountDao.findAllByHostAndPort(host.getHostname(), host.getPort())
        if (!accounts) throw new VistaAccountNotFoundException(host)

        // use the first one
        VistaAccount account = accounts.get(0)

        String systemId = account.vistaId

        if (!account.vprUserCredentials) {
            throw new SynchronizationCredentialsNotFoundException(account)
        }

        return account.vprUserCredentials
    }
}
