package org.osehra.cpe.vpr.web

import org.osehra.cpe.vpr.NotFoundException

class PatientNotFoundException extends NotFoundException {

    String pid

    PatientNotFoundException(String pid) {
        super("patient '${pid}' not found".toString())
        this.pid = pid
    }
}
