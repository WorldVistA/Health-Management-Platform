package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.vpr.VitalSign
import org.osehra.cpe.vpr.Medication
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.VitalSignOrganizer
import org.osehra.cpe.vpr.Patient
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class VitalSignTrendLinkGeneratorTests {

    VitalSignTrendLinkGenerator generator

    @Before
    void setUp() {
        generator = new VitalSignTrendLinkGenerator()
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
        assertTrue(generator.supports(new VitalSign()))
        assertFalse(generator.supports(new Medication()))
    }

    @Test
    void testGenerateLinkWithTypeName() {
        Link link = generator.generateLink(new VitalSign(typeName: "BLOOD PRESSURE", organizer: new VitalSignOrganizer(patient: new Patient(icn: "12345"))))

        assertEquals(LinkRelation.TREND.toString(), link.rel)
        assertEquals("http://www.example.org/foo/12345/vital/all?typeName=BLOOD%20PRESSURE", link.href)
    }

    @Test
    void testGenerateLinkWithTypeCode() {
        Link link = generator.generateLink(new VitalSign(typeCode: "urn:vuid:4500634", organizer: new VitalSignOrganizer(patient: new Patient(icn: "12345"))))

        assertEquals(LinkRelation.TREND.toString(), link.rel)
        assertEquals("http://www.example.org/foo/12345/vital/all?typeCode=urn:vuid:4500634", link.href)
    }
}
