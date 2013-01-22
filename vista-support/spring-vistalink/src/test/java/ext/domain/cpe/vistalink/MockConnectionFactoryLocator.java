package EXT.DOMAIN.cpe.vistalink;

import org.springframework.dao.DataAccessResourceFailureException;

import javax.resource.cci.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

public class MockConnectionFactoryLocator extends HashMap implements ConnectionFactoryLocator, Map {
    public ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException {
        ConnectionFactory connectionFactory = (ConnectionFactory) get(stationNumber);
        if (connectionFactory == null)
            throw new DataAccessResourceFailureException("unable to locate connection factory for station number " + stationNumber);
        return connectionFactory;
    }
}
