package EXT.DOMAIN.cpe.vpr.sync.msg

import org.springframework.stereotype.Service
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO
import org.springframework.beans.factory.annotation.Autowired
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants
import EXT.DOMAIN.cpe.vpr.sync.SyncAction

import static EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants.UID
import static EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants.ACTION
import static EXT.DOMAIN.cpe.vpr.sync.SyncAction.ITEM_CLEAR

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
