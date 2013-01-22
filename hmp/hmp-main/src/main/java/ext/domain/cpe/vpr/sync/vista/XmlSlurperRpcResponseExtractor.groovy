package EXT.DOMAIN.cpe.vpr.sync.vista

import groovy.util.slurpersupport.GPathResult
import EXT.DOMAIN.cpe.vista.rpc.RpcResponseExtractor
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse

/**
 * Uses XmlSlurper to parse rpc response into a GPathResult.
 */
class XmlSlurperRpcResponseExtractor implements RpcResponseExtractor<GPathResult> {

    GPathResult extractData(RpcResponse response) {
        return new XmlSlurper(false, false).parseText(response.toString())
    }
}
