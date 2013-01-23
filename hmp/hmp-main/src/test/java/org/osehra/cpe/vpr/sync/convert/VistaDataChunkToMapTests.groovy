package org.osehra.cpe.vpr.sync.convert

import com.fasterxml.jackson.databind.ObjectMapper

import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk

import org.osehra.cpe.vista.util.RpcUriUtils

import org.osehra.cpe.vista.rpc.RpcRequest
import org.osehra.cpe.vpr.sync.SyncMessageConstants

class VistaDataChunkToMapTests extends GroovyTestCase {
	VistaDataChunkToMap c
	VistaDataChunk fragment
	
	@Override
	protected void setUp() throws Exception {
	    c = new VistaDataChunkToMap()

        fragment = new VistaDataChunk(itemIndex: 7, itemCount: 9)
        fragment.systemId = 'ABCDEF'
        fragment.localPatientId = "229"
        fragment.patient = new Patient(id: 42, icn: "12345")
        fragment.rpcUri = RpcUriUtils.toURI(new RpcRequest("FOO/BAR", ['arg1', 'arg2'])).toString()
        fragment.params = [foo: 'bar', baz: 'spaz']
		super.setUp();
	}

	void testConvertJson(){
		fragment.domain = 'foo'
		fragment.json = new ObjectMapper().readTree('{"foo":"bar"}')
		Map m = c.convert(fragment)
		
        assertNotNull m
        assertEquals m[SyncMessageConstants.VISTA_ID], fragment.systemId
        assertEquals m[SyncMessageConstants.PATIENT_DFN], fragment.localPatientId
        assertEquals m[SyncMessageConstants.PATIENT_ID] , fragment.patient.getPid()
        assertEquals m[SyncMessageConstants.PATIENT_ICN], fragment.patient.icn
        assertEquals m[SyncMessageConstants.RPC_URI], fragment.rpcUri
        assertEquals m[SyncMessageConstants.RPC_ITEM_INDEX], fragment.itemIndex
        assertEquals m[SyncMessageConstants.RPC_ITEM_COUNT], fragment.itemCount
        assertEquals m[SyncMessageConstants.RPC_ITEM_CONTENT], fragment.content
        assertEquals m[SyncMessageConstants.VPR_DOMAIN], fragment.domain
	}
}
