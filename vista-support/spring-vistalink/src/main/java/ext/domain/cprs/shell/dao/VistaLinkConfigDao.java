package EXT.DOMAIN.cprs.shell.dao;

import EXT.DOMAIN.cpe.vistalink.locator.VistaLinkConnectorConfig;

import java.util.Set;

public interface VistaLinkConfigDao {
    Set<VistaLinkConnectorConfig> getConnectorConfiguration();

    VistaLinkConnectorConfig getConnector(String stationNumber);

    VistaLinkConnectorConfig saveConnector(VistaLinkConnectorConfig connector);

    void removeConnector(String stationNumber);
}
