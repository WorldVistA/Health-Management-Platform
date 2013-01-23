package org.osehra.cpe.vpr.sync.vista

import com.fasterxml.jackson.databind.JsonNode
import org.osehra.cpe.vista.rpc.conn.Connection
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.PatientFacility
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.osehra.cpe.vpr.sync.UnknownPatientException
import org.perf4j.StopWatch
import org.perf4j.slf4j.Slf4JStopWatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.stereotype.Service
import org.osehra.cpe.vista.rpc.*

import static org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants.*

@Service
class VistaPatientDataService implements IVistaPatientDataService {

    static final Logger LOG = LoggerFactory.getLogger(VistaPatientDataService)

    static transactional = false

    @Autowired
    RpcOperations synchronizationRpcTemplate

    @Autowired
    IPatientDAO patientDao

    String fetchVprVersion(String vistaId) {
        return synchronizationRpcTemplate.executeForString("vrpcb://${vistaId}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_DATA_VERSION}")
    }

    VistaDataChunk fetchPatientDemographicsWithDfn(String vistaId, String ptDfn) {
        return fetchPatientDemographics(vistaId, ptDfn, false)
    }

    VistaDataChunk fetchPatientDemographicsWithIcn(String vistaId, String ptIcn) {
        return fetchPatientDemographics(vistaId, ptIcn, true)
    }

    private VistaDataChunk fetchPatientDemographics(String vistaId, String pid, boolean isIcn) {
        StopWatch timer = new Slf4JStopWatch()
        try {
            RpcResponse response = synchronizationRpcTemplate.execute("vrpcb://${vistaId}/${VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}".toString(), [patientId: isIcn ? ";" + pid : pid, domain: 'patient'])
            JsonNode json = new JacksonRpcResponseExtractor().extractData(response)
            JsonNode patientJsonNode = json.path("data").path("items").path(0)
            if (patientJsonNode.isNull()) throw new DataRetrievalFailureException("missing 'data.items[0]' node in JSON RPC response")
            VistaDataChunk patientFragment = createVistaDataChunk(vistaId, response.getRequestUri(), patientJsonNode, "patient", 0, 1, null, getProcessorParams(vistaId, pid, isIcn));//createVistaXmlFragment(vistaId, response.getRequestUri(), patientElements[0], 0, 1, null, getProcessorParams(vistaId, pid, isIcn));
            patientFragment.params.put(SyncMessageConstants.DIVISION, response.getDivision())
            patientFragment.params.put(SyncMessageConstants.DIVISION_NAME, response.getDivisionName())

            if (!isIcn)
                patientFragment.localPatientId = pid

            return patientFragment
        } catch (Throwable t) {
            timer.stop("vpr.fetch.patient.failure", t)
            throw t
        } finally {
            timer.stop("vpr.fetch.patient")
        }
    }


