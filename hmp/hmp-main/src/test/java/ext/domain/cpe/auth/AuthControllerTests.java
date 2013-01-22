package EXT.DOMAIN.cpe.auth;

import EXT.DOMAIN.cpe.HmpProperties;
import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.web.servlet.ModelAndView;

import static EXT.DOMAIN.cpe.HmpProperties.SETUP_COMPLETE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthControllerTests {

    private AuthController c;
    private UserContext mockUserContext;
    private RpcOperations mockAuthenticationRpcTemplate;
    private IVistaAccountDao mockVistaAccountDao;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockHttpSession mockSession;
    private Environment mockEnvironment;

    @Before
    public void setUp() throws Exception {
        mockSession = new MockHttpSession();
        mockRequest = new MockHttpServletRequest();
        mockRequest.setSession(mockSession);
        mockResponse = new MockHttpServletResponse();

        mockUserContext = mock(UserContext.class);
        mockAuthenticationRpcTemplate = mock(RpcOperations.class);
        mockVistaAccountDao = mock(IVistaAccountDao.class);
        mockEnvironment = mock(Environment.class);

        c = new AuthController();
        c.setUserContext(mockUserContext);
        c.setAuthenticationRpcTemplate(mockAuthenticationRpcTemplate);
        c.setVistaAccountDao(mockVistaAccountDao);
        c.setEnvironment(mockEnvironment);
    }

    @Test
    public void testSetupNotCompleteRedirectsToRootSoThatSpringSecurityFilterPicksItUpRequest() throws Exception {
        when(mockEnvironment.getProperty(SETUP_COMPLETE)).thenReturn(null);

        ModelAndView mav = c.login(mockRequest, mockResponse);
        assertThat(mav.getViewName(), equalTo("redirect:/"));
    }

    @Test
    public void testDisplayLoginPageWithOriginalSavedRequestIfSetupComplete() throws Exception {
        when(mockEnvironment.getProperty(SETUP_COMPLETE)).thenReturn("true");
        when(mockEnvironment.getProperty(HmpProperties.VERSION)).thenReturn("fred");

        MockHttpServletRequest mockOriginalRequest = new MockHttpServletRequest();
        mockOriginalRequest.setScheme("https");
        mockOriginalRequest.setServerName("example.org");
        mockOriginalRequest.setServerPort(3333);
        mockOriginalRequest.setRequestURI("/foo/bar/baz");

        PortResolver mockPortResolver = mock(PortResolver.class);
        when(mockPortResolver.getServerPort(mockOriginalRequest)).thenReturn(3333);

        // spring security will have put the original request in the session
        mockSession.setAttribute("SPRING_SECURITY_SAVED_REQUEST", new DefaultSavedRequest(mockOriginalRequest, mockPortResolver));

        ModelAndView mav = c.login(mockRequest, mockResponse);
        assertThat(mav.getViewName(), equalTo("/auth/login"));
        assertThat(((String) mav.getModel().get("hmpVersion")), equalTo("fred"));
    }

    @Test
    public void testDisplayLoginPageAfterAjaxRequestWhichFailedDueToExpiredSession() throws Exception {
        when(mockEnvironment.getProperty(SETUP_COMPLETE)).thenReturn("true");
        when(mockEnvironment.getProperty(HmpProperties.VERSION)).thenReturn("fred");

        MockHttpServletRequest mockOriginalRequest = new MockHttpServletRequest();
        mockOriginalRequest.addHeader("X-Requested-With", "XMLHttpRequest");
        mockOriginalRequest.setScheme("https");
        mockOriginalRequest.setServerName("example.org");
        mockOriginalRequest.setServerPort(3333);
        mockOriginalRequest.setRequestURI("/foo/bar/baz");

        PortResolver mockPortResolver = mock(PortResolver.class);
        when(mockPortResolver.getServerPort(mockOriginalRequest)).thenReturn(3333);

        // spring security will have put the original request in the session
        mockSession.setAttribute("SPRING_SECURITY_SAVED_REQUEST", new DefaultSavedRequest(mockOriginalRequest, mockPortResolver));

        ModelAndView mav = c.login(mockRequest, mockResponse);
        assertThat(mav.getViewName(), equalTo("/auth/login"));
        assertThat(((String) mav.getModel().get("hmpVersion")), equalTo("fred"));
    }

    @Test
    public void testAlreadyLoggedInRedirectsToRoot() throws Exception {
        when(mockUserContext.isLoggedIn()).thenReturn(true);

        ModelAndView mav = c.login(mockRequest, mockResponse);
        assertThat(mav.getViewName(), equalTo("redirect:/"));
    }

    @Test
    public void testLogout() throws Exception {
        assertThat(c.logout(), equalTo("redirect:/j_spring_security_logout"));
    }
}
