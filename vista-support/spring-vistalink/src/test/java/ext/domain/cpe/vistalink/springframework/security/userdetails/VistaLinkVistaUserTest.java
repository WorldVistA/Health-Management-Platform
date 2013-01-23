package org.osehra.cpe.vistalink.springframework.security.userdetails;

import junit.framework.TestCase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

public class VistaLinkVistaUserTest extends TestCase {
    private VistaLinkVistaUser user;

    protected void setUp() throws Exception {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_BARNEY"));
        authorities.add(new SimpleGrantedAuthority("ROLE_FRED"));
        user = new VistaLinkVistaUser("1221.98897654", "982", "12345", "10BAZ", "BAZ10", "BAR,FOO", "Foo Bar", "FOO", null, "BAR", "Mr.", null, "MD", true, true, true, true, authorities);
    }

    public void testConstruct() {
        assertEquals("1221.98897654", user.getSignonLogInternalEntryNumber());
        assertEquals("982", user.getDivision());
        assertEquals("12345", user.getDUZ());
        assertEquals("BAR,FOO", user.getPersonName());
        assertEquals("Foo Bar", user.getDisplayName());
        assertEquals("FOO", user.getGivenName());
        assertEquals("BAR", user.getFamilyName());
        assertNull(user.getMiddleName());
        assertEquals("Mr.", user.getPrefix());
        assertNull(user.getSuffix());
        assertEquals("MD", user.getDegree());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertEquals(2, user.getAuthorities().size());
    }

    public void testUsername() {
        assertEquals("12345@982", user.getUsername());
    }

    public void testPassword() {
        assertEquals("10BAZ;BAZ10", user.getPassword());
    }
}
