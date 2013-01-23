package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.Medication
import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.vpr.ResultOrganizer
import org.osehra.cpe.vpr.Patient
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
