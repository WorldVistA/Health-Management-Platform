package EXT.DOMAIN.cpe.vpr.web

import EXT.DOMAIN.cpe.vpr.NotFoundException

class PatientNotFoundException extends NotFoundException {

    String pid

    PatientNotFoundException(String pid) {
        super("patient '${pid}' not found".toString())
        this.pid = pid
    }
}
