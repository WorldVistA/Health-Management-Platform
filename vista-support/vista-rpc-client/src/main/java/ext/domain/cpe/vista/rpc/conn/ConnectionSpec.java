package org.osehra.cpe.vista.rpc.conn;

import java.io.Serializable;

/**
 * <code>ConnectionSpec</code> is used by an application component to pass broker connection request-specific properties to the
 * {@link ConnectionFactory#getConnection(org.osehra.cpe.vista.rpc.RpcHost, ConnectionSpec)} method.
 *
 * @see ConnectionFactory
 */
public interface ConnectionSpec extends Serializable {
    boolean equals(Object o);

    int hashCode();
}
