package org.osehra.cpe.auth;

import org.osehra.cpe.vista.rpc.RpcHost;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class HmpUserTests {

    private final RpcHost HOST = new RpcHost("localhost");
    private final VistaSecurityKey FRED = new VistaSecurityKey("FRED");
    private final VistaSecurityKey WILMA = new VistaSecurityKey("WILMA");
    private final VistaUserClass BETTY = new VistaUserClass("BETTY");
    private final VistaUserClass BARNEY = new VistaUserClass("BARNEY");

    public static final String KEY_WITH_SPACES_STRING = "A KEY WITH SPACES IN IT";

    @Test
    public void testConstruct() throws Exception {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(FRED);
        authorities.add(WILMA);
        authorities.add(BETTY);
        authorities.add(BARNEY);

        HmpUser u = createUser(authorities, Collections.<TeamPosition>emptySet());

        assertThat(u.getHost(), sameInstance(HOST));
        assertThat(u.getVistaId(), equalTo("ABCDEF"));
        assertThat(u.getDivision(), equalTo("500"));
        assertThat(u.getDUZ(), equalTo("1234"));
        assertThat(u.getAccessCode(), equalTo("foo"));
        assertThat(u.getVerifyCode(), equalTo("bar"));
        assertThat(u.getDisplayName(), equalTo("BAR,FOO"));
        assertThat(u.getOrderingRole(), equalTo(OrderingRole.NONE));
        assertThat(u.getTimeoutSeconds(), equalTo(654321));
        assertThat(u.getTimeoutCountdownSeconds(), equalTo(40));

        assertThat(u.getSecurityKeys(), hasItems(FRED, WILMA));

        assertThat(u.getUserClasses(), hasItems(BETTY, BARNEY));
    }

    @Test
    public void testConstructWithOrderingRole() throws Exception {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(OrderingRole.DOCTOR);

        HmpUser u = createUser(authorities, Collections.<TeamPosition>emptySet());
        assertThat(u.getOrderingRole(), equalTo(OrderingRole.DOCTOR));
    }

    @Test
    public void testHasAuthority() throws Exception {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(FRED);
        authorities.add(BARNEY);

        HmpUser u = createUser(authorities, Collections.<TeamPosition>emptySet());
        assertThat(u.hasAuthority(VistaSecurityKey.VISTA_KEY_PREFIX + "FRED"), is(true));
        assertThat(u.hasAuthority(VistaSecurityKey.VISTA_KEY_PREFIX + "WILMA"), is(false));
        assertThat(u.hasAuthority(VistaUserClass.VISTA_USER_CLASS_PREFIX + "BARNEY"), is(true));
        assertThat(u.hasAuthority(VistaUserClass.VISTA_USER_CLASS_PREFIX + "BETTY"), is(false));
    }

    @Test
    public void testHasVistaKey() throws Exception {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(FRED);
        authorities.add(new VistaSecurityKey(KEY_WITH_SPACES_STRING));

        HmpUser u = createUser(authorities, Collections.<TeamPosition>emptySet());
        assertThat(u.hasVistaKey("FRED"), is(true));
        assertThat(u.hasVistaKey("WILMA"), is(false));
        assertThat(u.hasVistaKey(KEY_WITH_SPACES_STRING), is(true));
        assertThat(u.hasVistaKey(KEY_WITH_SPACES_STRING.replace(' ', '_')), is(false));

        assertThat(u.hasVistaKey(VistaSecurityKey.VISTA_KEY_PREFIX + "FRED"), is(true));
        assertThat(u.hasVistaKey(VistaSecurityKey.VISTA_KEY_PREFIX + "WILMA"), is(false));
        assertThat(u.hasVistaKey(VistaSecurityKey.VISTA_KEY_PREFIX + KEY_WITH_SPACES_STRING.replace(' ', '_')), is(true));
    }

    private HmpUser createUser(Collection<GrantedAuthority> authorities, Collection<TeamPosition> positions) {
        return new HmpUser(HOST,
                "ABCDEF",
                "500",
                "1234",
                "foo",
                "bar",
                "BAR,FOO",
                true,
                true,
                true,
                true,
                654321,
                40,
                authorities,
                positions);
    }
}
