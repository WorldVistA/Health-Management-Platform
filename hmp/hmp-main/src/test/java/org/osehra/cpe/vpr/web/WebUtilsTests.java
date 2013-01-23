package org.osehra.cpe.vpr.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class WebUtilsTests {

    private PortResolver mockPortResolver;

    @Before
    public void setUp() throws Exception {
        mockPortResolver = mock(PortResolver.class);
    }

    @Test
    public void testIsAjax() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("X-Requested-With", "XMLHttpRequest");

        assertThat(WebUtils.isAjax(request), equalTo(true));

        request.addHeader("x-requested-with", "XMLHttpRequest"); // IE sends lower case header

        assertThat(WebUtils.isAjax(request), equalTo(true));
    }

    @Test
    public void testIsNotAjax() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThat(WebUtils.isAjax(request), equalTo(false));
    }

    @Test
    public void testSavedRequestIsAjax() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Requested-With", "XMLHttpRequest");

        SavedRequest savedRequest = new DefaultSavedRequest(request, mockPortResolver);

        assertThat(WebUtils.isAjax(savedRequest), equalTo(true));

        request.addHeader("x-requested-with", "XMLHttpRequest"); // IE sends lower case header
        savedRequest = new DefaultSavedRequest(request, mockPortResolver);

        assertThat(WebUtils.isAjax(savedRequest), equalTo(true));
    }

    @Test
    public void testSavedRequestIsNotAjax() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        SavedRequest savedRequest = new DefaultSavedRequest(request, mockPortResolver);
        assertThat(WebUtils.isAjax(savedRequest), equalTo(false));
    }
}
