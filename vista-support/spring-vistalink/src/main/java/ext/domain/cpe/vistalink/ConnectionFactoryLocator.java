package EXT.DOMAIN.cpe.vistalink;

import org.springframework.dao.DataAccessResourceFailureException;

import javax.resource.cci.ConnectionFactory;

/**
 * Implementations of this interface retrieve JCA connection factories for a particular VistA station number.
 *
 * @see EXT.DOMAIN.cpe.vistalink.locator.InstitutionMappingConnectionFactoryLocator
 */
public interface ConnectionFactoryLocator {
    /**
     * Retrieves a connection factory for the specified station number, or throws an exception.  Never should return null.
     *
     * @param stationNumber
     * @return the connection factory for the specified station number, or throw a data access resource exception if there is none.
     * @throws DataAccessResourceFailureException
     *
     */
    ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException;
}
