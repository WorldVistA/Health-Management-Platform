package org.osehra.cpe.vpr

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import groovy.text.GStringTemplateEngine

import org.springframework.util.Assert

class UidUtils {

    private static Map<String, String> DOMAIN_TO_UID_TEMPLATES = [
            allergy: 'urn:va:${vistaSystemId}:${localPatientId}:art:${localId}',
            appointment: 'urn:va:${vistaSystemId}:${localPatientId}:appt:${localId}',
            consult: 'urn:va:${vistaSystemId}:${localPatientId}:cons:${localId}',
            document: 'urn:va:${vistaSystemId}:${localPatientId}:tiu:${localId}',
            encounter: 'urn:va:${vistaSystemId}:${localPatientId}:visit:${localId}',
            factor: 'urn:va:${vistaSystemId}:${localPatientId}:hf:${localId}',
            immunization: 'urn:va:${vistaSystemId}:${localPatientId}:imm:${localId}',
            lab: 'urn:va:${vistaSystemId}:${localPatientId}:lab:${localId}',
            medication: 'urn:va:${vistaSystemId}:${localPatientId}:med:${medIdOrOrderId}',
            observation: 'urn:va:${vistaSystemId}:${localPatientId}:obs:${localId}',
            order: 'urn:va:${vistaSystemId}:${localPatientId}:order:${localId}',
            problem: 'urn:va:${vistaSystemId}:${localPatientId}:prob:${localId}',
            procedure: 'urn:va:${vistaSystemId}:${localPatientId}:proc:${localId}',
            patient: 'urn:va:${vistaSystemId}:${localId}:pat:${localId}',
            radiology: 'urn:va:${vistaSystemId}:${localPatientId}:rad:${localId}',
            surgery: 'urn:va:${vistaSystemId}:${localPatientId}:surgery:${localId}',
            user: 'urn:va:user:${vistaSystemId}:${localId}',
            task: 'urn:va:${vistaSystemId}:${localPatientId}:task:${localId}',
            visit: 'urn:va:${vistaSystemId}:${localPatientId}:visit:${localId}',
            vitalSignOrganizer: 'urn:va:${vistaSystemId}:${localPatientId}:vs:${locationId}:${entered}',
            vitalSign: 'urn:va:${vistaSystemId}:${localPatientId}:vs:${localId}',
    ]

    private static Map UID_PATTERN_TO_DOMAIN_CLASS = [
			/urn:va:.*:.*:alert:.*/: PatientAlert,
            /urn:va:.*:.*:art:.*/: Allergy,
            /urn:va:.*:.*:cons:.*/: Procedure,
            /urn:va:.*:.*:tiu:.*/: Document,
            /urn:va:.*:.*:visit:.*/: Encounter,
            /urn:va:.*:.*:appt:.*/: Encounter,
            /urn:va:.*:.*:hf:.*/: HealthFactor,
            /urn:va:.*:.*:imm:.*/: Immunization,
            /urn:va:.*:.*:med:.*/: Medication,
            /urn:va:.*:.*:obs:.*/: Observation,
            /urn:va:.*:.*:order:.*/: Order,
            /urn:va:.*:.*:prob:.*/: Problem,
            /urn:va:.*:.*:pat/: Patient,
            /urn:va:.*:.*:proc:.*/: Procedure,
            /urn:va:.*:.*:surgery:.*/: Procedure,
            /urn:va:.*:.*:rad:.*/: Procedure,
            /urn:va:.*:.*:lab:.*/: Result,
            /urn:va:.*:.*:task:.*/: Task,
            /urn:va:.*:.*:vs:.*:.*/: VitalSignOrganizer,
            /urn:va:.*:.*:vs:.*/: VitalSign,
    ]

    private static SimpleTemplateEngine templateEngine

    private static String getUid(String domain, Map params) {
        assert DOMAIN_TO_UID_TEMPLATES.containsKey(domain)

        Template t = new GStringTemplateEngine(UidUtils.classLoader).createTemplate(DOMAIN_TO_UID_TEMPLATES[domain]);
        StringWriter sw = new StringWriter()
        t.make(params).writeTo(sw)
        return sw.toString()
    }

