package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.vpr.sync.SyncMessageConstants

class SyncMessageDispatcher implements IMapMessageHandler {

    Map<String, IMapMessageHandler> actionToProcessorMap

    void onMessage(Map msg) {
        String action = msg[SyncMessageConstants.ACTION]
        if (actionToProcessorMap.containsKey(action)) {
            actionToProcessorMap[action].onMessage(msg)
        }
    }
}
