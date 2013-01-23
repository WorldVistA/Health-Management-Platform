package org.osehra.cpe.rpc

import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.functors.InstantiateFactory


class RpcCommand {
    String division
    String accessCode
    String verifyCode
    String context
    String name
    List params = LazyList.decorate(new ArrayList(['']), new InstantiateFactory(String.class))
    String format = 'text'

    static constraints = {
        division(blank: false)
        accessCode(blank: false)
        verifyCode(blank: false)
        context(blank: true)
        name(blank: false)
    }
}
