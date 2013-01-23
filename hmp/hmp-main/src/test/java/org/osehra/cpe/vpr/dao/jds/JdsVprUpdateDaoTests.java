package org.osehra.cpe.vpr.dao.jds;

import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.jds.JdsOperations;
import org.osehra.cpe.vpr.sync.vista.VprUpdate;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class JdsVprUpdateDaoTests {

    private JdsVprUpdateDao dao;
    private JdsOperations mockJdsTemplate;
    private IGenericPOMObjectDAO mockGenericDao;

    @Before
    public void setUp() throws Exception {
        mockGenericDao = mock(IGenericPOMObjectDAO.class);
        mockJdsTemplate = mock(JdsOperations.class);

        dao = new JdsVprUpdateDao(mockGenericDao);
        dao.setJdsTemplate(mockJdsTemplate);
        dao.afterPropertiesSet();
    }

    @Test
    public void testFindOneBySystemId() throws Exception {
        VprUpdate lastUpdate = dao.findOneBySystemId("A1B2");

        verify(mockGenericDao).findByUID(VprUpdate.class, "urn:va:vprupdate:A1B2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindOneBySystemIdWithNullArg() throws Exception {
        dao.findOneBySystemId(null);
    }
}
