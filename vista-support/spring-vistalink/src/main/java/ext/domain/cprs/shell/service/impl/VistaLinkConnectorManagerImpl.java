package org.osehra.cprs.shell.service.impl;

import org.osehra.cpe.vistalink.locator.VistaLinkConnectorConfig;
import org.osehra.cpe.vistalink.locator.VistaLinkConnectorConfigConnectionFactoryLocator;
import org.osehra.cprs.shell.dao.VistaLinkConfigDao;
import org.osehra.cprs.shell.service.VistaLinkConnectorManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Set;

public class VistaLinkConnectorManagerImpl implements VistaLinkConnectorManager, InitializingBean {
    private VistaLinkConfigDao vistaLinkConfigDao;
    private VistaLinkConnectorConfigConnectionFactoryLocator vistaLinkConnectorConfigConnectionFactoryLocator;

    @Required
    public void setVistaLinkConfigDao(VistaLinkConfigDao vistaLinkConfigDao) {
        this.vistaLinkConfigDao = vistaLinkConfigDao;
    }

    @Required
    public void setVistaLinkConnectorConfigConnectionFactoryLocator(VistaLinkConnectorConfigConnectionFactoryLocator vistaLinkConnectorConfigConnectionFactoryLocator) {
        this.vistaLinkConnectorConfigConnectionFactoryLocator = vistaLinkConnectorConfigConnectionFactoryLocator;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.vistaLinkConfigDao, "vistaLinkConfigDao is required to be set");
        Assert.notNull(this.vistaLinkConnectorConfigConnectionFactoryLocator, "vistaLinkConnectorConfigConnectionFactoryLocator is required to be set");
        refreshConnectors();
    }

    public Set<VistaLinkConnectorConfig> getConnectorConfiguration() {
        return vistaLinkConfigDao.getConnectorConfiguration();
    }

    public VistaLinkConnectorConfig getConnector(String stationNumber) {
        return vistaLinkConfigDao.getConnector(stationNumber);
    }

    public VistaLinkConnectorConfig saveConnector(VistaLinkConnectorConfig connector) {
        try {
            return vistaLinkConfigDao.saveConnector(connector);
        } finally {
            refreshConnectors();
        }
    }

    public void removeConnector(String stationNumber) {
        vistaLinkConfigDao.removeConnector(stationNumber);
        refreshConnectors();
    }

    private void refreshConnectors() {
        vistaLinkConnectorConfigConnectionFactoryLocator.setConnectors(getConnectorConfiguration());
    }
}
