package org.osehra.cpe.vpr.dao;

import org.osehra.cpe.dao.HmpRepository;
import org.osehra.cpe.vpr.LastViewed;
import org.osehra.cpe.vpr.pom.IPOMObjectDAO;

public interface ILastViewedDao extends IPOMObjectDAO<LastViewed> {

    LastViewed findByUidAndUserId(String uid, String userId);
}
