package EXT.DOMAIN.cpe.vista.springframework.security.userdetails.memory;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class VistaUserMapTest {

    @Test
    public void testConstruct() {
        VistaUserMap userMap = new VistaUserMap();
        assertEquals(0, userMap.getUserCount());
    }

    @Test
    public void testAddUser() {
        VistaUserMap userMap = new VistaUserMap();
        VistaUserDetails u = createUser("12345", "9F2B", "982", "FOO", "BAR");
        userMap.addUser(u);
        assertEquals(1, userMap.getUserCount());
        assertSame(u, userMap.getUser("9F2B", "982", "FOO", "BAR"));
    }

    private VistaUserDetails createUser(String duz, String vistaId, String division, String access, String verify) {
        VistaUserDetails user = createMock(VistaUserDetails.class);
        expect(user.getDUZ()).andReturn(duz).anyTimes();
        expect(user.getVistaId()).andReturn(vistaId).anyTimes();
        expect(user.getDivision()).andReturn(division).anyTimes();
        expect(user.getPassword()).andReturn(access + ";" + verify).anyTimes();
        EasyMock.replay(user);
        return user;
    }
}
