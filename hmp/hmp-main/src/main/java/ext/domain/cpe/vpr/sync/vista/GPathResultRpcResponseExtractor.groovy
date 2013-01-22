package EXT.DOMAIN.cpe.vpr.sync.vista

import groovy.util.slurpersupport.GPathResult
import EXT.DOMAIN.cpe.vista.rpc.RpcResponseExtractor
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse

@Deprecated
class GPathResultRpcResponseExtractor implements RpcResponseExtractor<List<GPathResult>> {

    private String rootElementName

    GPathResultRpcResponseExtractor(String rootElementName) {
        this.rootElementName = rootElementName
    }

    List<GPathResult> extractData(RpcResponse response) {
        def input = new XmlSlurper(false, false).parseText(response.toString())
        List<GPathResult> fragments = input.depthFirst().findAll { it.name() == rootElementName }
        return fragments
    }
}