    List<VistaDataChunk> fetchDomainChunks(String vistaId, Patient pt, String domain, boolean includeBody, String category) {
        StopWatch timer = new Slf4JStopWatch();
        RpcResponse response = null;
        List<VistaDataChunk> chunks = null
        try {
            String pid = getPid(vistaId, pt)
            String uri = "vrpcb://${vistaId}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}"
            Map params = [patientId: pid, domain: domain, text: (includeBody ? 1 : 0)]
            if (category) {
                params.category = category;
            }

            response = synchronizationRpcTemplate.execute(uri, params)
            JsonNode jsonResponse = new JacksonRpcResponseExtractor().extractData(response)

            chunks = createVistaDataChunks(vistaId, response.getRequestUri(), jsonResponse, domain, pt, getProcessorParams(vistaId, pid, pt.icn != null))
            return chunks
        } finally {
            timer.stop("vpr.fetch." + domain + ".total")
            if (chunks) {
                // calculate fetch time per item and log
                long fetchTimePerItem = timer.getElapsedTime() / chunks.size();
                StopWatch stopWatch = new StopWatch(timer.getStartTime(), fetchTimePerItem, "vpr.fetch." + domain, null)
                LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME).info(stopWatch.toString())
            }
        }
    }

    private String getPid(String vistaId, Patient pt) {
        String pid
        if (!pt.icn) {
            Collection<PatientFacility> facilities = pt.facilities.findAll { PatientFacility f -> f.systemId == vistaId }
            assert !facilities.isEmpty(), "patient ${pt.id} doesn't have a local patient id in ${vistaId}"
            pid = facilities.find { it.localPatientId }?.localPatientId
            assert pid, "patient ${pt.id} doesn't have a local patient id in ${vistaId}"
        } else {
            pid = ";${pt.icn}"
        }
        return pid
    }

    VprUpdateData fetchUpdates(String vistaId, String vprId, String lastUpdate) {
        if (lastUpdate == null) lastUpdate = ''

        String uri = "vrpcb://${vistaId}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${VPR_GET_VISTA_DATA_JSON}"
        RpcRequest request = new RpcRequest(uri, [domain: "new", id: lastUpdate, text: "1", systemID: vprId])
        request.setTimeout(3)
        RpcResponse response = synchronizationRpcTemplate.execute(request);
        JsonNode json = new JacksonRpcResponseExtractor().extractData(response)
        JsonNode dataNode = json.path("data")
        JsonNode itemsNode = dataNode.path("items")

        String updateValue = dataNode.path("lastUpdate").textValue()
        VprUpdateData updates = new VprUpdateData(lastUpdate: updateValue)
        for (JsonNode patientNode : itemsNode) {
            try {
                String icn = patientNode.path("patientIcn")?.intValue()
                String dfn = patientNode.path("patientDfn")?.intValue()
                Patient patient = patientDao.findByLocalID(vistaId, dfn)
                if (patient){
	                // inserts/updates
	                JsonNode domainsNode = patientNode.path("domains")
	                for (JsonNode domainNode : domainsNode) {
	                    String domainName = domainNode.path("domainName").textValue()
	                    JsonNode item = domainNode.path("items")
	                    for (int c = 0; c < item?.size(); c++) {
	                        JsonNode value = item?.get(c);
	                        updates.chunks << createVistaDataChunk(vistaId, response.getRequestUri(), value, domainName, c, item.size(), patient, getProcessorParams(vistaId, dfn, false))
	                    }
	                }
	
	                // deletes
	                JsonNode deletesNode = patientNode.path("deletes")
	                if (!deletesNode.isMissingNode()) {
	                    for (JsonNode deleteNode : deletesNode) {
	                        String uid = deleteNode.get("uid").textValue()
	                        updates.uidsToDelete << uid
	                    }
	                }
                }else{
					//Log the message instead of sending exception to the error trap
				    LOG.warn(sprintf(UnknownPatientException.MESSAGE,[dfn,vistaId]))
                }
            } catch (Exception e) {
                updates.exceptions << e
            }
        }

        updates.chunks = updates.chunks.flatten()
        LOG.debug("fetched ${updates.chunks?.size()} JSON fragments for update ${vprId}")
        return updates
    }

    private List<VistaDataChunk> createVistaDataChunks(String vistaId, String rpcUri, JsonNode jsonResponse, String domain, Patient pt, Map processorParams = [:]) {
        JsonNode results = jsonResponse.path("data").path("items");
        if (results.isNull()) throw new DataRetrievalFailureException("missing 'data.items' node in JSON RPC response")
        List<VistaDataChunk> fragments = []
        for (int i = 0; i < results.size(); i++) {
            JsonNode item = results.get(i);
            fragments << createVistaDataChunk(vistaId, rpcUri, item, domain, i, results.size(), pt, processorParams)
        }
        return fragments
    }

    private VistaDataChunk createVistaDataChunk(String vistaId, String rpcUri, JsonNode json, String domain, int fragmentIndex, int fragmentCount, Patient pt = null, Map processorParams = [:]) {
        VistaDataChunk fragment = new VistaDataChunk(systemId: vistaId,
                rpcUri: rpcUri,
                params: processorParams,
                itemIndex: fragmentIndex,
                itemCount: fragmentCount,
                json: json,
                domain: domain,
                patient: pt,
                patientId: pt?.getPid())
        if (pt) fragment.localPatientId = pt.getLocalPatientIdForSystem(vistaId);
        return fragment
    }

    private Map getProcessorParams(String vistaId, String pid, boolean icn) {
        Map m = [:]
        m[SyncMessageConstants.VISTA_ID] = vistaId
        if (icn)
            m[SyncMessageConstants.PATIENT_ICN] = pid
        else
            m[SyncMessageConstants.PATIENT_DFN] = pid
        return m
    }

    void subscribePatient(String vistaId, String pid, String serverId) {
        List params = [serverId, "1", [pid]]
        String response = synchronizationRpcTemplate.executeForString("vrpcb://${vistaId}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${VPR_SUBSCRIBE}", params)
        // TODO: check for failure response here
    }

    void unsubscribePatient(String vistaId, String pid, String serverId) {
        List params = [serverId, "0", [pid]]
        String response = synchronizationRpcTemplate.executeForString("vrpcb://${vistaId}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${VPR_SUBSCRIBE}", params)
        // TODO: check for failure response here
    }

}
