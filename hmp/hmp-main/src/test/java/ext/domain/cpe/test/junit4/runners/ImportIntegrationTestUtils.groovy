package EXT.DOMAIN.cpe.test.junit4.runners

import EXT.DOMAIN.cpe.vista.rpc.RpcTemplate
import EXT.DOMAIN.cpe.vpr.Patient

import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import EXT.DOMAIN.cpe.vpr.sync.vista.json.PatientImporter
import static EXT.DOMAIN.cpe.vpr.sync.vista.SynchronizationRpcConstants.*
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils

import com.fasterxml.jackson.databind.JsonNode
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants

class ImportIntegrationTestUtils {
    static final protected String EXAMPLE_CONNECTION_URI = 'vrpcb://{stationNumber}:{accessCode};{verifyCode}@{host}:{port}'

    static List<VistaDataChunk> fetchChunks(RpcTemplate rpcTemplate, String connectionUri, String dfn, String domain, Patient pt = null) {
        String uri = "${connectionUri}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}"
        JsonNode response = rpcTemplate.executeForJson(uri, [patientId: dfn, domain: domain])
        if (response == null) return []

        String division = RpcUriUtils.extractDivision(uri);
        List<VistaDataChunk> chunks = []
        JsonNode items = response.path("data").path("items")
        for (int i = 0; i < items.size(); i++) {
            chunks.add(new VistaDataChunk(patient: pt, localPatientId: dfn, domain: domain, rpcUri: uri.toString(), params: createMockJobParameters(division, dfn, pt), itemIndex: i, itemCount: items.size(), json: items.get(i)))
        }
        return chunks
    }

    static Patient fetchPatient(RpcTemplate rpcTemplate, String uri, String dfn) {
        try {
            PatientImporter patientImporter = new PatientImporter()

            List<VistaDataChunk> chunks = fetchChunks(rpcTemplate, uri, dfn, "patient")
            assert chunks
            Patient pt = patientImporter.convert(chunks[0])
            return pt
        } catch (Throwable t) {
            throw t
        }
    }

    private static Map createMockJobParameters(String division, String dfn, Patient pt = null) {
        Map params = [:]
        params[SyncMessageConstants.DIVISION] = division
        params[SyncMessageConstants.PATIENT_DFN] = dfn
        params[SyncMessageConstants.PATIENT_ID] = pt?.pid
        params[SyncMessageConstants.PATIENT_ICN] = pt?.icn
        return params
    }
}
