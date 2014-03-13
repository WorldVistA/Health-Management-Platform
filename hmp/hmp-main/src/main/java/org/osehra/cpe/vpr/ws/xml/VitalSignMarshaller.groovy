package org.osehra.cpe.vpr.ws.xml

import java.beans.PropertyDescriptor;

import grails.converters.XML
import org.osehra.cpe.vpr.VitalSign


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
