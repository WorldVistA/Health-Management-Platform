package org.osehra.cpe.vpr.dao;

import org.osehra.cpe.vpr.pom.IPOMObjectDAO;
import org.osehra.cpe.vpr.sync.vista.VprUpdate;

public interface IVprUpdateDao extends IPOMObjectDAO<VprUpdate> {
    VprUpdate findOneBySystemId(String systemId);
}
