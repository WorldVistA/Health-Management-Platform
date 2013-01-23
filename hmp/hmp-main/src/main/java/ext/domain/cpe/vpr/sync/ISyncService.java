package org.osehra.cpe.vpr.sync;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service responsible for dispatching sync messages via JMS for processing by JMS listeners.
 */
public interface ISyncService {
    void sendLoadPatientsMsg(String vistaId, List<String> dfnList);
    void sendLoadPatientMsgWithIcn(String vistaId, String icn);
    void sendLoadPatientMsgWithDfn(String vistaId, String dfn);
    void sendLoadPatientCompleteMsg(Patient pt, Map originalLoadMsg);
    void sendImportPatientDataExtractItemMsg(VistaDataChunk fragment);
    void retryMsg(Map msg);
    void sendReindexPatientMsg(Patient pt);
    void sendReindexPatientMsg(String pid);
    void sendReindexAllPatientsMsg();
    void sendClearPatientMsg(Patient pt);
    void sendClearPatientMsg(String pid);
    void sendClearItemMsg(String uid);
    void sendClearAllPatientsMsg();
    void sendUpdateVprCompleteMsg(String serverId, String vistaId, String lastUpdate, Map<String, Set<String>> domainsByPatientId);
    void errorDuringMsg(Map msg, Throwable t);
    void registerChunkProcessing(VistaDataChunk chunk);
    void deregisterChunkProcessing(VistaDataChunk chunk);
    long getProcessingQueueSize();
}
