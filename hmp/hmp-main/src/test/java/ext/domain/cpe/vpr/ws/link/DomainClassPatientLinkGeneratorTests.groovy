package EXT.DOMAIN.cpe.vpr.ws.link

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.Document
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.PatientFacility
import EXT.DOMAIN.cpe.vpr.ResultOrganizer
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class DomainClassPatientLinkGeneratorTests {

    static final String MOCK_PATIENT_URL = "http://www.example.org/foo/v1/12345"

    DomainClassPatientLinkGenerator generator

    @Before
    void setUp() {
        generator = new DomainClassPatientLinkGenerator()
        generator.linkService = { getPatientHref: MOCK_PATIENT_URL } as ILinkService
        generator.omitClasses = [PatientFacility]
        generator.afterPropertiesSet()
    }

    @Test
    void testSupports() {
        assertTrue(generator.supports(new ResultOrganizer()))
        assertTrue(generator.supports(new Document()))
        assertFalse(generator.supports(new Patient()))
    }

    @Test
    void testGenerateLink() {
        Link link = generator.generateLink(new Document(uid: 'urn:va:tiu:500:4064', localId: '4064', patient: new Patient(icn: '12345')))
        assertEquals(LinkRelation.PATIENT.toString(), link.rel)
        assertEquals(MOCK_PATIENT_URL, link.href)
    }

    @Test
    void testGenerateLinkForOmittedClassIsNull() {
        Link link = generator.generateLink(new PatientFacility([code: '500', name: "CAMP MASTER", localPatientId: '4064']))
        assertNull(link)
    }
}
