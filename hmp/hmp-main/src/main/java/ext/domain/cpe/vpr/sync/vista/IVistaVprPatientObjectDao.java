package EXT.DOMAIN.cpe.vpr.sync.vista;

import EXT.DOMAIN.cpe.vpr.pom.IDataStoreDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.Map;

/**
 *  Interface to the VPR PUT PATIENT DATA Remote Procedure call (RPC)
 *
 *  @see "VistA FileMan VPR PATIENT OBJECT(560.1)"
 */
public interface IVistaVprPatientObjectDao {
    <T extends IPatientObject> T save(T entity);
    <T extends IPatientObject> T save(Class<T> entityType, Map<String, Object> data);
}
