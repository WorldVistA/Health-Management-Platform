package org.osehra.cpe.feed.atom.xml

import org.osehra.cpe.datetime.PointInTime
import grails.converters.XML
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.osehra.cpe.feed.atom.*

class FeedMarshallerTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp();

        loadCodec HTMLCodec

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new TextMarshaller(), 1)
        XML.registerObjectMarshaller(new LinkMarshaller(), 1)
        XML.registerObjectMarshaller(new PersonMarshaller(), 1)
        XML.registerObjectMarshaller(new CategoryMarshaller(), 1)
        XML.registerObjectMarshaller(new EntryMarshaller(), 1)
        XML.registerObjectMarshaller(new FeedMarshaller(), 1)
    }

    void testSupports() {
        FeedMarshaller m = new FeedMarshaller()
        assertTrue(m.supports(new Feed()))
    }

    void testMarshalMinimalFeed() {
        def expected = '''
    <feed xmlns="http://www.w3.org/2005/Atom">
        <id>http://example.com/blog/1234</id>
        <title type="text">Atom-Powered Robots Run Amok</title>
        <updated>1975-07-23T10:13:34</updated>
    </feed>
    '''
        String xml = new XML(new Feed(
                id: 'http://example.com/blog/1234',
                title: new Text('Atom-Powered Robots Run Amok'),
                updated: new PointInTime(1975, 7, 23, 10, 13, 34, 0))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    void testMarshalFullFeed() {
        def expected = '''
    <feed xmlns="http://www.w3.org/2005/Atom">
        <id>http://example.com/blog/1234</id>
        <title type="text">Atom-Powered Robots Run Amok</title>
        <updated>1975-07-23T10:13:34</updated>
        <author>
            <name>John Doe</name>
            <email>JohnDoe@example.com</email>
            <uri>http://example.com/~johndoe</uri>
        </author>
        <link rel="self" href="/feed"/>   
        <generator uri="/myblog.php" version="1.0">Example Toolkit</generator>
        <subtitle type="text">all your examples are belong to us</subtitle>
        <category term="sports"/>
        <contributor>
            <name>Jane Doe</name>
        </contributor>
        <icon>/icon.jpg</icon>
        <logo>/logo.jpg</logo>
        <rights type="html">&amp;amp;copy; 2005 John Doe</rights>
        <entry>
            <id>http://example.com/blog/1234/entries/5678</id>
            <title type="text">Hello world</title>
            <updated>1975-07-23T10:13:34</updated>
        </entry>
    </feed>
    '''
        Feed feed = new Feed(
                id: 'http://example.com/blog/1234',
                title: new Text('Atom-Powered Robots Run Amok'),
                updated: new PointInTime(1975, 7, 23, 10, 13, 34, 0),
                author: new Person(name: 'John Doe', email: 'JohnDoe@example.com', uri: 'http://example.com/~johndoe'),
                link: new Link(rel: 'self', href: "/feed"),
                generator: new Generator(uri: '/myblog.php', version: '1.0', text: 'Example Toolkit'),
                icon: '/icon.jpg',
                logo: '/logo.jpg',
                rights: new Text(text: '&copy; 2005 John Doe', type: "html"),
                subtitle: new Text('all your examples are belong to us'))
        feed.categories.add(new Category(term: 'sports'))
        feed.contributors.add(new Person(name: 'Jane Doe'))

        feed.entries.add(new Entry(id: 'http://example.com/blog/1234/entries/5678', title: new Text('Hello world'), updated: new PointInTime(1975, 7, 23, 10, 13, 34, 0)))

        String xml = new XML(feed).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}
