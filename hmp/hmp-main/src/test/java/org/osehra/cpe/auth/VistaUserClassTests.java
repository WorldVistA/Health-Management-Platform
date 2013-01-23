package org.osehra.cpe.auth;

import org.osehra.cpe.auth.VistaUserClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class VistaUserClassTests {
    @Test
    public void testConstruct() throws Exception {
        VistaUserClass userClass = new VistaUserClass("CLINICAL COORDINATOR");

        assertThat(userClass.getUserClass(), equalTo("CLINICAL COORDINATOR"));
        assertThat(userClass.getAuthority(), equalTo(VistaUserClass.VISTA_USER_CLASS_PREFIX + "CLINICAL_COORDINATOR"));
    }
}
