package EXT.DOMAIN.cpe.feed.atom

import grails.test.GrailsUnitTestCase
import EXT.DOMAIN.cpe.feed.atom.Link


class LinkTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp();

        mockForConstraintsTests Link
    }

    void testHrefRequired() {
       Link l = new Link()
       assertFalse(l.validate())
       l.href = "http://www.example.com"
       assertTrue(l.validate())
    }
}
