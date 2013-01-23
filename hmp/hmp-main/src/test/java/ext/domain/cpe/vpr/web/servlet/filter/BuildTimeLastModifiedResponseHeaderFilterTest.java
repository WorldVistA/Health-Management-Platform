package org.osehra.cpe.vpr.web.servlet.filter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class BuildTimeLastModifiedResponseHeaderFilterTest {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private MockServletContext servletContext;
    private StaticWebApplicationContext appContext;
    private BuildTimeLastModifiedResponseHeaderFilter f;

    @Before
    public void setUp() throws Exception {
        appContext = new StaticWebApplicationContext();
        servletContext = new MockServletContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);
        request = new MockHttpServletRequest(servletContext);
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        f = new BuildTimeLastModifiedResponseHeaderFilter();
    }

    @Test
    public void testInitWithDefaultsAndDestroy() throws ServletException {
        FilterConfig filterConfig = new MockFilterConfig(servletContext);
        f.init(filterConfig);
        assertSame(filterConfig, f.getFilterConfig());

        assertEquals(BuildTimeLastModifiedResponseHeaderFilter.DEFAULT_BUILDTIME_DATE_FORMAT, f.getBuildTimeDateFormat());
        assertEquals(BuildTimeLastModifiedResponseHeaderFilter.DEFAULT_BUILDTIME_KEY, f.getBuildTimePropertyKey());
        assertEquals(BuildTimeLastModifiedResponseHeaderFilter.DEFAULT_VERSION_PROPERTY_KEY, f.getVersionPropertyKey());

        f.destroy();
    }

    @Test
    public void testInitParameters() throws ServletException {
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        filterConfig.addInitParameter("buildTimeDateFormat", "MM/dd/yyyy HH:mm:ss Z");
        filterConfig.addInitParameter("buildTimePropertyKey", "foo");
        filterConfig.addInitParameter("versionPropertyKey", "bar");

        f.init(filterConfig);
        assertSame(filterConfig, f.getFilterConfig());

        assertEquals("MM/dd/yyyy HH:mm:ss Z", f.getBuildTimeDateFormat());
        assertEquals("foo", f.getBuildTimePropertyKey());
        assertEquals("bar", f.getVersionPropertyKey());
    }

    @Test
    public void testLastModifiedAndExpiresHeadersForSnapshotVersion() throws IOException, ServletException {
        Map props = new HashMap();
        props.put("dummyBuildTimePropertyKey", "2009-07-27 14:45:28 -0600");
        props.put("dummyVersionPropertyKey", "bar-0.1-SNAPSHOT");

        appContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource("mockEnvironmentProperties", props));
        appContext.refresh();

        MockFilterConfig config = new MockFilterConfig(servletContext);
        config.addInitParameter("buildTimePropertyKey", "dummyBuildTimePropertyKey"); // set up to match the message keys
        config.addInitParameter("versionPropertyKey", "dummyVersionPropertyKey");
        f.init(config);

        f.doFilter(request, response, filterChain);

        assertThat(response.getHeader("Last-Modified"), equalTo("1248727528000"));
        assertThat(response.getHeader("Expires"), equalTo("0"));
    }

    @Test
    public void testLastModifiedAndExpiresHeadersForVersionThatIsNotSnapshot() throws IOException, ServletException {
        Map props = new HashMap();
        props.put("dummyBuildTimePropertyKey", "2009-07-27 14:45:28 -0600");
        props.put("dummyVersionPropertyKey", "bar-1.1");

        appContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource("mockEnvironmentProperties", props));
        appContext.refresh();

        MockFilterConfig config = new MockFilterConfig(servletContext);
        config.addInitParameter("buildTimePropertyKey", "dummyBuildTimePropertyKey"); // set up to match the message keys
        config.addInitParameter("versionPropertyKey", "dummyVersionPropertyKey");
        f.init(config);

        f.doFilter(request, response, filterChain);

        assertThat(response.getHeader("Last-Modified"), equalTo("1248727528000"));
        assertThat(response.getHeader("Expires"), equalTo(new Long(1248727528000L + 1000L * 60L * 60L * 24L * 365L).toString()));
    }
}
