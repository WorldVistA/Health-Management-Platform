package EXT.DOMAIN.cpe.vpr.sync.msg

import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants

class SyncMessageDispatcher implements IMapMessageHandler {

    Map<String, IMapMessageHandler> actionToProcessorMap

    void onMessage(Map msg) {
        String action = msg[SyncMessageConstants.ACTION]
        if (actionToProcessorMap.containsKey(action)) {
            actionToProcessorMap[action].onMessage(msg)
        }
    }
}
