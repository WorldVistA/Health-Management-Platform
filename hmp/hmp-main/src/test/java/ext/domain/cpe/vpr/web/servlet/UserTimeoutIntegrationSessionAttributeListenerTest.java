package org.osehra.cpe.vpr.web.servlet;

import org.osehra.cpe.auth.HmpUser;
import org.osehra.cpe.auth.HmpUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpSessionBindingEvent;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTimeoutIntegrationSessionAttributeListenerTest {

    private UserTimeoutIntegrationSessionAttributeListener listener;
    private SecurityContext mockSecurityContext;
    private MockHttpSession mockSession;

    @Before
    public void setUp() throws Exception {
        listener = new UserTimeoutIntegrationSessionAttributeListener();
        mockSession = new MockHttpSession();
        mockSession.setMaxInactiveInterval(600); // default of 5 minutes

        mockSecurityContext = mock(SecurityContext.class);
        Authentication mockAuth = mock(Authentication.class);
        HmpUserDetails mockUser = mock(HmpUserDetails.class);

        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(mockUser.getTimeoutSeconds()).thenReturn(420);
    }

    @Test
    public void testAttributeAdded() throws Exception {
        listener.attributeAdded(new HttpSessionBindingEvent(mockSession, HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, mockSecurityContext));

        assertThat(mockSession.getMaxInactiveInterval(), is(420));
    }

    @Test
    public void testAttributeReplaced() throws Exception {

    }

    @Test
    public void testAttributeRemoved() throws Exception {

    }
}
