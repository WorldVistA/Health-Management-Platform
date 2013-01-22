package EXT.DOMAIN.cpe.feed.atom.json

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.feed.atom.Link


class LinkMarshaller implements ObjectMarshaller<JSON> {

    void marshalObject(Object object, JSON json) {
        Link l = object as Link
        
        json.writer.object();
        if (l.rel) json.property('rel', l.rel)
        if (l.type) json.property('type', l.type)
        if (l.href) json.property('href', l.href)
        if (l.title) json.property('title', l.title)
        if (l.length) json.property('length', l.length)
        json.writer.endObject();
    }

    boolean supports(Object object) {
        return object instanceof Link
    }

}
