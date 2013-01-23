package org.osehra.cpe.vpr.sync.vista

import com.fasterxml.jackson.databind.JsonNode

import org.osehra.cpe.vpr.Patient

import org.osehra.cpe.vpr.pom.POMUtils;

/**
 * Represents a fragment of xml returned by a VistA RPC that corresponds to an item that is processed individually.
 */
public class VistaDataChunk {

    public enum Type {
        NEW_OR_UPDATE,
        ERROR
    }

    /**
     * A unique identifier for the system that this chunk came from.
     *
     * In VistA, in defined as the hex string of the CRC-16 of the domain name of this VistA system.
     *
     * @see "VistA FileMan KERNEL SYSTEM PARAMETERS,DOMAIN NAME(8989.3,.01)"
     */
    String systemId

    /**
     * The VPR patient id of the patient this chunk pertains to
     */
    String patientId

    /**
     * The local patient id of the patient in the system this came from (DFN in VistA)
     */
    String localPatientId

    /**
     * The optional {@link Patient} object
     */
    Patient patient

    /**
     * The optional type of the chunk
     */
    Type type = Type.NEW_OR_UPDATE

    /**
     * The domain of this chunk (as passed to the extract RPC)
     */
    String domain

    /**
     * The actual content of this chunk as a string (usually JSON formatted)
     */
    String content

    /**
     * The URI of the RPC that this chunk was returned from.
     */
    String rpcUri

    /**
     * The index (0-based) of this chunk in the list of all chunks returned by one extract RPC
     */
    int itemIndex

    /**
     *  The count of this chunk in the list of all chunks returned by one extract RPC
     */
    int itemCount

    /**
     * A catch-all set of key value pairs that should accompany the chunk
     */
    Map params

    private JsonNode json

    public Patient getPatient() {
        return patient;
    }

    String toString() {
        return "'${domain}' extract ${itemIndex + 1} of ${itemCount}${patient ? ' for ' + patient.toString() : ''} returned by ${rpcUri}".toString()
    }

    public JsonNode getJson() {
        if (!json) {
            json = POMUtils.parseJSONtoNode(content);
        }
        return this.json
    }

    public Map<String, Object> getJsonMap() {
        return POMUtils.parseJSONtoMap(content);
    }

    public void setJson(JsonNode node) {
        content = node.toString()
        this.json = null
    }
}
