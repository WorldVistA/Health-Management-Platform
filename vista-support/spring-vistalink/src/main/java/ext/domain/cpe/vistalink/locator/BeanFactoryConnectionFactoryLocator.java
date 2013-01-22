package EXT.DOMAIN.cpe.vistalink.locator;

import EXT.DOMAIN.cpe.vistalink.ConnectionFactoryLocator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.resource.cci.ConnectionFactory;
import java.text.MessageFormat;

import static org.springframework.util.StringUtils.hasText;

public class BeanFactoryConnectionFactoryLocator implements ConnectionFactoryLocator, BeanFactoryAware {

    private static final String NO_CONNECTION_FACTORY = "Unable to obtain connection factory for station ''{0}''";

    private BeanFactory beanFactory;
    private String prefix;
    private String suffix;

    public ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException {
        try {
            ConnectionFactory connectionFactory = (ConnectionFactory) beanFactory.getBean(getBeanName(stationNumber), ConnectionFactory.class);
            return connectionFactory;
        } catch (BeansException e) {
            throw new DataAccessResourceFailureException(MessageFormat.format(NO_CONNECTION_FACTORY, stationNumber), e);
        }
    }

    private String getBeanName(String stationNumber) {
        StringBuilder builder = new StringBuilder();
        if (hasText(getPrefix())) {
            builder.append(getPrefix());
        }
        builder.append(stationNumber);
        if (hasText(getSuffix())) {
            builder.append(getSuffix());
        }
        return builder.toString();
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
