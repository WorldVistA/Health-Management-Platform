package EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails;

import EXT.DOMAIN.cpe.vistalink.AbstractVistaLinkConnectionTest;
import EXT.DOMAIN.vistalink.adapter.record.VistaLinkFaultException;
import EXT.DOMAIN.vistalink.security.m.SecurityAccessVerifyCodePairInvalidException;
import org.easymock.EasyMock;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;

import javax.resource.ResourceException;
import java.io.IOException;

import static EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails.VistaLinkUserDetailService.*;
import static org.easymock.EasyMock.expect;

public class VistaLinkUserDetailServiceTest extends AbstractVistaLinkConnectionTest {

    private static final String TEST_DUZ = "12345";
    private static final String TEST_STATION_NUMBER = "982";
    private static final String TEST_SIGNON_LOG_IEN = "3080311.14052001";

    private static final String TEST_ACCESS_CODE = "FOOBAR";
    private static final String TEST_VERIFY_CODE = "BARFOO";
    private static final String TEST_CLIENT_IP_ADDRESS = "127.0.0.1";
    private static final String TEST_APPLICATION_NAME = "Test Application Name";

    private VistaLinkUserDetailService userDetailService = new VistaLinkUserDetailService();

    private VistaUserCache mockUserCache;

    protected String getStationNumber() {
        return TEST_STATION_NUMBER;
    }

    protected void setUp() throws Exception {
        mockUserCache = EasyMock.createMock(VistaUserCache.class);
        super.setUp();
        setExpectedTimeOut(DEFAULT_TIMEOUT);
        userDetailService.setUserCache(mockUserCache);
        userDetailService.setConnectionFactoryLocator(mockConnectionFactoryLocator);
        userDetailService.setApplicationName(TEST_APPLICATION_NAME);
        userDetailService.afterPropertiesSet();
    }

    public void testDefaultTimeOut() {
        assertEquals(DEFAULT_TIMEOUT, userDetailService.getRpcTemplate().getTimeOut());
    }

    public void testMissingCredentialsThrowsBadCredentials() {
        try {
            userDetailService.login(null, TEST_STATION_NUMBER, null, null, null, null, null);
            fail("expected bad credentials exception");
        } catch (BadCredentialsException e) {
            // NOOP
        }
        try {
            userDetailService.login(null, TEST_STATION_NUMBER, TEST_ACCESS_CODE, TEST_VERIFY_CODE, null, null, null);
            fail("expected bad credentials exception");
        } catch (BadCredentialsException e) {
            // NOOP
        }
        try {
            userDetailService.login(null, TEST_STATION_NUMBER, null, TEST_VERIFY_CODE, null, null, TEST_CLIENT_IP_ADDRESS);
            fail("expected bad credentials exception");
        } catch (BadCredentialsException e) {
            // NOOP
        }
        try {
            userDetailService.login(null, TEST_STATION_NUMBER, TEST_ACCESS_CODE, null, null, null, TEST_CLIENT_IP_ADDRESS);
            fail("expected bad credentials exception");
        } catch (BadCredentialsException e) {
            // NOOP
        }
    }

