package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.cpe.vistalink.locator.InstitutionMappingConnectionFactoryLocator;
import junit.framework.TestCase;

public class VistaLinkDaoSupportTest extends TestCase {

    public void testDefaultConstruction() {
        VistaLinkDaoSupport dao = new VistaLinkDaoSupport();
        dao.afterPropertiesSet();
        assertNotNull(dao.getConnectionFactoryLocator());
        assertNotNull(dao.getRpcTemplate());
        assertTrue(dao.getConnectionFactoryLocator() instanceof InstitutionMappingConnectionFactoryLocator);
        assertSame(dao.getConnectionFactoryLocator(), dao.getRpcTemplate().getConnectionFactoryLocator());
    }

    public void testSetConnectionFactoryLocator() {
        VistaLinkDaoSupport dao = new VistaLinkDaoSupport();
        dao.setConnectionFactoryLocator(new MockConnectionFactoryLocator());
        dao.afterPropertiesSet();
        assertNotNull(dao.getConnectionFactoryLocator());
        assertNotNull(dao.getRpcTemplate());
        assertTrue(dao.getConnectionFactoryLocator() instanceof MockConnectionFactoryLocator);
        assertSame(dao.getConnectionFactoryLocator(), dao.getRpcTemplate().getConnectionFactoryLocator());
    }

    public void testSetRpcTemplate() {
        VistaLinkTemplate t = new VistaLinkTemplate(new MockConnectionFactoryLocator());
        VistaLinkDaoSupport dao = new VistaLinkDaoSupport();
        dao.setRpcTemplate(t);
        dao.afterPropertiesSet();
        assertSame(t, dao.getRpcTemplate());
        assertSame(t.getConnectionFactoryLocator(), dao.getConnectionFactoryLocator());
    }
}
