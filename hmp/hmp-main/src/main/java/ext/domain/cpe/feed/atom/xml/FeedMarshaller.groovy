package EXT.DOMAIN.cpe.feed.atom.xml

import EXT.DOMAIN.cpe.feed.atom.Feed
import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.joda.time.format.ISODateTimeFormat
import EXT.DOMAIN.cpe.feed.atom.Person
import EXT.DOMAIN.cpe.feed.atom.Link

/**
 * Responsible for marshalling <code>Feed</code> nodes to XML.
 *
 * @see Feed
 */
class FeedMarshaller implements ObjectMarshaller<XML>, NameAwareMarshaller {

    String getElementName(Object o) {
        return 'feed';
    }

    void marshalObject(Object object, XML xml) {
        Feed f = object as Feed
//        if (!f.validate()) {
//           throw new ValidationException("invalid atom feed", f.errors)
//        }

        xml.attribute('xmlns', 'http://www.w3.org/2005/Atom')

        xml.startNode('id')
        xml.chars(f.id)
        xml.end()

        xml.startNode('title')
        xml.convertAnother f.title
        xml.end()

        xml.startNode('updated')
        xml.chars(ISODateTimeFormat.dateTimeNoMillis().print(f.updated))
        xml.end()

        f.authors.each { Person author ->
            xml.startNode('author')
            xml.convertAnother author
            xml.end()
        }

        f.links.each { Link link ->
            xml.startNode(xml.getElementName(link))
            xml.convertAnother link
            xml.end()
        }

        f.categories.each { category ->
            xml.startNode(xml.getElementName(category))
            xml.convertAnother category
            xml.end()
        }

        f.contributors.each { Person contributor ->
            xml.startNode('contributor')
            xml.convertAnother contributor
            xml.end()
        }

        if (f.generator) {
           xml.startNode('generator')
           if (f.generator.uri) xml.attribute('uri', f.generator.uri)
           if (f.generator.version) xml.attribute('version', f.generator.version)
           xml.chars(f.generator.text)
           xml.end()
        }

        if (f.icon) {
            xml.startNode('icon')
            xml.chars(f.icon)
            xml.end()
        }

        if (f.logo) {
            xml.startNode('logo')
            xml.chars(f.logo)
            xml.end()
        }

        if (f.rights) {
            xml.startNode('rights')
            xml.convertAnother(f.rights)
            xml.end()
        }

        if (f.subtitle) {
            xml.startNode('subtitle')
            xml.convertAnother f.subtitle
            xml.end()
        }

        f.entries.each { entry ->
            xml.startNode(xml.getElementName(entry))
            xml.convertAnother entry
            xml.end()
        }
    }

    boolean supports(Object object) {
        return object instanceof Feed
    }

}
