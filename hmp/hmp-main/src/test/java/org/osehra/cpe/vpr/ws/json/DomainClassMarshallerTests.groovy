package org.osehra.cpe.vpr.ws.json

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.feed.atom.json.LinkMarshaller
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.PatientFlag
import org.osehra.cpe.vpr.ResultOrganizer
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.vpr.ws.link.LinkRelation
import grails.converters.JSON
import org.codehaus.groovy.grails.support.proxy.DefaultProxyHandler

class DomainClassMarshallerTests {

    protected void setUp() {


        def mockUrlCreator = { Object o ->
            if (o instanceof Patient)
                return [new Link(rel: LinkRelation.SELF, href: 'bar')]
            else
                return [new Link(rel: LinkRelation.SELF, href: 'foo')];
        } as ILinkService

        JSON.registerObjectMarshaller(new DomainClassMarshaller(proxyHandler: new DefaultProxyHandler(), linkService: mockUrlCreator), 1)
        JSON.registerObjectMarshaller(new LinkMarshaller())
    }

    void testAfterPropertiesSet() {

    }

    void testMarshalPatientRelated() {
        Patient pt = new Patient(icn: '12345')
//        pt.addToFacilities(code:'500', name:'CAMP MASTER')

        ResultOrganizer lab = new ResultOrganizer(
                uid: 'urn:va:lab:500:317:CH;6919868.919987',
                localId: 'CH;6919868.919987',
                patient: pt,
                resultStatusCode: 'completed',
                resultStatusName: 'Completed',
                specimen: "BLOOD",
                organizerType: "accession",
                version: 1
        )
        lab.addToResults(
                uid: 'urn:va:lab:500:317:CH;6919868.919987;386',
                localId: 'CH;6919868.919987;386',
                typeName: 'HGB',
                result: '8.0',
                units: 'g/dL',
                high: '18',
                low: '14',
                interpretationCode: 'LL',
                interpretationName: 'Low',
                version: 1)

        // this should be handled by addToResults(), shouldn't it?  Maybe a bug in Grails MockUnit?
        lab.results.toList()[0].organizers = new HashSet()
        lab.results.toList()[0].organizers.add(lab)

        def expected = '''{
    "domain": "result_organizer",
    "link":[{"rel":"self","href":"foo"},{"rel":"http://vaww.cpe.DOMAIN.EXT/rels/patient","href":"bar"}],
    "localId":"CH;6919868.919987",
    "organizerType": "accession",
    "resultStatus": {
        "class":"org.osehra.cpe.codes.ResultStatus",
        "domain": "result_status",
        "link": {
            "rel":"self",
            "href":"foo"
        },
        "code":"completed",
        "name":"Completed"
    },
    "results":[{
        "accession": {
            "_ref":"../..",
            "domain":"observation_interpretation"
        },
        "domain": "result",
        "link":[{"rel":"self","href":"foo"},{"rel":"http://vaww.cpe.DOMAIN.EXT/rels/patient","href":"bar"}],
        "high":"18",
        "kind":"Unknown",
        "localId":"CH;6919868.919987;386",
        "low":"14",
        "result":"8.0",
        "resultStatus": {
            "domain":"result_status",
            "link": {
                "rel":"self",
                "href":"foo"
            },
            "code":"completed",
            "name":"Completed"
        },
        "interpretation": {
            "domain":"observation_interpretation"
            "link": {
                "rel":"self",
                "href":"foo"
            },
            "code":"LL",
            "name":"Low"
        },
        "organizers":[{
            "_ref":"../../..",
            "domain":"result_organizer"
        }],
        "typeName":"HGB",
        "qualifiedName": "HGB (BLOOD)",
        "summary": "HGB (BLOOD) 8.0LL g/dL",
        "specimen":"BLOOD",
        "uid":"urn:va:lab:500:317:CH;6919868.919987;386",
        "units":"g/dL"
    }],
    "specimen": "BLOOD",
    "uid":"urn:va:lab:500:317:CH;6919868.919987",
}
'''
        String json = (lab as JSON).toString()
        assertJsonEquals(expected.toString(), json)
    }

    void testMarshalPatientOwned() {
        Patient pt = new Patient(icn: '12345')
        PatientFlag flag = new PatientFlag(name: 'BEHAVIORAL', text: 'shows signs of potential violence')
        pt.addToFlags(flag)

        def expected = '''{
"domain": "patient_flag",
"link": {
    "rel":"self",
    "href":"foo"
},
"name": "BEHAVIORAL",
"text": "shows signs of potential violence",
}
'''
        String json = (flag as JSON).toString()
        assertJsonEquals(expected.toString(), json)
    }
}
