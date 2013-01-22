package EXT.DOMAIN.cpe.vpr.ws.xml

import java.beans.PropertyDescriptor;

import grails.converters.XML
import EXT.DOMAIN.cpe.vpr.VitalSign


class VitalSignMarshaller extends DomainClassMarshaller {
    @Override
    boolean supports(Object object) {
        return object instanceof VitalSign
    }

    @Override protected void marshalProperty(Object o, PropertyDescriptor property, XML xml) {
        if (property.name == "organizer") return;
        super.marshalProperty(o, property, xml)
    }


}
