package org.osehra.cpe.vpr.dao.multi;

import org.osehra.cpe.vpr.pom.IPatientDAO;

import java.util.Map;

// consider implementing this as a dynamic proxy
public class RoutingPatientDao {
    private Map<String, IPatientDAO> targetDaos;

    private IPatientDAO defaultTargetDao;
}
