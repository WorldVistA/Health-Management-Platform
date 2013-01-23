package org.osehra.cpe.feed.atom

import grails.test.GrailsUnitTestCase
import org.osehra.cpe.feed.atom.Category

class CategoryTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp();

        mockForConstraintsTests Category
    }

    void testTermIsRequired() {
        Category c = new Category()
        assertFalse c.validate()
        c = new Category(term:'foo')
        assertTrue c.validate()
    }
}
