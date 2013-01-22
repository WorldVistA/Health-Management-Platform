package EXT.DOMAIN.cpe.hub.dao;

import EXT.DOMAIN.cpe.dao.HmpRepository;
import EXT.DOMAIN.cpe.hub.VistaAccount;

import java.util.List;
import java.util.Map;

public interface IVistaAccountDao extends HmpRepository<VistaAccount, Integer> {
    VistaAccount findByDivisionHostAndPort(String division, String host, int port);
    List<VistaAccount> findAllByVistaId(String vistaId);
    List<VistaAccount> findAllByVistaIdIsNotNull();
    List<VistaAccount> findAllByHostAndPort(String host, int port);
}
