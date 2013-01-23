package org.osehra.cpe.vista.rpc.conn;

/**
 * ConnectionSpec for opening a connection in order to fetch system info when access/verify codes are not yet known.
 *
 * @see ConnectionFactory
 */
public class AnonymousConnectionSpec implements ConnectionSpec {

    public static final String ANONYMOUS = "ANONYMOUS";

    @Override
    public String toString() {
        return ANONYMOUS;
    }
}