    //     public void testAuthenticateFailsForIncorrectPasswordCase() {
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "KOala");
//
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(new MockAuthenticationDaoUserrod());
//        provider.setUserCache(new MockUserCache());
//
//        try {
//            provider.authenticate(token);
//            fail("Should have thrown BadCredentialsException");
//        } catch (BadCredentialsException expected) {
//            assertTrue(true);
//        }
//    }

    // test for expired credentials
    // test for bad credentials after an expired credentials result

    //    public void testAuthenticateFailsWithEmptyUsername() {
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, "koala");
//
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(new MockAuthenticationDaoUserrod());
//        provider.setUserCache(new MockUserCache());
//
//        try {
//            provider.authenticate(token);
//            fail("Should have thrown BadCredentialsException");
//        } catch (BadCredentialsException expected) {
//            assertTrue(true);
//        }
//    }
//
    public void testAuthenticateFailsWithInvalidPassword() throws IOException {
        expectVistaLinkAccessVerifyConnection(TEST_ACCESS_CODE, "flibberty-floo", TEST_CLIENT_IP_ADDRESS);
        expectRpcAndDefaultThrow(RPC_CONTEXT, GET_USER_INFO_RPC, createParams(TEST_CLIENT_IP_ADDRESS, TEST_APPLICATION_NAME), new PermissionDeniedDataAccessException("", new SecurityAccessVerifyCodePairInvalidException(new VistaLinkFaultException())));
        replay();

        try {
            userDetailService.login(null, TEST_STATION_NUMBER, TEST_ACCESS_CODE, "flibberty-floo", null, null, TEST_CLIENT_IP_ADDRESS);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
            assertTrue(true);
        }
    }

    public void testLogout() {
        VistaLinkVistaUserDetails user = EasyMock.createMock(VistaLinkVistaUserDetails.class);
        expect(user.getDUZ()).andReturn(TEST_DUZ).anyTimes();
        expect(user.getDivision()).andReturn(TEST_STATION_NUMBER).anyTimes();
        expect(user.getSignonLogInternalEntryNumber()).andReturn(TEST_SIGNON_LOG_IEN).anyTimes();
        expect(user.getAccessCode()).andReturn(TEST_ACCESS_CODE).anyTimes();
        expect(user.getVerifyCode()).andReturn(TEST_VERIFY_CODE).anyTimes();

        mockUserCache.removeUserFromCache(TEST_ACCESS_CODE, TEST_VERIFY_CODE);
        EasyMock.expectLastCall();

        expectVistaLinkDuzConnection(TEST_DUZ);
        expectRpcAndReturn(RPC_CONTEXT, LOGOUT_RPC_NAME, createParams(TEST_SIGNON_LOG_IEN), "<foo/>");

        replay();
        EasyMock.replay(user, mockUserCache);

        userDetailService.logout(user);

        verify();
        EasyMock.verify(user, mockUserCache);
    }

    public void testLogin() throws IOException {
        expectVistaLinkAccessVerifyConnection(TEST_ACCESS_CODE, TEST_VERIFY_CODE, TEST_CLIENT_IP_ADDRESS);
        expectRpcAndReturnXmlResource(RPC_CONTEXT, GET_USER_INFO_RPC, createParams(TEST_CLIENT_IP_ADDRESS, TEST_APPLICATION_NAME), "successfulLoginResponse.xml");
        replay();

        VistaLinkVistaUserDetails user = (VistaLinkVistaUserDetails) userDetailService.login(null, TEST_STATION_NUMBER, TEST_ACCESS_CODE, TEST_VERIFY_CODE, null, null, TEST_CLIENT_IP_ADDRESS);

        assertNotNull(user);
        assertEquals(TEST_STATION_NUMBER, user.getDivision());
        assertEquals(TEST_DUZ, user.getDUZ());
        assertEquals(TEST_SIGNON_LOG_IEN, user.getSignonLogInternalEntryNumber());
        assertEquals("Bar,Foo", user.getPersonName());
        assertEquals("Foo Bar", user.getDisplayName());
        assertEquals("BAR", user.getFamilyName());
        assertEquals("FOO", user.getGivenName());
        assertNull(user.getMiddleName());
        assertNull(user.getPrefix());
        assertNull(user.getSuffix());
        assertNull(user.getDegree());

        verify();
    }

    protected void expectVistaLinkAccessVerifyConnection(String accessCode, String verifyCode, String clientIpAddress) {
        try {
            org.easymock.EasyMock.expect(mockConnectionFactory.getConnection(new VistaLinkAccessVerifyConnectionSpec(getStationNumber(), accessCode, verifyCode, clientIpAddress))).andReturn(mockVistaLinkConnection);
        } catch (ResourceException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }
}
