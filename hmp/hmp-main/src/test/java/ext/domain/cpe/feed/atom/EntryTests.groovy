package org.osehra.cpe.feed.atom

import grails.test.GrailsUnitTestCase
import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.feed.atom.Entry
import org.osehra.cpe.feed.atom.Person

class EntryTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp();
    }

    void testAuthor() {
        Entry e = new Entry()
        assertNull e.author

        Person a = new Person(name: 'Foo')
        e.author = a
        assertSame a, e.author

        e.authors.add(new Person(name: 'Bar'))
        try {
            e.author
            fail('expected exception')
        } catch (UnsupportedOperationException ex) {
            // NOOP
        }
    }

    void testLink() {
        Entry e = new Entry()
        assertNull e.link

        Link l = new Link(href: 'http://www.example.org')
        e.link = l
        assertSame l, e.link

        e.links.add(new Link(href: 'http://www.google.com'))
        try {
            e.link
            fail('expected exception')
        } catch (UnsupportedOperationException ex) {
            // NOOP
        }
    }

    void testCompareTo() {
        Entry e1 = new Entry(updated: new PointInTime(1984, 3, 11))
        Entry e2 = new Entry(updated: new PointInTime(1975, 7, 23))

        assertTrue(e1.compareTo(e2) < 0)
        assertTrue(e2.compareTo(e1) > 0)
        assertTrue(e2.compareTo(e2) == 0)
    }
}
