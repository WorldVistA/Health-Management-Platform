package org.osehra.cpe.vpr

import org.osehra.cpe.test.MockGrailsApplicationUnitTestCase
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.codecs.URLCodec
import org.springframework.context.support.StaticApplicationContext
import org.osehra.cpe.vpr.ws.link.PatientRelatedSelfLinkGenerator

//class LinkServiceTests extends MockGrailsApplicationUnitTestCase {
class LinkServiceTests {
//TODO - fix this.
//    LinkService s
//
//    protected void setUp() {
//        super.setUp()
//
//        loadCodec(URLCodec)
//
//        mockDomain(Patient)
//        mockDomain(Result)
//        mockDomain(ResultOrganizer)
//        //mockDomain(ObservationInterpretation)
//
//        s = new LinkService()
////        s.grailsApplication = this.grailsApplication
////        s.grailsApplication.mainContext.registerSingleton('foo', PatientRelatedSelfLinkGenerator)
//
////        def config = new ConfigObject()
////        config.grails.serverURL = 'http://www.example.com/foo'
////        ConfigurationHolder.config = config
//    }
//
//    protected void tearDown() {
//        super.tearDown()
//    }
//
//    void testCreatePatientUrl() {
//        assertEquals('/vpr/v1/12345', s.getSelfHref(new Patient(icn: '12345')))
//    }
//
//    void testCreateCodedValueUrl() {
//        assertEquals('http://www.example.com/foo/codes/observationInterpretation/L', s.getSelfHref(new ObservationInterpretation(code: 'L', name: 'Low')))
//    }
//
//    void testCreatePatientRelatedUrl() {
//        assertEquals('/vpr/v1/12345/result/show/urn%3Ava%3Alab%3A500%3A229%3ACH%3B6898876.885%3B2', s.getSelfHref(new Result(uid: 'urn:va:lab:500:229:CH;6898876.885;2', localId: 'CH;6898876.885;2', organizers: [new ResultOrganizer(patient: new Patient(icn: '12345'))])))
//    }
}
