package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML

import EXT.DOMAIN.cpe.feed.atom.Content

/**
 * Responsible for marshalling <code>Content</code> atom nodes to XML.
 *
 * @see EXT.DOMAIN.cpe.feed.atom.Content
 */
class ContentMarshaller extends TextMarshaller {

    boolean supports(Object object) {
        return object instanceof Content;
    }

    void marshalObject(Object object, XML xml) {
        Content c = object as Content
        if (c.src) {
            if (c.type) xml.attribute('type', c.type)
            xml.attribute('src', c.src)
        } else {
            super.marshalObject(c, xml)
        }
    }
}
