package EXT.DOMAIN.cpe.vista.springframework.security.authentication;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetailsService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.NullUserCache;

import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class VistaAuthenticationProviderTest {

    private static final String TEST_REMOTE_ADDRESS = "192.168.0.1";
    private static final String TEST_VISTA_ID = "9F2B";
    private static final String TEST_DIVISION = "663";
    private static final String TEST_ACCESS = "10VEHU";
    private static final String TEST_VERIFY = "VEHU10";
    private static final String TEST_DUZ = "12345";

    private VistaUserDetails user;
    private VistaAuthenticationProviderTest.MockUserCache mockCache;

    private VistaUserDetailsService mockUserDetailService;
    private VistaAuthenticationProvider provider;

    @Before
    public void setUp() throws Exception {
        user = createUser(TEST_VISTA_ID, TEST_DIVISION, TEST_DUZ, TEST_ACCESS + ";" + TEST_VERIFY + ";" + TEST_REMOTE_ADDRESS, true, true, true, true, new SimpleGrantedAuthority("ROLE_ONE"), new SimpleGrantedAuthority("ROLE_TWO"));

        mockUserDetailService = EasyMock.createMock(VistaUserDetailsService.class);

        provider = new VistaAuthenticationProvider();
        provider.setUserDetailsService(mockUserDetailService);
        mockCache = new MockUserCache();
        provider.setUserCache(mockCache);
        provider.afterPropertiesSet();
    }

    private static class FooAnswer implements IAnswer<Collection<? extends GrantedAuthority>> {

        private Collection<GrantedAuthority> authorities;

        private FooAnswer(Collection<GrantedAuthority> authorities) {
            this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> answer() throws Throwable {
            return authorities;
        }
    }

    private interface GrantedAuthorityCollection extends Collection {

    }

    protected VistaUserDetails createUser(String vistaId, String division, String duz, String password, boolean nonExpired, boolean nonLocked, boolean credentialsNonExpired, boolean enabled, GrantedAuthority... authorities) {
        VistaUserDetails user = EasyMock.createMock(VistaUserDetails.class);
        expect(user.getVistaId()).andReturn(vistaId).anyTimes();
        expect(user.getDivision()).andReturn(division).anyTimes();
        expect(user.getDUZ()).andReturn(duz).anyTimes();
        expect(user.isAccountNonExpired()).andReturn(nonExpired).anyTimes();
        expect(user.isAccountNonLocked()).andReturn(nonLocked).anyTimes();
        expect(user.isCredentialsNonExpired()).andReturn(credentialsNonExpired).anyTimes();
        expect(user.isEnabled()).andReturn(enabled).anyTimes();
        expect(user.getUsername()).andReturn(duz + "@" + vistaId + ";" + division).anyTimes();
        expect(user.getPassword()).andReturn(password).anyTimes();
        expect(user.getAuthorities()).andReturn((Collection) Arrays.asList(authorities)).anyTimes();
        replay(user);
        return user;
    }

    @Test
    public void testSupports() {
        VistaAuthenticationProvider provider = new VistaAuthenticationProvider();

        assertTrue(provider.supports(VistaAuthenticationToken.class));
        assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
//        assertFalse(provider.supports(X509AuthenticationToken.class));
    }

    @Test
    public void testReceivedBadCredentialsWhenCredentialsNotProvided() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, null, null, null, null)).andThrow(new BadCredentialsException("missing credentials"));
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, null, null);
        try {
            provider.authenticate(token);
            fail("Expected BadCredenialsException");
        } catch (BadCredentialsException expected) {
            // NOOP
        }

        verify(mockUserDetailService);
    }

    @Test
    public void testAuthenticateFailsIfAccountExpired() {
        user = createUser("54321.123456789", TEST_DIVISION, TEST_DUZ, null, false, true, true, true);
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        try {
            provider.authenticate(token);
            fail("Should have thrown AccountExpiredException");
        } catch (AccountExpiredException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticateFailsIfAccountLocked() {
        user = createUser("54321.123456789", TEST_DIVISION, TEST_DUZ, null, true, false, true, true);
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        try {
            provider.authenticate(token);
            fail("Should have thrown LockedException");
        } catch (LockedException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticateFailsIfCredentialsExpired() {
        user = createUser("54321.123456789", TEST_DIVISION, TEST_DUZ, null, true, true, false, true);
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);
        try {
            provider.authenticate(token);
            fail("Expected CredentialsExpiredException");
        } catch (CredentialsExpiredException expected) {
            // NOOP
        }

        verify(mockUserDetailService);
    }

    @Test
    public void testAuthenticateFailsIfUserDisabled() {
        user = createUser("54321.123456789", TEST_DIVISION, TEST_DUZ, null, true, true, true, false);
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        try {
            provider.authenticate(token);
            fail("Should have thrown DisabledException");
        } catch (DisabledException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticateFailsWhenAuthenticationDaoHasBackendFailure() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andThrow(new DataRetrievalFailureException("This mock simulator is designed to fail"));
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);
        try {
            provider.authenticate(token);
            fail("Should have thrown AuthenticationServiceException");
        } catch (AuthenticationServiceException expected) {
            assertTrue(true);
        }

        verify(mockUserDetailService);
    }

    @Test
    public void testAuthenticates() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);
        Authentication result = provider.authenticate(token);

        if (!(result instanceof VistaAuthenticationToken)) {
            fail("Should have returned instance of VistaAuthenticationToken");
        }
        assertNotSame(token, result);

        VistaAuthenticationToken castResult = (VistaAuthenticationToken) result;
        assertTrue(VistaUserDetails.class.isAssignableFrom(castResult.getPrincipal().getClass()));
        assertEquals(TEST_VISTA_ID, castResult.getVistaId());
        assertEquals(TEST_ACCESS, castResult.getAccessCode());
        assertEquals(TEST_VERIFY, castResult.getVerifyCode());
        assertEquals("ROLE_ONE", new ArrayList<GrantedAuthority>(castResult.getAuthorities()).get(0).getAuthority());
        assertEquals("ROLE_TWO", new ArrayList<GrantedAuthority>(castResult.getAuthorities()).get(1).getAuthority());
        assertEquals(TEST_REMOTE_ADDRESS, castResult.getDetails());

        verify(mockUserDetailService);
    }

    @Test
    public void testAuthenticatesASecondTime() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        Authentication result = provider.authenticate(token);

        if (!(result instanceof VistaAuthenticationToken)) {
            fail("Should have returned instance of VistaAuthenticationToken");
        }

        // Now try to authenticate with the previous result (with its UserDetails)
        Authentication result2 = provider.authenticate(result);

        if (!(result2 instanceof VistaAuthenticationToken)) {
            fail("Should have returned instance of VistaAuthenticationToken");
        }

        assertNotSame(result, result2);
        assertEquals(result.getCredentials(), result2.getCredentials());
    }

    @Test
    public void testDetectsNullBeingReturnedFromAuthenticationDao() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(null);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        try {
            provider.authenticate(token);
            fail("Should have thrown AuthenticationServiceException");
        } catch (AuthenticationServiceException expected) {
            assertEquals("VistaUserDetailsService returned null, which is an interface contract violation",
                    expected.getMessage());
        }
    }

    @Test
    public void testGoesBackToAuthenticationDaoToObtainLatestVerifyCodeIfCachedVerifyCodeSeemsIncorrect() {
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        VistaAuthenticationToken token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, TEST_VERIFY, TEST_REMOTE_ADDRESS);

        // This will work, as password still "koala"
        provider.authenticate(token);

        // Check "12345@663 = 10VEHU;VEHU10;192.168.0.1" ended up in the cache
        assertEquals(TEST_ACCESS + ";" + TEST_VERIFY + ";" + TEST_REMOTE_ADDRESS, mockCache.getUserFromCache(TEST_DUZ + "@" + TEST_VISTA_ID + ";" + TEST_DIVISION).getPassword());
        verify(mockUserDetailService);

        // Now change the password the AuthenticationDao will return
        EasyMock.reset(mockUserDetailService);
        user = createUser(TEST_VISTA_ID, TEST_DIVISION, TEST_DUZ, TEST_ACCESS + ";easternLongNeckTurtle;" + TEST_REMOTE_ADDRESS, true, true, true, true, new SimpleGrantedAuthority("ROLE_ONE"), new SimpleGrantedAuthority("ROLE_TWO"));
        expect(mockUserDetailService.login(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, "easternLongNeckTurtle", null, null, TEST_REMOTE_ADDRESS)).andReturn(user);
        replay(mockUserDetailService);

        // Now try authentication again, with the new password
        token = new VistaAuthenticationToken(TEST_VISTA_ID, TEST_DIVISION, TEST_ACCESS, "easternLongNeckTurtle", TEST_REMOTE_ADDRESS);
        provider.authenticate(token);

        // To get this far, the new password was accepted
        // Check the cache was updated
        assertEquals("10VEHU;easternLongNeckTurtle;192.168.0.1", mockCache.getUserFromCache(TEST_DUZ + "@" + TEST_VISTA_ID + ";" + TEST_DIVISION).getPassword());
    }

    @Test
    public void testStartupFailsIfNoVistaUserDetailsService()
            throws Exception {
        VistaAuthenticationProvider provider = new VistaAuthenticationProvider();

        try {
            provider.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testStartupFailsIfNoUserCacheSet() throws Exception {
        VistaAuthenticationProvider provider = new VistaAuthenticationProvider();
        provider.setUserDetailsService(mockUserDetailService);
        assertEquals(NullUserCache.class, provider.getUserCache().getClass());
        provider.setUserCache(null);

        try {
            provider.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testStartupSuccess() throws Exception {
        VistaAuthenticationProvider provider = new VistaAuthenticationProvider();
        provider.setUserDetailsService(mockUserDetailService);
        provider.setUserCache(new MockUserCache());
        assertSame(mockUserDetailService, provider.getUserDetailsService());
        provider.afterPropertiesSet();
    }

    private class MockUserCache implements UserCache {
        private Map cache = new HashMap();

        public UserDetails getUserFromCache(String username) {
            return (UserDetails) cache.get(username);
        }

        public void putUserInCache(UserDetails user) {
            cache.put(user.getUsername(), user);
        }

        public void removeUserFromCache(String username) {
        }
    }
}
