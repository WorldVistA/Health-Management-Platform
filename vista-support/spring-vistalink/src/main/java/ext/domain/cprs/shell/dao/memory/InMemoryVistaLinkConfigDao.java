package org.osehra.cprs.shell.dao.memory;

import org.osehra.cpe.vistalink.locator.VistaLinkConnectorConfig;
import org.osehra.cprs.shell.dao.VistaLinkConfigDao;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Provide summary documentation of class org.osehra.cprs.shell.dao.memory.InMemoryVistaLinkConfigDao
 */
public class InMemoryVistaLinkConfigDao implements VistaLinkConfigDao {

    private Set<VistaLinkConnectorConfig> connectors = new HashSet<VistaLinkConnectorConfig>();

    public VistaLinkConnectorConfig getConnector(String stationNumber) {
        for (VistaLinkConnectorConfig c : connectors) {
            if (c.getPrimaryStation().equals(stationNumber)) return c;
        }
        return null;
    }

    public Set<VistaLinkConnectorConfig> getConnectorConfiguration() {
        return connectors;
    }

    public VistaLinkConnectorConfig saveConnector(VistaLinkConnectorConfig connector) {
        return connector;
    }

    public void removeConnector(String stationNumber) {
        // NOOP;
    }

    public void setConnectors(Set<VistaLinkConnectorConfig> connectors) {
        this.connectors.addAll(connectors);
    }
}
