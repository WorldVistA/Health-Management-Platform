package EXT.DOMAIN.cpe.vpr.ws.link

import EXT.DOMAIN.cpe.vpr.Result
import EXT.DOMAIN.cpe.vpr.Medication
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import EXT.DOMAIN.cpe.vpr.ResultOrganizer
import EXT.DOMAIN.cpe.vpr.Patient
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class ResultTrendLinkGeneratorTests {

    ResultTrendLinkGenerator generator

    @Before
    void setUp() {
        generator = new ResultTrendLinkGenerator()
        generator.linkService = {"http://www.example.org/foo/12345"} as ILinkService
    }

    @Test
    void testAfterPropertiesSet() {
        try {
            generator.linkService = null
            generator.afterPropertiesSet()
            fail("expected illegal arg exception")
        } catch (Exception e) {
            // NOOP
        }
        generator.linkService = {} as ILinkService
        generator.afterPropertiesSet()
    }

    @Test
    void testSupports() {
        assertTrue(generator.supports(new Result()))
        assertFalse(generator.supports(new Medication()))
    }

    @Test
    void testGenerateLinkWithTypeName() {
        Link link = generator.generateLink(new Result(typeName: "LDL CHOLESTEROL", organizers: [new ResultOrganizer(patient: new Patient(icn: "12345"))]))

        assertEquals(LinkRelation.TREND.toString(), link.rel)
        assertEquals("http://www.example.org/foo/12345/result/all?typeName=LDL%20CHOLESTEROL", link.href)
    }

    @Test
    void testGenerateLinkWithTypeCode() {
        Link link = generator.generateLink(new Result(typeCode: "urn:lnc:22748-8", organizers: [new ResultOrganizer(patient: new Patient(icn: "12345"))]))

        assertEquals(LinkRelation.TREND.toString(), link.rel)
        assertEquals("http://www.example.org/foo/12345/result/all?typeCode=urn:lnc:22748-8", link.href)
    }
}