    private static String getDefaultDomainUid(String domain, String vistaSystemId, String localPatientId, String localId) {
        Assert.hasText(domain, "[Assertion failed] - the 'domain' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(vistaSystemId, "[Assertion failed] - the 'vistaSystemId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(localPatientId, "[Assertion failed] - the 'localPatientId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(localId, "[Assertion failed] - the 'localId' argument must have text; it must not be null, empty, or blank");

        getUid(domain, [vistaSystemId: vistaSystemId, localPatientId: localPatientId, localId: localId])
    }

    static String getAllergyUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("allergy", vistaSystemId, localPatientId, localId);
    }

    static String getAppointmentUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("appointment", vistaSystemId, localPatientId, localId);
    }

    static String getConsultUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("consult", vistaSystemId, localPatientId, localId);
    }

    static String getDocumentUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("document", vistaSystemId, localPatientId, localId);
    }

    static String getHealthFactorUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("factor", vistaSystemId, localPatientId, localId);
    }

    static String getImmunizationUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("immunization", vistaSystemId, localPatientId, localId);
    }

    static String getMedicationUid(String vistaSystemId, String localPatientId, String medIdOrOrderId) {
        Assert.hasText(vistaSystemId, "[Assertion failed] - the 'vistaSystemId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(localPatientId, "[Assertion failed] - the 'localPatientId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(medIdOrOrderId, "[Assertion failed] - the 'medIdOrOrderId' argument must have text; it must not be null, empty, or blank");

        return getUid('medication', [vistaSystemId: vistaSystemId, localPatientId: localPatientId, medIdOrOrderId: medIdOrOrderId])
    }

    static String getOrderUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("order", vistaSystemId, localPatientId, localId);
    }

    static String getObservationUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("observation", vistaSystemId, localPatientId, localId);
    }

    static String getPatientUid(String vistaSystemId, String localId) {
        return getUid('patient', [vistaSystemId: vistaSystemId, localId: localId])
    }

    static String getProblemUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("problem", vistaSystemId, localPatientId, localId);
    }

    static String getProcedureUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("procedure", vistaSystemId, localPatientId, localId);
    }

    static String getSurgeryUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("surgery", vistaSystemId, localPatientId, localId);
    }

    static String getResultUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("lab", vistaSystemId, localPatientId, localId);
    }

    static String getResultOrganizerUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("lab", vistaSystemId, localPatientId, localId);
    }

    static String getRadiologyUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("radiology", vistaSystemId, localPatientId, localId);
    }

    static String getUserUid(String vistaSystemId, String localId) {
        return getUid('user', [vistaSystemId: vistaSystemId, localId: localId])
    }

    static String getVisitUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("visit", vistaSystemId, localPatientId, localId);
    }

    static String getVitalSignOrganizerUid(String vistaSystemId, String localPatientId, String locationId, String entered) {
        Assert.hasText(vistaSystemId, "[Assertion failed] - the 'vistaSystemId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(localPatientId, "[Assertion failed] - the 'localPatientId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(locationId, "[Assertion failed] - the 'locationId' argument must have text; it must not be null, empty, or blank");
        Assert.hasText(entered, "[Assertion failed] - the 'entered' argument must have text; it must not be null, empty, or blank");

        getUid('vitalSignOrganizer', [vistaSystemId: vistaSystemId, localPatientId: localPatientId, locationId: locationId, entered: entered])
    }

    static String getVitalSignUid(String vistaSystemId, String localPatientId, String localId) {
        return getDefaultDomainUid("vitalSign", vistaSystemId, localPatientId, localId);
    }

    static Class getDomainClassByUid(String uid) {
        def matchingEntry = UID_PATTERN_TO_DOMAIN_CLASS.find { Map.Entry it ->
            uid ==~ it.key
        }
        matchingEntry?.value
    }
	
	static String getVistaClassNameByUid(String uid) {
		String[] splt = uid.split(":");
		if(splt.length>4) {
			return splt[4];
		}
		return "";
	}
}
