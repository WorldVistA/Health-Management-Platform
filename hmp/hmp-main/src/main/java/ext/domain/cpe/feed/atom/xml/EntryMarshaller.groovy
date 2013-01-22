package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.feed.atom.Entry
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller
import org.joda.time.format.ISODateTimeFormat
import EXT.DOMAIN.cpe.feed.atom.Person

/**
 * Responsible for marshalling <code>Entry</code> atom nodes to XML.
 *
 * @see EXT.DOMAIN.cpe.feed.atom.Entry
 */
class EntryMarshaller implements ObjectMarshaller<XML>, NameAwareMarshaller {

    String getElementName(Object o) {
        return 'entry';
    }

    void marshalObject(Object object, XML xml) {
        Entry e = object as Entry

        xml.startNode('id')
        xml.chars(e.id)
        xml.end()

        xml.startNode('title')
        xml.convertAnother e.title
        xml.end()

        xml.startNode('updated')
        xml.chars(ISODateTimeFormat.dateTimeNoMillis().print(e.updated))
        xml.end()

        e.authors.each { Person author ->
            xml.startNode('author')
            xml.convertAnother author
            xml.end()
        }

        e.links.each { link ->
            xml.startNode(xml.getElementName(link))
            xml.convertAnother link
            xml.end()
        }

        if (e.summary) {
            xml.startNode('summary')
            xml.convertAnother e.summary
            xml.end()
        }

        if (e.content) {
            xml.startNode('content')
            xml.convertAnother e.content
            xml.end()
        }

        e.categories.each { category ->
            xml.startNode(xml.getElementName(category))
            xml.convertAnother category
            xml.end()
        }

        e.contributors.each { Person contributor ->
            xml.startNode('contributor')
            xml.convertAnother contributor
            xml.end()
        }

        if (e.published) {
            xml.startNode('published')
            xml.chars(ISODateTimeFormat.dateTimeNoMillis().print(e.published))
            xml.end()
        }

        if (e.rights) {
            xml.startNode('rights')
            xml.convertAnother e.rights
            xml.end()
        }
    }

    boolean supports(Object object) {
        return object instanceof Entry
    }

}
