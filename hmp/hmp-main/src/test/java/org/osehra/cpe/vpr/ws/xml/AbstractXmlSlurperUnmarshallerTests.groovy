package org.osehra.cpe.vpr.ws.xml

import groovy.util.slurpersupport.GPathResult
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamSource
import org.xml.sax.InputSource
import org.xml.sax.helpers.XMLReaderFactory

class AbstractXmlSlurperUnmarshallerTests extends GroovyTestCase {

    static def FOO = '''
<foo>
    <bar baz="waz">spaz</bar>
</foo>
    '''
    FooUnmarshaller u = new FooUnmarshaller()

    void testStreamSourceWithReader() {
        assertFoo(u.unmarshal(new StreamSource(new StringReader(FOO))))
    }

    void testSaxSource() {
        assertFoo(u.unmarshal(new SAXSource(new InputSource(new StringReader(FOO)))))
        assertFoo(u.unmarshal(new SAXSource(XMLReaderFactory.createXMLReader(), new InputSource(new StringReader(FOO)))))
    }

    void testGPathResultSource() {
        assertFoo(u.unmarshal(new GPathResultSource(xml:new XmlSlurper().parse(new StringReader(FOO)))))
    }

    void assertFoo(Foo foo) {
        groovy.util.GroovyTestCase.assertEquals 'spaz', foo.bar
        groovy.util.GroovyTestCase.assertEquals 'waz', foo.baz
    }
}

class FooUnmarshaller extends AbstractXmlSlurperUnmarshaller {

    boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Foo)
    }

    protected Object unmarshalGPathResult(GPathResult xml) {
        return new Foo(bar: xml.bar.text(), baz: xml.bar.@baz)
    }
}
