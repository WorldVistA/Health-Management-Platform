package org.osehra.cpe.vpr.sync.vista

import org.osehra.cpe.vpr.Patient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import static org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants.*

class MockVistaDataChunks {

    public static final String VISTA_ID = "F484";
    public static final String ICN = "12345"
    public static final String DFN = "229"
    public static final String DIVISION = "500"

    static VistaDataChunk createFromJson(JsonNode json, Patient pt = null, String domain) {
       return new VistaDataChunk(systemId: VISTA_ID, localPatientId: DFN, json: json, domain: domain, patient: pt, patientId: pt?.getPid(), itemIndex: 0, itemCount: 1, params: getParams(), rpcUri: "vrpcb://${VISTA_ID}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}")
    }

    static VistaDataChunk createFromJson(JsonNode json, String systemId, String dfn, String domain) {
       return new VistaDataChunk(systemId: systemId, localPatientId: dfn, json: json, domain: domain, itemIndex: 0, itemCount: 1, params: getParams(), rpcUri: "vrpcb://${VISTA_ID}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}")
    }

    static VistaDataChunk createFromJson(String json, Patient pt = null, String domain) {
        return createFromJson(new ObjectMapper().readTree(json), pt, domain)
    }

    static VistaDataChunk createFromJson(String json, String systemId, String dfn, String domain) {
        return createFromJson(new ObjectMapper().readTree(json), systemId, dfn, domain)
    }

    static VistaDataChunk createFromJson(InputStream stream, Patient pt = null, String domain) {
        return createFromJson(new ObjectMapper().readTree(stream), pt, domain)
    }

    static VistaDataChunk createFromJson(InputStream stream, String systemId, String dfn, String domain) {
        return createFromJson(new ObjectMapper().readTree(stream), systemId, dfn, domain)
    }

	static List<VistaDataChunk> createListFromJson(String systemId, Patient pt, String domain, int num) {
		Random random = new Random(System.currentTimeMillis());
		List<VistaDataChunk> items = new ArrayList<VistaDataChunk>();
		for (int i = 0; i < num; i++) {
			String json = "{\"localId\":\"${random.nextLong()}\"}"
			VistaDataChunk item = new VistaDataChunk(systemId: VISTA_ID,
					localPatientId: DFN,
					json:new ObjectMapper().readTree(json),
					patient: pt,
					domain: domain,
					itemIndex: i,
					itemCount: num,
					params: getParams(),
					rpcUri: "vrpcb://${VISTA_ID}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}")
			items << item
		}
		return items;
	}

    private static Map getParams() {
        Map params = [:]
        return params
    }
}
