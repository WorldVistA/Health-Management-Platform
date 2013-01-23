package org.osehra.cpe.vpr.sync.msg

import org.springframework.stereotype.Service
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.springframework.beans.factory.annotation.Autowired
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.osehra.cpe.vpr.sync.SyncAction

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.UID
import static org.osehra.cpe.vpr.sync.SyncMessageConstants.ACTION
import static org.osehra.cpe.vpr.sync.SyncAction.ITEM_CLEAR

@Service
class ClearItemMessageHandler implements IMapMessageHandler {

    @Autowired
    IGenericPatientObjectDAO genericDao;

    @Override
    void onMessage(Map msg) {
        assert msg[ACTION] == ITEM_CLEAR
        assert msg[UID]

        String uid = msg[UID]
        genericDao.deleteByUID(null, uid)
    }
}
