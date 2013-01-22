package EXT.DOMAIN.cpe.vpr.sync.vista

import EXT.DOMAIN.cpe.hub.VistaAccount
import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao
import EXT.DOMAIN.cpe.vista.rpc.CredentialsProvider
import EXT.DOMAIN.cpe.vista.rpc.RpcHost

import EXT.DOMAIN.cpe.hub.VistaAccountNotFoundException
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
