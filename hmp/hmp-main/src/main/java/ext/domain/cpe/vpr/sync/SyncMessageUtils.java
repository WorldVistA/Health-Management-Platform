package org.osehra.cpe.vpr.sync;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.*;

public class SyncMessageUtils {

    public static Map createErrorMessage(Map msg, Throwable t) {
        Map error = new HashMap(msg);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        error.put(EXCEPTION_NAME, getExceptionName(t));
        error.put(EXCEPTION_MESSAGE, getExceptionMessage(t));
        error.put(EXCEPTION_STACK_TRACE, sw.toString());
        return error;
    }

    private static String getExceptionName(Throwable t) {
        if (t.getCause() != null) {
            return t.getCause().getClass().getName();
        } else {
           return t.getClass().getName();
        }
    }

    private static String getExceptionMessage(Throwable t) {
        if (t.getCause() != null) {
            return t.getCause().getMessage();
        } else {
            return t.getMessage();
        }
    }
}
