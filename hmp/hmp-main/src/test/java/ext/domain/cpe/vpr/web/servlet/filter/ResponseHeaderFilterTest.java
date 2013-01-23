package org.osehra.cpe.vpr.web.servlet.filter;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;

public class ResponseHeaderFilterTest extends TestCase {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    protected void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    public void testInitDestroy() throws ServletException {
        FilterConfig filterConfig = new MockFilterConfig();
        ResponseHeaderFilter f = new ResponseHeaderFilter();

        f.init(filterConfig);
        assertSame(filterConfig, f.filterConfig);

        f.destroy();
        assertNull(f.filterConfig);
    }

    public void testFilter() throws IOException, ServletException {
        ResponseHeaderFilter f = new ResponseHeaderFilter();

        MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("Foo", "Bar");
        f.init(config);

        f.doFilter(request, response, filterChain);

        Assert.assertEquals("Bar", response.getHeader("Foo"));

        assertSame(request, filterChain.getRequest());
        assertSame(response, filterChain.getResponse());
    }
}
