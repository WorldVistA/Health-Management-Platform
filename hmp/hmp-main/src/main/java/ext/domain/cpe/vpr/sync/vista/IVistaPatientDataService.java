package EXT.DOMAIN.cpe.vpr.sync.vista;

import EXT.DOMAIN.cpe.vpr.Patient;

import java.util.List;

// TODO: I think this might should be named IVprDao or maybe IVprExtractService (also maybe belongs in another package?)

/**
 * API to the VPR Extracts.
 */
public interface IVistaPatientDataService {
    String fetchVprVersion(String vistaId);
    VistaDataChunk fetchPatientDemographicsWithDfn(String vistaId, String ptDfn);
    VistaDataChunk fetchPatientDemographicsWithIcn(String vistaId, String ptIcn);
    List<VistaDataChunk> fetchDomainChunks(String vistaId, Patient pt, String domain, boolean includeBody, String category);
    VprUpdateData fetchUpdates(String vistaId, String vprId, String lastUpdate);
    void subscribePatient(String vistaId, String pid, String serverId);
    void unsubscribePatient(String vistaId, String pid, String serverId);
}
