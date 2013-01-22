package EXT.DOMAIN.cpe.vpr.sync.vista;

import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;
import EXT.DOMAIN.cpe.vpr.pom.IDataStoreDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPOMObject;

import java.util.Map;

/**
 *  Interface to the VPR PUT OBJECT Remote Procedure call (RPC)
 *
 *  @see "VistA FileMan VPR OBJECT(560.11)"
 */
public interface IVistaVprObjectDao {
    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance.
     *
     * @param entity
     * @return the saved entity
     */
    <T extends IPOMObject> T save(T entity);

    /**
     * Saves given entity data converting it to the requested entityType. Use the returned instance for further operations as the save operation might have changed the
     * entity data.
     *
     * @param entityType
     * @param data
     * @return the saved entity
     */
    <T extends IPOMObject> T save(Class<T> entityType, Map<String, Object> data);
}
