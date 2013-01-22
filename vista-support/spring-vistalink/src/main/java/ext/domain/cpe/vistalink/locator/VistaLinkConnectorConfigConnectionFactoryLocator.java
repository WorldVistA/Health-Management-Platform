package EXT.DOMAIN.cpe.vistalink.locator;

import EXT.DOMAIN.cpe.vistalink.ConnectionFactoryLocator;
import EXT.DOMAIN.cpe.vistalink.adapter.spi.VistaLinkManagedConnectionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jca.support.LocalConnectionFactoryBean;

import javax.resource.cci.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VistaLinkConnectorConfigConnectionFactoryLocator implements ConnectionFactoryLocator {

    private static final String VLJ_CONNECTOR_PREFIX = "vljConnector";

    private Map<String, VistaLinkConnectorConfig> connectors = new HashMap<String, VistaLinkConnectorConfig>();
    private BeanFactoryConnectionFactoryLocator locator = new BeanFactoryConnectionFactoryLocator();

    public VistaLinkConnectorConfigConnectionFactoryLocator() {
        locator.setPrefix(VLJ_CONNECTOR_PREFIX);
        locator.setBeanFactory(createBeanFactory());
    }

    private BeanFactory createBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        for (VistaLinkConnectorConfig connector : connectors.values()) {
            beanFactory.registerBeanDefinition(getManagedConnectionFactoryBeanName(connector), createManagedConnectionFactoryBeanDefinition(connector));
            beanFactory.registerBeanDefinition(getConnectionFactoryBeanName(connector), createConnectionFactoryBeanDefinition(connector));
        }
        return beanFactory;
    }

    private String getConnectionFactoryBeanName(VistaLinkConnectorConfig connector) {
        return VLJ_CONNECTOR_PREFIX + connector.getPrimaryStation();
    }

    private BeanDefinition createManagedConnectionFactoryBeanDefinition(VistaLinkConnectorConfig connector) {
        BeanDefinitionBuilder managedConnectionFactoryBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(VistaLinkManagedConnectionFactory.class);
        managedConnectionFactoryBeanDefBuilder.addPropertyValue("stationNumber", connector.getPrimaryStation());
        managedConnectionFactoryBeanDefBuilder.addPropertyValue("nonManagedHostIPAddress", connector.getHost());
        managedConnectionFactoryBeanDefBuilder.addPropertyValue("nonManagedHostPort", connector.getPort());
        managedConnectionFactoryBeanDefBuilder.addPropertyValue("nonManagedAccessCode", connector.getAccessCode());
        managedConnectionFactoryBeanDefBuilder.addPropertyValue("nonManagedVerifyCode", connector.getVerifyCode());
        return managedConnectionFactoryBeanDefBuilder.getBeanDefinition();
    }

    private BeanDefinition createConnectionFactoryBeanDefinition(VistaLinkConnectorConfig connector) {
        BeanDefinitionBuilder localConnectionFactoryBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(LocalConnectionFactoryBean.class);
        localConnectionFactoryBeanDefBuilder.addPropertyReference("managedConnectionFactory", getManagedConnectionFactoryBeanName(connector));
        return localConnectionFactoryBeanDefBuilder.getBeanDefinition();
    }

    private String getManagedConnectionFactoryBeanName(VistaLinkConnectorConfig connector) {
        return "vljManagedConnectionFactory" + connector.getPrimaryStation();
    }

    public ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException {
        return locator.getConnectionFactory(stationNumber);
    }

    public void setConnectors(Set<VistaLinkConnectorConfig> connectors) {
        for (VistaLinkConnectorConfig connector : connectors) {
            this.connectors.put(connector.getPrimaryStation(), connector);
        }
        locator.setBeanFactory(createBeanFactory());
    }
}
