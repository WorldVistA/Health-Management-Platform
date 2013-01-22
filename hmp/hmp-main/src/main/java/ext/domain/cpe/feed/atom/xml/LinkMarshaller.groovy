package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller
import EXT.DOMAIN.cpe.feed.atom.Link

/**
 * Responsible for marshalling <code>Link</code> atom nodes to XML.
 * 
 * @see Link
 */
class LinkMarshaller implements ObjectMarshaller<XML>, NameAwareMarshaller {

    String getElementName(Object o) {
        return 'link'
    }

    void marshalObject(Object object, XML xml) {
        Link l = object as Link
        xml.attribute('xmlns', 'http://www.w3.org/2005/Atom')
        if (l.rel) xml.attribute('rel', l.rel)
        if (l.type) xml.attribute('type', l.type)
        if (l.href) xml.attribute('href', l.href)
        if (l.title) xml.attribute('title', l.title)
        if (l.length) xml.attribute('length', l.length)
    }

    boolean supports(Object object) {
        return object instanceof Link
    }

}
