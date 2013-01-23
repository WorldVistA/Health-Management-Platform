package org.osehra.cpe.vpr.pom.jds;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DefaultJdsExceptionTranslatorTests {

    private DefaultJdsExceptionTranslator t;

    @Before
    public void setUp() throws Exception {
        t = new DefaultJdsExceptionTranslator();
    }

    @Test
    public void testHttpServerErrorException() throws Exception {
//        DataAccessException e = t.translate("foo", "/vpr/34", new HttpServerErrorException())
    }

    @Test
    public void testResourceAccessException() throws Exception {
        ResourceAccessException original = new ResourceAccessException("network is down or sommat like that");

        DataAccessException e = t.translate("foo", "/vpr/34", original);

        assertThat(e, instanceOf(DataAccessResourceFailureException.class));
        assertThat((ResourceAccessException) e.getRootCause(), sameInstance(original));
    }
}
