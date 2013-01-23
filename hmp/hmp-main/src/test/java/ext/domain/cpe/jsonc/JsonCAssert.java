package org.osehra.cpe.jsonc;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class JsonCAssert {
    public static void assertError(JsonCResponse jsonc, String code, String message) {
        assertThat(jsonc.getSuccess(), is(false));
        assertThat(jsonc.data, nullValue());
        assertThat(jsonc.error.code, is(code));
        assertThat(jsonc.error.message, is(message));

        assertThat(jsonc.error.errors.size(), is(1));
        assertThat(jsonc.error.errors.get(0).get("code"), is(code));
        assertThat(jsonc.error.errors.get(0).get("message"), is(message));
    }

    public static void assertExceptionError(JsonCResponse jsonc, String code, Exception ex) {
        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));

        assertThat(jsonc.getSuccess(), is(false));
        assertThat(jsonc.data, nullValue());
        assertThat(jsonc.error.code, is(code));
        assertThat(jsonc.error.message, is(ex.getMessage()));

        assertThat(jsonc.error.errors.size(), is(1));
        assertThat(jsonc.error.errors.get(0).get("code"), is(code));
        assertThat(jsonc.error.errors.get(0).get("message"), is(ex.getMessage()));
        assertThat(jsonc.error.errors.get(0).get("exception"), is(ex.getClass().getName()));
        assertThat(jsonc.error.errors.get(0).get("stackTrace"), is(stackTrace.toString()));

        Throwable cause = ex.getCause();
        if (cause != null) {
            assertThat(jsonc.error.errors.get(0).get("causedBy"), is(cause.getClass().getName()));
            assertThat(jsonc.error.errors.get(0).get("causedByMessage"), is(cause.getMessage()));
        }
    }
}
