package org.osehra.cpe.vpr

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.test.MockGrailsApplicationUnitTestCase
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.vpr.ws.link.LinkRelation
import grails.converters.JSON
import grails.converters.XML
import grails.test.ControllerUnitTestCase
import grails.validation.ValidationException
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.support.proxy.DefaultProxyHandler

class PatientDomainControllerTests {
//class PatientDomainControllerTests extends ControllerUnitTestCase {

//    GrailsApplication grailsApplication
//
//    protected void setUp() {
//        super.setUp()
//
//        def mockPatient = new Patient(id: 123456L, icn: 'foo', facilities: [] as SortedSet)
//        def mockAllergies = [new Allergy(patient: mockPatient, uid: 'urn:va:art:1A2B:ABCDEF')]
//
//        mockDomain(Patient, [mockPatient])
//        mockDomain(PatientFacility)
//        mockDomain(Allergy, mockAllergies)
//
//        Allergy.metaClass.'static'.withSession = {}
////        Allergy.metaClass.'static'.withCriteria = { Closure countClosure -> // mocks count()
//        //            return 1
//        //        }
//        Allergy.metaClass.'static'.forPatient = {
//            NamedCriteriaProxy mockCriteriaProxy = new NamedCriteriaProxy(domainClass: domainClassesInfo.getGrailsClassByLogicalPropertyName('allergy'))
//            mockCriteriaProxy.metaClass.count = {
//                return 1
//            }
//            mockCriteriaProxy.metaClass.listDistinct = {
//                return mockAllergies
//            }
//            return mockCriteriaProxy
//        }
//
//        mockForConstraintsTests(PatientDomainCommand)
//        mockForConstraintsTests(UidCommand)
//        mockForConstraintsTests(TemporalCommand)
//        mockForConstraintsTests(PaginatedCollectionCommand)
//
//        controller.grailsApplication = grailsApplication = MockGrailsApplicationUnitTestCase.createGrailsApplication(domainClassesInfo)
//
//        def mockUrlCreator = { Object o ->
//            if (o instanceof Patient)
//                return [new Link(rel: LinkRelation.SELF, href: 'bar')]
//            else
//                return [new Link(rel: LinkRelation.SELF, href: 'foo')];
//        } as ILinkService
//
//        HL7DateTimeConverters.registerJsonAndXmlMarshallers();
//        JSON.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.json.DomainClassMarshaller(proxyHandler: new DefaultProxyHandler(), linkService: mockUrlCreator), 1)
//        JSON.registerObjectMarshaller(new org.osehra.cpe.feed.atom.json.LinkMarshaller())
//        JSON.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.json.VprResponseMarshaller())
//        JSON.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.json.PaginatedCollectionResponseMarshaller())
//        XML.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.xml.VprResponseMarshaller())
//        XML.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.xml.PaginatedCollectionResponseMarshaller())
//        XML.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.xml.MapMarshaller())
//        XML.registerObjectMarshaller(new org.osehra.cpe.feed.atom.xml.LinkMarshaller())
//        XML.registerObjectMarshaller(new org.osehra.cpe.vpr.ws.xml.DomainClassMarshaller(proxyHandler: new DefaultProxyHandler(), linkService: mockUrlCreator), 1)
//    }
//
//    protected void tearDown() {
//        super.tearDown()
//    }
//
//    void testShow() {
//        controller.show(new PatientDomainCommand(pid: '123456', domain: 'allergy'), new UidCommand(uid: 'urn:va:art:1A2B:ABCDEF'))
//    }
//
//    void testShowWithUnknownDomain() {
//        shouldFail(UnknownDomainException) {
//            // this fails because there is no 'lab' domain class in the list of mockDomain() calls...
//            controller.show(new PatientDomainCommand(pid: '123456', domain: 'lab'), new UidCommand(uid: 'urn:va:lab:FEDCBA'))
//        }
//    }
//
//    void testShowWithMissingPid() {
//        shouldFail(ValidationException) {
//            controller.show(new PatientDomainCommand(domain: 'allergy'), new UidCommand(uid: null))
//        }
//    }
//
//    void testShowWithMissingUid() {
//        shouldFail(ValidationException) {
//            controller.show(new PatientDomainCommand(pid: '123456', domain: 'allergy'), new UidCommand(uid: null))
//        }
//    }
//
//    void testShowWithPatientNotFound() {
//        shouldFail(PatientNotFoundException) {
//            controller.show(new PatientDomainCommand(pid: '456789', domain: 'allergy'), new UidCommand(uid: 'urn:va:art:1A2B:ABCDEF'))
//        }
//    }
//
//    void testShowWithUidNotFound() {
//        shouldFail(UidNotFoundException) {
//            controller.show(new PatientDomainCommand(pid: '123456', domain: 'allergy'), new UidCommand(uid: 'urn:va:art:1A2B:FEDCBA'))
//        }
//    }
//
//    void testListWithUnknownDomain() {
//        shouldFail(UnknownDomainException) {
//            // this fails because there is no 'lab' domain class in the list of mockDomain() calls...
//            controller.list(new PatientDomainCommand(pid: '123456', domain: 'lab'), new TemporalCommand(), new PaginatedCollectionCommand())
//        }
//    }
//
//    void testListWithMissingPid() {
//        shouldFail(ValidationException) {
//            controller.list(new PatientDomainCommand(domain: 'allergy'), new TemporalCommand(), new PaginatedCollectionCommand())
//        }
//    }
//
//    void testListWithPatientNotFound() {
//        shouldFail(PatientNotFoundException) {
//            controller.list(new PatientDomainCommand(pid: '456789', domain: 'allergy'), new TemporalCommand(), new PaginatedCollectionCommand())
//        }
//    }
//
//    void testList() {
//        controller.list(new PatientDomainCommand(pid: '123456', domain: 'allergy'), new TemporalCommand(), new PaginatedCollectionCommand())
//    }

}
