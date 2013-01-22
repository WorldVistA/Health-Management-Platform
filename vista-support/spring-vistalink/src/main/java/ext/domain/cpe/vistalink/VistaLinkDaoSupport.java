package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.cpe.vistalink.locator.InstitutionMappingConnectionFactoryLocator;
import org.springframework.dao.support.DaoSupport;

public class VistaLinkDaoSupport extends DaoSupport {

    private VistaLinkTemplate rpcTemplate;

    protected void checkDaoConfig() throws IllegalArgumentException {
        if (this.rpcTemplate == null) {
            setConnectionFactoryLocator(new InstitutionMappingConnectionFactoryLocator());
        }
    }

    protected VistaLinkTemplate createRpcTemplate(ConnectionFactoryLocator connectionFactoryLocator) {
        return new VistaLinkTemplate(connectionFactoryLocator);
    }

    public ConnectionFactoryLocator getConnectionFactoryLocator() {
        return rpcTemplate.getConnectionFactoryLocator();
    }

    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        setRpcTemplate(createRpcTemplate(connectionFactoryLocator));
    }

    public VistaLinkTemplate getRpcTemplate() {
        return rpcTemplate;
    }

    public void setRpcTemplate(VistaLinkTemplate rpcTemplate) {
        this.rpcTemplate = rpcTemplate;
    }
}
