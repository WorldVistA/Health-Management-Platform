package org.osehra.cpe.vpr.sync.vista

/**
 * Class that holds the results of one call to one VistA system's VPR updates.
 *
 * @see VistaPatientDataService#fetchUpdates
 * @see org.osehra.cpe.vpr.sync.vista.VprUpdateJob
 */
class VprUpdateData {
    String lastUpdate
    /**
     * A list of {@link VistaDataChunk}s to insert or update in the VPR.
     */
    List<VistaDataChunk> chunks = []
    /**
     * A list of UIDs to individial items to remove from the VPR.
     * <p/>
     * These are usually generated from "enterred in error" or "retracted" items.
     */
    Set<String> uidsToDelete = []
    List<Exception> exceptions = []
}
