package org.osehra.cpe.feed.atom.xml

import org.osehra.cpe.datetime.PointInTime
import grails.converters.XML
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.osehra.cpe.feed.atom.*

class EntryMarshallerTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()

        loadCodec HTMLCodec

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new TextMarshaller(), 1)
        XML.registerObjectMarshaller(new ContentMarshaller(), 1)
        XML.registerObjectMarshaller(new LinkMarshaller(), 1)
        XML.registerObjectMarshaller(new PersonMarshaller(), 1)
        XML.registerObjectMarshaller(new CategoryMarshaller(), 1)
        XML.registerObjectMarshaller(new EntryMarshaller(), 1)
    }

    void testSupports() {
        EntryMarshaller m = new EntryMarshaller()
        assertTrue(m.supports(new Entry()))
    }

    void testMarshalMinimalEntry() {
        def expected = '''
    <entry>
        <id>http://example.com/blog/1234</id>
        <title type="text">Atom-Powered Robots Run Amok</title>
        <updated>1975-07-23T10:13:34</updated>
    </entry>
    '''
        String xml = new XML(new Entry(
                id: 'http://example.com/blog/1234',
                title: new Text('Atom-Powered Robots Run Amok'),
                updated: new PointInTime(1975, 7, 23, 10, 13, 34, 0))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    void testMarshalFullEntry() {
        def expected = '''
    <entry>
        <id>http://example.com/blog/1234</id>
        <title type="text">Atom-Powered Robots Run Amok</title>
        <updated>1975-07-23T10:13:34</updated>
        <author>
            <name>Flintstone, Fred</name>
        </author>
        <content type="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eget tortor quam, mollis porta quam. Nam placerat sem consectetur magna semper bibendum. Duis pretium pellentesque nisi, id rhoncus augue porta eget. Nulla facilisi. Nam non augue sed leo auctor pretium. Curabitur et lacus odio, ac mattis velit. In eget dapibus metus. In condimentum, lacus ac fringilla hendrerit, turpis mauris imperdiet nulla, non mattis dolor quam vitae leo. Morbi nec velit sit amet odio commodo consectetur. Mauris semper nulla at mi vulputate malesuada.</content>
        <link xmlns='http://www.w3.org/2005/Atom' rel="alternate" href="/blog/1234"/>
        <summary type="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit.</summary>
        <category term="Foo"/>
        <category term="Bar"/>
        <contributor>
            <name>Flintstone, Wilma</name>
        </contributor>
        <contributor>
            <name>Rubble, Barney</name>
        </contributor>
        <published>1984-03-11T22:43:09</published>
        <rights type="html">&amp;amp;copy; 1984 Fred Flintstone</rights>
    </entry>
    '''
        Entry entry = new Entry(
                id: 'http://example.com/blog/1234',
                title: new Text('Atom-Powered Robots Run Amok'),
                updated: new PointInTime(1975, 7, 23, 10, 13, 34, 0),
                author: new Person(name: 'Flintstone, Fred'),
                content: new Content('Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eget tortor quam, mollis porta quam. Nam placerat sem consectetur magna semper bibendum. Duis pretium pellentesque nisi, id rhoncus augue porta eget. Nulla facilisi. Nam non augue sed leo auctor pretium. Curabitur et lacus odio, ac mattis velit. In eget dapibus metus. In condimentum, lacus ac fringilla hendrerit, turpis mauris imperdiet nulla, non mattis dolor quam vitae leo. Morbi nec velit sit amet odio commodo consectetur. Mauris semper nulla at mi vulputate malesuada.'),
                link: new Link(rel: 'alternate', href: '/blog/1234'),
                summary: new Text('Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
                published: new PointInTime(1984, 3, 11, 22, 43, 9),
                rights: new Text(type:'html', text:'&copy; 1984 Fred Flintstone'))
        entry.categories.add(new Category(term:'Foo'))
        entry.categories.add(new Category(term:'Bar'))
        entry.contributors.add(new Person(name:'Flintstone, Wilma'))
        entry.contributors.add(new Person(name:'Rubble, Barney'))
        
        String xml = new XML(entry).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}
