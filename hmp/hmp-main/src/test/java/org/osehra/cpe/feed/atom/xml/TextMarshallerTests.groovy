package org.osehra.cpe.feed.atom.xml

import grails.test.GrailsUnitTestCase
import org.custommonkey.xmlunit.XMLUnit
import grails.converters.XML
import org.osehra.cpe.feed.atom.Text
import org.custommonkey.xmlunit.Diff


class TextMarshallerTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()

        loadCodec org.codehaus.groovy.grails.plugins.codecs.HTMLCodec

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new TextMarshaller(), 1)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSupports() {
        TextMarshaller m = new TextMarshaller()
        assertTrue(m.supports(new Text("The quick brown fox jumps over the lazy dog.")))
    }

    void testMarshalPlainText() {
        def expected = '''
<foo>
<title type='text'>The quick brown fox jumps over the lazy dog.</title>
<class>org.osehra.cpe.feed.atom.xml.Foo</class>
</foo>
'''
        String xml = new XML(new Foo(title: new Text("The quick brown fox jumps over the lazy dog."))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }


    void testMarshalPlainTextWithXmlEntityCharacter() {
        def expected = '''
<foo>
<title type='text'>AT&amp;T bought by SBC!</title>
<class>org.osehra.cpe.feed.atom.xml.Foo</class>
</foo>
'''
        String xml = new XML(new Foo(title: new Text("AT&T bought by SBC!"))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    void testMarshalHtml() {
        def expected = '''
<foo>
<title type='html'>AT&amp;amp;T bought &amp;lt;b&amp;gt;by SBC&amp;lt;/b&amp;gt;!</title>
<class>org.osehra.cpe.feed.atom.xml.Foo</class>
</foo>
'''
        String xml = new XML(new Foo(title: new Text(text:'AT&T bought <b>by SBC</b>!', type:"html"))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

//    void testMarshalXhtml() {
//               def expected = '''
//<foo>
//<title type='xhtml'>
//    <div xmlns="http://www.w3.org/1999/xhtml">AT&amp;T bought <b>by SBC</b>!</div>
//</title>
//<class>org.osehra.vler.cpe.feed.atom.xml.Foo</class>
//</foo>
//'''
//        String xml = new XML(new Foo(title: new Text(text:'AT&T bought <b>by SBC</b>!', type:"xhtml"))).toString()
//        def xmlDiff = new Diff(expected, xml)
//        assertTrue(xmlDiff.toString(), xmlDiff.similar())
//    }
}

class Foo {
    Text title
}
