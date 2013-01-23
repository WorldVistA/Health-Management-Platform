package org.osehra.cpe.feed.atom.xml;


import org.osehra.cpe.feed.atom.Category
import grails.converters.XML
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertTrue

class CategoryMarshallerTests {

    @Before
    public void setUp() {
        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new CategoryMarshaller(), 1)
    }

    @Test
    void testSupports() {
        CategoryMarshaller m = new CategoryMarshaller()
        assertTrue(m.supports(new Category(term: 'foobar')))
    }

    @Test
    void testMarshalMinimalCategory() {
               def expected = '''
<category term='foo'/>
'''
        String xml = new XML(new Category(term: 'foo')).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    @Test
    void testMarshalFullCategory() {
               def expected = '''
<category term='bar' scheme="http://www.example.org/rels/bar" label="This is a Bar."/>
'''
        String xml = new XML(new Category(term: 'bar', scheme:'http://www.example.org/rels/bar', label:'This is a Bar.')).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}
