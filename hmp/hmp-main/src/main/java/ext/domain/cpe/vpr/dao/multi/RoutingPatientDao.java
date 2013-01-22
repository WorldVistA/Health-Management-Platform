package EXT.DOMAIN.cpe.vpr.dao.multi;

import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;

import java.util.Map;

// consider implementing this as a dynamic proxy
public class RoutingPatientDao {
    private Map<String, IPatientDAO> targetDaos;

    private IPatientDAO defaultTargetDao;
}
