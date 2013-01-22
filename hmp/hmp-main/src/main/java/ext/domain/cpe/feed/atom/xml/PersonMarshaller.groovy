package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.feed.atom.Person

/**
 * Responsible for marshalling <code>Person</code> atom nodes to XML.
 *
 * @see Person
 */
class PersonMarshaller implements ObjectMarshaller<XML> {

    void marshalObject(Object object, XML xml) {
        Person p = object as Person
        xml.build {
            name(p.name)
            if (p.uri) uri(p.uri)
            if (p.email) email(p.email)
        }
    }

    boolean supports(Object object) {
        return object instanceof Person
    }

}
