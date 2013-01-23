package org.osehra.cpe.vpr.ws.xml

import java.beans.PropertyDescriptor;

import grails.converters.XML
import org.osehra.cpe.vpr.Result


class ResultMarshaller extends DomainClassMarshaller {
    @Override
    boolean supports(Object object) {
        return object instanceof Result;
    }

    @Override protected void marshalProperty(Object o, PropertyDescriptor property, XML xml) {
        if (property.name == "organizers") return;
        super.marshalProperty(o, property, xml)
    }

}
