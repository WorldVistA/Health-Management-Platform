package org.osehra.cpe.feed.atom.xml;


import org.osehra.cpe.feed.atom.Link
import grails.converters.XML
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertTrue

class LinkMarshallerTests {

    @Before
    void setUp() {
        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new LinkMarshaller(), 1)
    }

    @Test
    void testSupports() {
        LinkMarshaller m = new LinkMarshaller()
        assertTrue(m.supports(new Link(rel: 'self', href: 'http://www.example.com')))
    }

    @Test
    void testMarshalAtomLink() {
               String expected = '''
<atom:link xmlns:atom="http://www.w3.org/2005/Atom"
           rel='self'
           href='http://www.example.com'/>
'''
        String xml = new XML(new Link(rel: 'self', href: 'http://www.example.com')).toString()
        Diff xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}
