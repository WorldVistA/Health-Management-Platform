package org.osehra.cpe.vpr.sync.convert;

import org.osehra.cpe.vpr.SyncError;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SyncErrorToMapTests {

    @Test
    public void testConvert() throws Exception {
        SyncErrorToMap converter = new SyncErrorToMap();

        SyncError error = new SyncError();
        error.setItem("foo");
        error.setMessage("bar");
        error.setStackTrace("baz");
        error.setPid("23");

        Map msg = converter.convert(error);

        assertThat(msg.get(SyncMessageConstants.EXCEPTION_NAME).toString(), is("foo"));
        assertThat(msg.get(SyncMessageConstants.EXCEPTION_MESSAGE).toString(), is("bar"));
        assertThat(msg.get(SyncMessageConstants.EXCEPTION_STACK_TRACE).toString(), is("baz"));
        assertThat(msg.get(SyncMessageConstants.PATIENT_ID).toString(), is("23"));
    }
}
