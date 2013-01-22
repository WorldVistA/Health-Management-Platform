package EXT.DOMAIN.cpe.vpr.dao;

import EXT.DOMAIN.cpe.dao.HmpRepository;
import EXT.DOMAIN.cpe.vpr.LastViewed;
import EXT.DOMAIN.cpe.vpr.pom.IPOMObjectDAO;

public interface ILastViewedDao extends IPOMObjectDAO<LastViewed> {

    LastViewed findByUidAndUserId(String uid, String userId);
}
