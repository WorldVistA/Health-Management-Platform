package org.osehra.cpe.vpr.sync

import org.osehra.cpe.vpr.Patient


class UnreachableFacilityException extends Exception {

    UnreachableFacilityException(Patient pt, String stationNumber, String name, String vistaId) {
        super("Patient ${pt} was seen at facility '${name}' station number '${stationNumber}' but data from ${name} is unreachable because the facility's 'domain' was missing from the patient demographics extract from VistA account '${vistaId}'. Please set the 'domain' for the ${name} entry in the INSTITUTION file.".toString())
    }
}
