package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.*
import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*
import org.osehra.cpe.vpr.pom.IPatientDAO

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class DomainClassSelfLinkGeneratorTests {

    PatientRelatedSelfLinkGenerator generator

    IPatientDAO mockPatientDao

    @Before
    void setUp() {
        mockPatientDao = mock(IPatientDAO.class)

        generator = new PatientRelatedSelfLinkGenerator()
        generator.patientDao = mockPatientDao;
    }

    @Test
    void testSupports() {
        assertTrue(generator.supports(new Patient(icn: '12345')))
        assertTrue(generator.supports(new Result()))
        assertFalse(generator.supports("foobar"))
    }

    @Test
    void testGenerateLinkForPatient() {
        Link link = generator.generateLink(new Patient(icn: '12345'))
        assertEquals(LinkRelation.SELF.toString(), link.rel)
        assertEquals("/vpr/v1/12345", link.href)
    }

    @Test
    void testGenerateLinkForPatientRelatedDomainObject() {
        Patient mockPatient = new Patient(pid: "42", icn: '12345')
        when(mockPatientDao.findByVprPid("42")).thenReturn(mockPatient)

        Link link = generator.generateLink(new Document([pid: "42", uid: 'urn:va:tiu:500:4064', localId: '4064', patient: mockPatient]))
        assertEquals(LinkRelation.SELF.toString(), link.rel)
        assertEquals("/vpr/v1/12345/document/show/urn%3Ava%3Atiu%3A500%3A4064", link.href)
    }

}
