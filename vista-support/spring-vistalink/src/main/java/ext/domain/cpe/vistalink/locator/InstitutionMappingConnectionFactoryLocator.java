package org.osehra.cpe.vistalink.locator;

import org.osehra.cpe.vistalink.ConnectionFactoryLocator;
import org.osehra.vistalink.adapter.cci.VistaLinkConnectionFactory;
import org.osehra.vistalink.institution.InstitutionMapNotInitializedException;
import org.osehra.vistalink.institution.InstitutionMappingDelegate;
import org.osehra.vistalink.institution.InstitutionMappingNotFoundException;
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
