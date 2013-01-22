package EXT.DOMAIN.cpe.feed.atom

import grails.test.GrailsUnitTestCase

import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.feed.atom.Feed
import EXT.DOMAIN.cpe.feed.atom.Entry
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.feed.atom.Person


class FeedTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp();
    }

    void testAuthor() {
        Feed f = new Feed()
        assertNull f.author

        Person a = new Person(name: 'Foo')
        f.author = a
        assertSame a, f.author

        f.authors.add(new Person(name: 'Bar'))
        try {
            f.author
            fail('expected exception')
        } catch (UnsupportedOperationException ex) {
            // NOOP
        }
    }

    void testLink() {
        Feed f = new Feed()
        assertNull f.link

        Link l = new Link(href: 'http://www.example.org')
        f.link = l
        assertSame l, f.link

        f.links.add(new Link(href: 'http://www.google.com'))
        try {
            f.link
            fail('expected exception')
        } catch (UnsupportedOperationException ex) {
            // NOOP
        }
    }

    void testUpdated() {
        Feed f = new Feed()
        assertNull(f.updated)

        f.entries.add new Entry(updated:new PointInTime(1984, 3, 11, 22, 24, 56))
        f.entries.add new Entry(updated:new PointInTime(1993, 6, 4, 2, 19, 12))

        assertEquals(new PointInTime(1993, 6, 4, 2, 19, 12), f.updated)

        PointInTime lastUpdated = new PointInTime(1975, 07, 23, 10, 54, 23)
        f.updated = lastUpdated
        assertEquals(lastUpdated, f.updated)
    }
}
