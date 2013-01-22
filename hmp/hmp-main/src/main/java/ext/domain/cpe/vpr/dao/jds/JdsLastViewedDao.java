package EXT.DOMAIN.cpe.vpr.dao.jds;

import EXT.DOMAIN.cpe.vpr.LastViewed;
import EXT.DOMAIN.cpe.vpr.dao.ILastViewedDao;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPOMObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsPOMObjectDAO;

public class JdsLastViewedDao extends JdsPOMObjectDAO<LastViewed> implements ILastViewedDao {
    public JdsLastViewedDao(IGenericPOMObjectDAO genericDao) {
        super(LastViewed.class, genericDao);
    }

    @Override
    public LastViewed findByUidAndUserId(String uid, String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
