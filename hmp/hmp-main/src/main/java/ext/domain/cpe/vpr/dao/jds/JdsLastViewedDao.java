package org.osehra.cpe.vpr.dao.jds;

import org.osehra.cpe.vpr.LastViewed;
import org.osehra.cpe.vpr.dao.ILastViewedDao;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.jds.JdsPOMObjectDAO;

public class JdsLastViewedDao extends JdsPOMObjectDAO<LastViewed> implements ILastViewedDao {
    public JdsLastViewedDao(IGenericPOMObjectDAO genericDao) {
        super(LastViewed.class, genericDao);
    }

    @Override
    public LastViewed findByUidAndUserId(String uid, String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
