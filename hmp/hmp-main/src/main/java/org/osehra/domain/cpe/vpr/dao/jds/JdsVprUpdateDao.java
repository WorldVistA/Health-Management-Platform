package org.osehra.cpe.vpr.dao.jds;

import org.osehra.cpe.vpr.dao.IVprUpdateDao;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.jds.JdsPOMObjectDAO;
import org.osehra.cpe.vpr.sync.vista.VprUpdate;
import org.springframework.util.Assert;

public class JdsVprUpdateDao extends JdsPOMObjectDAO<VprUpdate> implements IVprUpdateDao {

    public JdsVprUpdateDao(IGenericPOMObjectDAO genericDao) {
        super(VprUpdate.class, genericDao);
    }

    @Override
    public VprUpdate findOneBySystemId(String systemId) {
        Assert.notNull(systemId, "[Assertion failed] - 'systemId' argument is required; it must not be null");

        return findOne("urn:va:vprupdate:" + systemId);
    }
}
