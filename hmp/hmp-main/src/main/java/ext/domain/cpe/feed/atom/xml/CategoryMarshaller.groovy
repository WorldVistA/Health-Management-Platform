package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.feed.atom.Category
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller

/**
 * Responsible for marshalling <code>Category</code> atom nodes to XML.
 *
 * @see EXT.DOMAIN.cpe.feed.atom.Category
 */
class CategoryMarshaller implements ObjectMarshaller<XML>, NameAwareMarshaller {

    String getElementName(Object o) {
        return 'category'
    }

    boolean supports(Object object) {
        return object instanceof Category;
    }

    void marshalObject(Object object, XML xml) {
        Category c = object as Category
        xml.attribute('term', c.term)
        if (c.scheme) xml.attribute('scheme', c.scheme)
        if (c.label) xml.attribute('label', c.label)
    }

}
