package EXT.DOMAIN.cpe.vistalink.locator;

import junit.framework.TestCase;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.resource.cci.ConnectionFactory;
import java.util.HashSet;
import java.util.Set;

public class VistaLinkConnectorConfigConnectionFactoryLocatorTest extends TestCase {

    public void testNoConnectorForRequestedStationNumber() throws Exception {
        VistaLinkConnectorConfigConnectionFactoryLocator locator = new VistaLinkConnectorConfigConnectionFactoryLocator();

        try {
            locator.getConnectionFactory("982");
        } catch (DataAccessResourceFailureException e) {
            assertTrue(true);
        }
    }

    public void testGetConnectionFactory() throws Exception {
        Set<VistaLinkConnectorConfig> connectors = new HashSet<VistaLinkConnectorConfig>();
        connectors.add(new VistaLinkConnectorConfig("982", "FOO FACILITY NUMERO UNO", "vhaislfoo2.vha.DOMAIN.EXT", 8014, "10BAR", "BAR10"));

        VistaLinkConnectorConfigConnectionFactoryLocator locator = new VistaLinkConnectorConfigConnectionFactoryLocator();
        locator.setConnectors(connectors);

        ConnectionFactory cf = locator.getConnectionFactory("982");

        assertNotNull(cf);
    }

    public void testGetConnectionFactoryForDifferentConnectorSets() throws Exception {
        Set<VistaLinkConnectorConfig> connectors1 = new HashSet<VistaLinkConnectorConfig>();
        connectors1.add(new VistaLinkConnectorConfig("982", "FOO FACILITY NUMERO UNO", "vhaislfoo2.vha.DOMAIN.EXT", 8014, "10BAR", "BAR10"));

        VistaLinkConnectorConfigConnectionFactoryLocator locator = new VistaLinkConnectorConfigConnectionFactoryLocator();
        locator.setConnectors(connectors1);

        ConnectionFactory cf = locator.getConnectionFactory("982");
        assertNotNull(cf);
        try {
            locator.getConnectionFactory("983");
        } catch (DataAccessResourceFailureException e) {
            assertTrue(true);
        }

        Set<VistaLinkConnectorConfig> connectors2 = new HashSet<VistaLinkConnectorConfig>();
        connectors2.add(new VistaLinkConnectorConfig("983", "BAR FACILITY NUMERO UNO", "vhaislbar2.vha.DOMAIN.EXT", 8015, "10FOO", "FOO10"));
        locator.setConnectors(connectors2);

        cf = locator.getConnectionFactory("983");
        assertNotNull(cf);
        try {
            locator.getConnectionFactory("982");
        } catch (DataAccessResourceFailureException e) {
            assertTrue(true);
        }

    }
}
