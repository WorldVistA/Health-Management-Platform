package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.feed.atom.Link
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.osehra.cpe.jsonc.JsonCCollection
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class PreviousPageLinkGeneratorTests {

    PreviousPageLinkGenerator generator

    @Before
    void setUp() {
        generator = new PreviousPageLinkGenerator()
    }

    @Test
    void testSupports() {
        assertFalse(generator.supports(JsonCCollection.create(createMockPage(0, 20, 42))))
        assertTrue(generator.supports(JsonCCollection.create(createMockPage(20, 20, 42))))
        assertTrue(generator.supports(JsonCCollection.create(createMockPage(40, 20, 42))))
        assertFalse(generator.supports(1..10))
    }

    @Test
    void testGenerateLink() {
        Page mockPage = createMockPage(40, 20, 42)
        JsonCCollection jsonc = JsonCCollection.create(mockPage);
        jsonc.setSelfLink("http://www.example.org/mock/collection");

        Link link = generator.generateLink(jsonc)
        assertEquals(LinkRelation.PREVIOUS.toString(), link.rel)
        assertEquals("http://www.example.org/mock/collection?startIndex=20&count=20", link.href)
    }

    private Page createMockPage(int startIndex, int itemsPerPage, int total) {
        return new PageImpl(startIndex..(startIndex+Math.min(itemsPerPage-1, total-startIndex)), new PageRequest((int) (startIndex / itemsPerPage), itemsPerPage), total)
    }
}
