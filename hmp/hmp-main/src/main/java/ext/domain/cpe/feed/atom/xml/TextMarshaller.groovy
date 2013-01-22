package EXT.DOMAIN.cpe.feed.atom.xml

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import EXT.DOMAIN.cpe.feed.atom.Text
import org.codehaus.groovy.grails.web.xml.XMLStreamWriter

/**
 * Responsible for marshalling <code>Text</code> atom nodes to XML.
 *
 * @see Text
 */
class TextMarshaller implements ObjectMarshaller<XML> {

    boolean supports(Object object) {
        return object instanceof Text;
    }

    void marshalObject(Object object, XML xml) {
        Text t = object as Text

        if (t.type) xml.attribute('type', t.type)

        if (t.type == 'text') {
            xml.chars(t.text)
        } else if (t.type == 'html') {
            xml.chars(t.text.encodeAsHTML())
        } else if (t.type == 'xhtml') {
            xml.startNode("div")
            xml.attribute("xmlns", "http://www.w3.org/1999/xhtml")
            xml.writer.endStartTag()
            xml.writer.mode = XMLStreamWriter.Mode.CONTENT
            xml.writer.writer.unescaped().write(t.text)
            xml.end()
            // FIXME: make unit test pass
        } else if (t.type.endsWith("xml")) {
            xml.writer.endStartTag()
            xml.writer.mode = XMLStreamWriter.Mode.CONTENT
            xml.writer.writer.unescaped().write(t.text)
            xml.writer.end()
        }
    }

}
