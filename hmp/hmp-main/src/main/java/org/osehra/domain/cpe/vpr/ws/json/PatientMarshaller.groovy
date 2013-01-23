package org.osehra.cpe.vpr.ws.json

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller

class PatientMarshaller implements ObjectMarshaller<JSON> {
    boolean supports(Object object) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    void marshalObject(Object object, JSON converter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
