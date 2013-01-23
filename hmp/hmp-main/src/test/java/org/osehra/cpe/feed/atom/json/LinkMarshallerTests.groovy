package org.osehra.cpe.feed.atom.json

import org.osehra.cpe.feed.atom.Link
import grails.converters.JSON
import org.osehra.cpe.test.MockGrailsApplicationUnitTestCase

class LinkMarshallerTests extends MockGrailsApplicationUnitTestCase {

    protected void setUp() {
        super.setUp();

        JSON.registerObjectMarshaller(new LinkMarshaller())
    }

    void testSupports() {
        LinkMarshaller m = new LinkMarshaller()
        assertTrue(m.supports(new Link(rel: 'self', href: 'http://www.example.com')))
        assertFalse(m.supports(null))
        assertFalse(m.supports('foo'))
    }

    void testMarshalLink() {
        assertEquals('{"class":"org.osehra.cpe.feed.atom.json.Foo","link":{"rel":"self","href":"http://www.example.com"}}', new JSON(new Foo(link:new Link(rel: 'self', href: 'http://www.example.com'))).toString());
    }
}

class Foo {
    Link link
}
