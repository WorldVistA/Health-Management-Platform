package org.osehra.cpe.vpr.sync.vista;

import org.osehra.cpe.HmpProperties;
import org.osehra.cpe.hub.VistaAccount;
import org.osehra.cpe.hub.dao.IVistaAccountDao;
import org.osehra.cpe.vpr.dao.IVprUpdateDao;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.sync.ISyncService;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import java.util.*;

public class VprUpdateJob implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(VprUpdateJob.class);

    IVistaPatientDataService vistaPatientDataService;

    ISyncService syncService;

    IVistaAccountDao vistaAccountDao;

    IVprUpdateDao lastUpdateDao;

    private String serverId;

    private boolean disabled = false;

    @Autowired
    public void setVistaPatientDataService(IVistaPatientDataService vistaPatientDataService) {
        this.vistaPatientDataService = vistaPatientDataService;
    }

    @Autowired
    public void setSyncService(ISyncService syncService) {
        this.syncService = syncService;
    }

    @Autowired
    public void setVistaAccountDao(IVistaAccountDao vistaAccountDao) {
        this.vistaAccountDao = vistaAccountDao;
    }

    @Autowired
    public void setLastUpdateDao(IVprUpdateDao lastUpdateDao) {
        this.lastUpdateDao = lastUpdateDao;
    }

    @Required
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public synchronized void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void run() {
        if (isDisabled()) return;

        List<VistaAccount> accounts = vistaAccountDao.findAllByVistaIdIsNotNull();
        for (VistaAccount account : accounts) {
            if (!account.isVprAutoUpdate()) continue;

            String vistaId = account.getVistaId();
            LOG.info("HMP instance {} checking for updates at VistA {}", serverId, vistaId);
            try {
                VprUpdate lastUpdate = lastUpdateDao.findOneBySystemId(vistaId);

                String lastUpdateTimestamp = lastUpdate != null ? lastUpdate.getTimestamp() : "";
                LOG.debug("lastUpdate for VistA {} at {}", vistaId, lastUpdateTimestamp);

                VprUpdateData data = vistaPatientDataService.fetchUpdates(vistaId, serverId, lastUpdateTimestamp);
                if (data == null) continue;

                processExceptions(data, vistaId, serverId);
                processChunks(data.getChunks());
                processDeletions(data.getUidsToDelete());
                Map<String, Set<String>> domainsByPatientId = getDomainsByPatientId(data.getChunks());

                syncService.sendUpdateVprCompleteMsg(serverId, vistaId, data.getLastUpdate(), !domainsByPatientId.isEmpty() ? domainsByPatientId : null);
            } catch (SynchronizationCredentialsNotFoundException e) {
                LOG.warn("No VPR updates from VistA " + vistaId + " are available. " + e.getMessage());
            } catch (Throwable t) {
                LOG.error("Unable to fetch VPR updates from Vista " + vistaId, t);
            }
        }
    }

    private void processChunks(List<VistaDataChunk> chunks) {
        for (VistaDataChunk chunk : chunks) {
            try {
                syncService.sendImportPatientDataExtractItemMsg(chunk);
            } catch (Throwable t) {
                LOG.warn("unexpected exception sending import message", t);
            }
        }
    }

    private void processDeletions(Set<String> uidsToDelete) {
        for (String uid : uidsToDelete) {
            syncService.sendClearItemMsg(uid);
        }
    }

    private Map<String, Set<String>> getDomainsByPatientId(List<VistaDataChunk> chunks) {
        Map<String, Set<String>> domainsByPatientId = new HashMap<String, Set<String>>();
        for (VistaDataChunk chunk : chunks) {
            String pid = chunk.getPatientId();
            if (!StringUtils.hasText(pid)) continue;
            if (!domainsByPatientId.containsKey(pid)) {
                domainsByPatientId.put(pid, new HashSet<String>());
            }
            domainsByPatientId.get(pid).add(chunk.getDomain());
        }
        return domainsByPatientId;
    }

    private void processExceptions(VprUpdateData data, String vistaId, String serverId) {
        for (Exception e : data.getExceptions()) {
            LOG.error("exception during fetchUpdates() at " + vistaId, e);
            Map msg = new HashMap();
            msg.put(SyncMessageConstants.VISTA_ID, vistaId);
            msg.put(HmpProperties.SERVER_ID, serverId);
            msg.put(SyncMessageConstants.VISTA_LAST_UPDATED, data.getLastUpdate());
            msg.put(SyncMessageConstants.TIMESTAMP, System.currentTimeMillis());
            syncService.errorDuringMsg(msg, e);
        }
    }
}
