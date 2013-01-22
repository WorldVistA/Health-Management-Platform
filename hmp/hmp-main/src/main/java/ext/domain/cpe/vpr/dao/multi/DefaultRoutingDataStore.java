package EXT.DOMAIN.cpe.vpr.dao.multi;

import EXT.DOMAIN.cpe.vpr.pom.IDataStoreDAO;
import EXT.DOMAIN.cpe.vpr.dao.RoutingDataStore;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import org.perf4j.StopWatch;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class DefaultRoutingDataStore implements RoutingDataStore, EnvironmentAware {

    private Environment environment;
    private SortedMap<String, IDataStoreDAO> dataStores;

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Required
    public void setDataStores(SortedMap<String, IDataStoreDAO> dataStores) {
        this.dataStores = dataStores;
    }

    @Override
    public <T extends IPatientObject> void save(T entity) {
        for (Map.Entry<String, IDataStoreDAO> dataStoreEntry : dataStores.entrySet()) {
            String dataSource = dataStoreEntry.getKey();
            if (!environment.acceptsProfiles(dataSource)) continue;

            IDataStoreDAO dao = dataStoreEntry.getValue();
            if (dao == null) {
                throw new InvalidDataAccessResourceUsageException("No implementation of IDataStoreDAO registered under key '" + dataStoreEntry.getKey() + "'");
            } else {
                StopWatch timer = new StopWatch("vpr.persist." + ClassUtils.getShortNameAsProperty(entity.getClass()) + "." + dataSource);
                dao.save(entity);
                timer.stop();
            }
        }
    }

    @Override
    public <T extends IPatientObject> void delete(T entity) {
        for (Map.Entry<String, IDataStoreDAO> dataStoreEntry : dataStores.entrySet()) {
            String dataSource = dataStoreEntry.getKey();
            if (!environment.acceptsProfiles(dataSource)) continue;

            IDataStoreDAO dao = dataStoreEntry.getValue();
            if (dao == null) {
                throw new InvalidDataAccessResourceUsageException("No implementation of IDataStoreDAO registered under key '" + dataStoreEntry.getKey() + "'");
            } else {
                StopWatch timer = new StopWatch("vpr.delete." + ClassUtils.getShortNameAsProperty(entity.getClass()) + "." + dataSource);
                dao.delete(entity);
                timer.stop();
            }
        }
    }
}
