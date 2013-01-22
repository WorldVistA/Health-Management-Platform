package EXT.DOMAIN.cpe.vpr.ws.json

import java.beans.PropertyDescriptor;

import grails.converters.JSON
import EXT.DOMAIN.cpe.vpr.Result

class ResultMarshaller extends DomainClassMarshaller {
    @Override
    boolean supports(Object object) {
        return object instanceof Result;
    }

    @Override protected void marshalProperty(Object o, PropertyDescriptor property, JSON json) {
        if (property.name == "organizers") return;
        if (property.name == "accession") return;
        super.marshalProperty(o, property, json)
    }

}
