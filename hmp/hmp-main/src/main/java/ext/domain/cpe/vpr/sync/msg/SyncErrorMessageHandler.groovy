package org.osehra.cpe.vpr.sync.msg

import org.springframework.transaction.annotation.Transactional
import org.osehra.cpe.vpr.SyncError
import org.springframework.transaction.annotation.Propagation

import org.osehra.cpe.vpr.sync.SyncAction

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.osehra.cpe.vpr.dao.ISyncErrorDao


@Deprecated
@Service
class SyncErrorMessageHandler implements IMapMessageHandler {

    @Autowired
    ISyncErrorDao syncErrorDao

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void onMessage(Map msg) {
        String action = msg[SyncMessageConstants.ACTION]
        if (action == SyncAction.IMPORT_CHUNK)
            onImportError(msg)
        else
            onLoadError(msg)
    }

    private void onLoadError(Map msg) {
        SyncError syncError = new SyncError(pid: msg[SyncMessageConstants.PATIENT_ID],
                item: msg["exception.name"],
                message: msg["exception.message"],
                stackTrace: msg["exception.stackTrace"])
        save(syncError)
    }

    private void onImportError(Map msg) {
        String json = msg[SyncMessageConstants.RPC_JSON]
        String domain = msg[SyncMessageConstants.VPR_DOMAIN]
            SyncError syncError = new SyncError(pid: msg[SyncMessageConstants.PATIENT_ID],
                    item: "'${domain}' chunk ${msg[SyncMessageConstants.RPC_ITEM_INDEX] + 1} of ${msg[SyncMessageConstants.RPC_ITEM_COUNT]} returned from ${msg[SyncMessageConstants.RPC_URI]}",
                    message: msg["exception.message"],
                    stackTrace: msg["exception.stackTrace"],
                    json: json)
        save(syncError)
    }


    private void save(SyncError syncError) {
        syncErrorDao.save(syncError)
    }
}

