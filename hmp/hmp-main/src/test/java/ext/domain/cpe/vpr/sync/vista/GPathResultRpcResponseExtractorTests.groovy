package EXT.DOMAIN.cpe.vpr.sync.vista

import groovy.util.slurpersupport.GPathResult
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse

class GPathResultRpcResponseExtractorTests extends GroovyTestCase {
    void testExtractData() {
        GPathResultRpcResponseExtractor extractor = new GPathResultRpcResponseExtractor('bar')

        List<GPathResult> nodes = extractor.extractData(new RpcResponse('''
<foo>
    <bar baz="1">one</bar>
    <bar baz="2">two</bar>
    <bar baz="3">three</bar>
</foo>
'''))
        assertEquals 3, nodes.size()
        assertEquals '1', nodes[0].@baz.toString()
        assertEquals 'one', nodes[0].text()
        assertEquals '2', nodes[1].@baz.toString()
        assertEquals 'two', nodes[1].text()
        assertEquals '3', nodes[2].@baz.toString()
        assertEquals 'three', nodes[2].text()
    }
}
