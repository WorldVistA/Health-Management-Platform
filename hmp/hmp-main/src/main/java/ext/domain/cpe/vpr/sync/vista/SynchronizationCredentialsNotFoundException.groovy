package EXT.DOMAIN.cpe.vpr.sync.vista

import EXT.DOMAIN.cpe.hub.VistaAccount

class SynchronizationCredentialsNotFoundException extends RuntimeException {
    SynchronizationCredentialsNotFoundException(VistaAccount vistaAccount) {
        super("Credentials for the VPR synchronization user for the '${vistaAccount.name}' VistA system with id '${vistaAccount.vistaId}' and station number '${vistaAccount.division}' are not found. Please validate your VPR VistA configuration.".toString());
    }
}
