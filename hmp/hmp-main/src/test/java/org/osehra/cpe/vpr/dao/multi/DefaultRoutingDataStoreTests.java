package org.osehra.cpe.vpr.dao.multi;

import org.osehra.cpe.vpr.pom.IDataStoreDAO;
import org.osehra.cpe.test.mockito.ReturnsArgument;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.sync.vista.Foo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DefaultRoutingDataStoreTests {

    private IDataStoreDAO fooMockDao;
    private IDataStoreDAO barMockDao;
    private SortedMap<String, IDataStoreDAO> daosByDataSource;
    private DefaultRoutingDataStore dataStore;

    @Before
    public void setUp() throws Exception {
        fooMockDao = mock(IDataStoreDAO.class);
        barMockDao = mock(IDataStoreDAO.class);

        daosByDataSource = new TreeMap<String, IDataStoreDAO>();
        daosByDataSource.put("foo", fooMockDao);
        daosByDataSource.put("bar", barMockDao);

        dataStore = new DefaultRoutingDataStore();
        dataStore.setEnvironment(mock(Environment.class));
        dataStore.setDataStores(daosByDataSource);
    }

    @Test
    public void testSaveDelegatesToConfiguredDaos() throws Exception {
        when(dataStore.getEnvironment().acceptsProfiles("foo")).thenReturn(true);
        when(dataStore.getEnvironment().acceptsProfiles("bar")).thenReturn(true);

        Foo foo = new Foo();

        dataStore.save(foo);

        verify(fooMockDao).save(foo);
        verify(barMockDao).save(foo);
    }

    @Test
    public void testSaveDelegatesToConfiguredDaosBasedOnActiveSpringProfiles() throws Exception {
        when(dataStore.getEnvironment().acceptsProfiles("foo")).thenReturn(true);
        when(dataStore.getEnvironment().acceptsProfiles("bar")).thenReturn(false);

        Foo foo = new Foo();

        dataStore.save(foo);

        verify(fooMockDao).save(foo);
        verifyZeroInteractions(barMockDao);
   }
}
