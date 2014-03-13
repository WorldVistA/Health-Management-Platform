package org.osehra.cpe.vpr.ws.xml

import java.beans.PropertyDescriptor;

import org.osehra.cpe.vpr.ResultOrganizer
import grails.converters.XML


class ResultOrganizerMarshaller extends DomainClassMarshaller {
    @Override
    boolean supports(Object object) {
        return object instanceof ResultOrganizer;
    }

    @Override protected void marshalProperty(Object o, PropertyDescriptor property, XML xml) {
        if (property.name == "results") return;
        super.marshalProperty(o, property, xml)
    }
}
