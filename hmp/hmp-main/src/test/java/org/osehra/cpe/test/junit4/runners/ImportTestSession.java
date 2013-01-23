package org.osehra.cpe.test.junit4.runners;

import org.osehra.cpe.vista.rpc.RpcRequest;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ImportTestSession {
    String connectionUri();

    int rpcTimeout() default RpcRequest.DEFAULT_TIMEOUT;
}
