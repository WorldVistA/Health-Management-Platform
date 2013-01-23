package org.osehra.cpe.vpr.sync.vista

import groovy.util.slurpersupport.GPathResult
import org.osehra.cpe.vista.rpc.RpcResponseExtractor
import org.osehra.cpe.vista.rpc.RpcResponse

/**
 * Uses XmlSlurper to parse rpc response into a GPathResult.
 */
class XmlSlurperRpcResponseExtractor implements RpcResponseExtractor<GPathResult> {

    GPathResult extractData(RpcResponse response) {
        return new XmlSlurper(false, false).parseText(response.toString())
    }
}
