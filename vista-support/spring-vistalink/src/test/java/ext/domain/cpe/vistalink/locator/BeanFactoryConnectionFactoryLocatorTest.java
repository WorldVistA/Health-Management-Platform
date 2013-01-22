package EXT.DOMAIN.cpe.vistalink.locator;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.resource.cci.ConnectionFactory;

public class BeanFactoryConnectionFactoryLocatorTest extends TestCase {

    public void testConnectionFactoryNotFound() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();

        BeanFactoryConnectionFactoryLocator locator = new BeanFactoryConnectionFactoryLocator();
        locator.setBeanFactory(beanFactory);

        try {
            locator.getConnectionFactory("982");
            fail("expected exception");
        } catch (DataAccessResourceFailureException e) {
            assertTrue(true);
        }
    }

    public void testGetConnectionFactoryForStationNumber() {
        testGetConnectionFactory("982", null, null);
    }

    public void testGetConnectionFactoryWithPrefix() {
        testGetConnectionFactory("982", "pre", null);
    }

    public void testGetConnectionFactoryWithSuffix() {
        testGetConnectionFactory("982", null, "suf");
    }

    public void testGetConnectionFactoryWithPrefixAndSuffix() {
        testGetConnectionFactory("982", "pre", "suf");
    }

    private void testGetConnectionFactory(String stationNumber, String prefix, String suffix) {
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        ConnectionFactory mockConnectionFactory = EasyMock.createMock(ConnectionFactory.class);
        EasyMock.expect(beanFactory.getBean((prefix != null ? prefix : "") + stationNumber + (suffix != null ? suffix : ""), ConnectionFactory.class)).andReturn(mockConnectionFactory);
        EasyMock.replay(beanFactory, mockConnectionFactory);

        BeanFactoryConnectionFactoryLocator locator = new BeanFactoryConnectionFactoryLocator();
        locator.setBeanFactory(beanFactory);
        if (prefix != null) locator.setPrefix(prefix);
        if (suffix != null) locator.setSuffix(suffix);

        ConnectionFactory cf = locator.getConnectionFactory(stationNumber);
        assertSame(mockConnectionFactory, cf);

        EasyMock.verify(beanFactory, mockConnectionFactory);
    }
}
