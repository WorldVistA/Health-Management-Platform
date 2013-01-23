package org.osehra.cpe.feed.atom.xml

import grails.test.GrailsUnitTestCase
import org.custommonkey.xmlunit.XMLUnit
import grails.converters.XML
import org.osehra.cpe.feed.atom.Content
import org.osehra.cpe.feed.atom.Text
import org.custommonkey.xmlunit.Diff

class ContentMarshallerTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new ContentMarshaller(), 1)
    }

    void testSupports() {
        ContentMarshaller m = new ContentMarshaller()
        assertTrue(m.supports(new Content()))
        assertFalse(m.supports(new Text()))
    }

    void testMarshalText() {
        def expected = '''
    <baz>
        <content type="text">The quick brown fox jumps over the lazy dog.</content>
        <class>org.osehra.cpe.feed.atom.xml.Baz</class>
    </baz>
    '''
        String xml = new XML(new Baz(content:new Content("The quick brown fox jumps over the lazy dog."))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

     void testMarshalSrc() {
        def expected = '''
    <baz>
        <content src="http://www.example.org/blogs/123"/>
        <class>org.osehra.cpe.feed.atom.xml.Baz</class>
    </baz>
    '''
        String xml = new XML(new Baz(content:new Content(src:"http://www.example.org/blogs/123"))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    void testMarshalSrcAndType() {
        def expected = '''
    <baz>
        <content type="text/html" src="http://www.example.org/blogs/123"/>
        <class>org.osehra.cpe.feed.atom.xml.Baz</class>
    </baz>
    '''
        String xml = new XML(new Baz(content:new Content(src:"http://www.example.org/blogs/123", type:'text/html'))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}

class Baz {
    Content content
}
