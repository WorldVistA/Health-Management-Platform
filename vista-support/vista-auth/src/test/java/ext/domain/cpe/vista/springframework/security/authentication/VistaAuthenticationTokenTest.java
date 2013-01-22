package EXT.DOMAIN.cpe.vista.springframework.security.authentication;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;

import static org.junit.Assert.*;

public class VistaAuthenticationTokenTest {

    @Test
    public void testConstruct() {
        VistaAuthenticationToken token = new VistaAuthenticationToken("9F2B", "891", "10VEHU", "VEHU10", "127.0.0.1");
        assertNull(token.getDuz());
        assertEquals("9F2B", token.getVistaId());
        assertEquals("891", token.getDivision());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("VEHU10", token.getVerifyCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());

        assertEquals(VistaAuthenticationToken.UNAUTHENTICATED + "@9F2B;891", token.getPrincipal());
        assertEquals("10VEHU;VEHU10;127.0.0.1", token.getCredentials());
        assertEquals("127.0.0.1", token.getDetails());
        assertFalse(token.isAuthenticated());
    }

    @Test
    public void testAuthenticatedConstruct() {
        VistaUserDetails user = EasyMock.createMock(VistaUserDetails.class);
        EasyMock.expect(user.getVistaId()).andReturn("9F2B").anyTimes();
        EasyMock.expect(user.getDivision()).andReturn("891").anyTimes();
        EasyMock.expect(user.getDUZ()).andReturn("101284").anyTimes();
        EasyMock.replay(user);

        VistaAuthenticationToken token = new VistaAuthenticationToken(user, "10VEHU", "VEHU10", "127.0.0.1", Collections.<GrantedAuthority>emptyList());
        assertEquals(user.getVistaId(), token.getVistaId());
        assertEquals(user.getDUZ(), token.getDuz());
        assertEquals(user.getDivision(), token.getDivision());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("VEHU10", token.getVerifyCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());

        assertSame(user, token.getPrincipal());
        assertEquals("10VEHU;VEHU10;127.0.0.1", token.getCredentials());
        assertEquals("127.0.0.1", token.getDetails());
        assertTrue(token.isAuthenticated());
    }

     @Test
    public void testConstructWithMissingAccessCode() {
        VistaAuthenticationToken token = new VistaAuthenticationToken("9F2B", "891", null, "VEHU10", "127.0.0.1");
        assertNull(token.getCredentials());
        assertEquals("VEHU10", token.getVerifyCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());
        token = new VistaAuthenticationToken("9F2B", "891", "", "VEHU10", "127.0.0.1");
        assertNull(token.getCredentials());
        assertEquals("VEHU10", token.getVerifyCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());
    }

     @Test
    public void testConstructWithMissingVerifyCode() {
        VistaAuthenticationToken token = new VistaAuthenticationToken("9F2B", "891", "10VEHU", null, "127.0.0.1");
        assertNull(token.getCredentials());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());
        token = new VistaAuthenticationToken("9F2B", "891", "10VEHU", "", "127.0.0.1");
        assertNull(token.getCredentials());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("127.0.0.1", token.getRemoteAddress());
    }

     @Test
    public void testConstructWithMissingRemoteAddress() {
        VistaAuthenticationToken token = new VistaAuthenticationToken("9F2B", "891", "10VEHU", "VEHU10", null);
        assertNull(token.getCredentials());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("VEHU10", token.getVerifyCode());
        token = new VistaAuthenticationToken("9F2B", "891", "10VEHU", "VEHU10", "");
        assertNull(token.getCredentials());
        assertEquals("10VEHU", token.getAccessCode());
        assertEquals("VEHU10", token.getVerifyCode());
    }

     @Test
    public void testConstructWithMissingCredentials() {
        VistaAuthenticationToken token = new VistaAuthenticationToken("9F2B", "891", null, null, null);
        assertNull(token.getCredentials());
        assertNull(token.getAccessCode());
        assertNull(token.getVerifyCode());
        assertNull(token.getRemoteAddress());
        token = new VistaAuthenticationToken("9F2B", "891", "", "", "");
        assertNull(token.getCredentials());
        assertNull(token.getAccessCode());
        assertNull(token.getVerifyCode());
        assertNull(token.getRemoteAddress());
    }

     @Test
    public void testSetDetailsIsNoop() {

    }
}
