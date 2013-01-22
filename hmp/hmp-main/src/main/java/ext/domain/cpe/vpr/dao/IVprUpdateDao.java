package EXT.DOMAIN.cpe.vpr.dao;

import EXT.DOMAIN.cpe.vpr.pom.IPOMObjectDAO;
import EXT.DOMAIN.cpe.vpr.sync.vista.VprUpdate;

public interface IVprUpdateDao extends IPOMObjectDAO<VprUpdate> {
    VprUpdate findOneBySystemId(String systemId);
}
