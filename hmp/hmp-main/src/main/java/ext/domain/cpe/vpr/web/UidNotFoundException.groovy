package EXT.DOMAIN.cpe.vpr.web

import EXT.DOMAIN.cpe.vpr.NotFoundException

class UidNotFoundException extends NotFoundException {

    Class domainClass
    String uid

    UidNotFoundException(Class domainClass, String uid) {
        super("${domainClass.name} with uid '${uid}' not found".toString())
        this.domainClass = domainClass
        this.uid = uid
    }

}
