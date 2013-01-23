package org.osehra.cprs.shell.service;

import org.osehra.cpe.vistalink.locator.VistaLinkConnectorConfig;

import java.util.Set;

public interface VistaLinkConnectorManager {
    Set<VistaLinkConnectorConfig> getConnectorConfiguration();

    VistaLinkConnectorConfig getConnector(String stationNumber);

    VistaLinkConnectorConfig saveConnector(VistaLinkConnectorConfig connector);

    void removeConnector(String stationNumber);
}
