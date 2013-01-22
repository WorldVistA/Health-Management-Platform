package EXT.DOMAIN.cpe.vistalink.locator;

import EXT.DOMAIN.cpe.vistalink.ConnectionFactoryLocator;
import EXT.DOMAIN.vistalink.adapter.cci.VistaLinkConnectionFactory;
import EXT.DOMAIN.vistalink.institution.InstitutionMapNotInitializedException;
import EXT.DOMAIN.vistalink.institution.InstitutionMappingDelegate;
import EXT.DOMAIN.vistalink.institution.InstitutionMappingNotFoundException;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.cci.ConnectionFactory;
import java.text.MessageFormat;

public class InstitutionMappingConnectionFactoryLocator implements ConnectionFactoryLocator {
    private static final String NO_CONNECTION_FACTORY = "Unable to obtain connection factory for station ''{0}''";
    private static final String NO_CONNECTION_FACTORY_JNDI = "Unable to obtain connection factory for station ''{0}'' at JNDI name ''{1}''";

    public ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException {
        try {
            String jndiConnectorName = InstitutionMappingDelegate.getJndiConnectorNameForInstitution(
                    stationNumber);
            try {
                Context ic = new InitialContext();
                VistaLinkConnectionFactory vistaLinkConnectionFactory = (VistaLinkConnectionFactory) ic.lookup(jndiConnectorName);
                if (vistaLinkConnectionFactory == null)
                    throw new DataAccessResourceFailureException(MessageFormat.format(NO_CONNECTION_FACTORY_JNDI, stationNumber, jndiConnectorName));
                return vistaLinkConnectionFactory;
            } catch (NamingException e) {
                throw new DataAccessResourceFailureException(MessageFormat.format(NO_CONNECTION_FACTORY_JNDI, stationNumber, jndiConnectorName), e);
            }
        } catch (InstitutionMappingNotFoundException e) {
            throw new DataAccessResourceFailureException(MessageFormat.format(NO_CONNECTION_FACTORY, stationNumber), e);
        } catch (InstitutionMapNotInitializedException e) {
            throw new DataAccessResourceFailureException(MessageFormat.format(NO_CONNECTION_FACTORY, stationNumber), e);
        }

    }
}
