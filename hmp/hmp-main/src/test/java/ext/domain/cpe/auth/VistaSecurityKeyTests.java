package org.osehra.cpe.auth;

import org.osehra.cpe.auth.VistaSecurityKey;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class VistaSecurityKeyTests {
    @Test
    public void testConstruct() throws Exception {
        VistaSecurityKey key = new VistaSecurityKey("GMV MANAGER");
        assertThat(key.getKey(), equalTo("GMV MANAGER"));
        assertThat(key.getAuthority(), equalTo(VistaSecurityKey.VISTA_KEY_PREFIX + "GMV_MANAGER"));
    }
}
