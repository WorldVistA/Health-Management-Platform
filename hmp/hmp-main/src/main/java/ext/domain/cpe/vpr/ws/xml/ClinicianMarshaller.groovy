package EXT.DOMAIN.cpe.vpr.ws.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.vpr.Clinician


class ClinicianMarshaller implements ObjectMarshaller<XML> {
    boolean supports(Object object) {
        object instanceof Clinician
    }

    void marshalObject(Object o, XML xml) {
        Clinician clinician = o as Clinician
        xml.convertAnother(clinician.name)
    }
}
