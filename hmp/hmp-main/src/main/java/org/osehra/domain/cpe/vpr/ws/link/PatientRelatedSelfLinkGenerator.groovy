package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.feed.atom.Link

import org.osehra.cpe.vpr.Patient
import grails.util.GrailsNameUtils
import org.springframework.stereotype.Component
import org.osehra.cpe.vpr.pom.IPatientObject
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.springframework.beans.factory.annotation.Autowired;

@Component
class PatientRelatedSelfLinkGenerator implements ILinkGenerator {

    @Autowired
    IPatientDAO patientDao

    boolean supports(Object object) {
        return object instanceof IPatientObject;
    }

    Link generateLink(Object object) {
        String url = getSelfUrl(object)
        if (url)
            return new Link(rel: "self", href: url)
        else
            return null
    }

    private String getSelfUrl(Object o) {
        if (o == null) return null
        if (o instanceof Patient) {
            return getPatientHref(o.icn ?: o.pid)
        }

        IPatientObject patientRelated = (IPatientObject) o;
        Patient patient = patientDao.findByVprPid(patientRelated.pid);
        if (patient) {
            return getSelfHref(patient.icn ?: patient.pid, o.class, patientRelated.uid)
        }

        return null
    }

    static String getPatientHref(long pid) {
        return getPatientHref("" + pid);
    }

    static String getPatientHref(String pid) {
        return "/vpr/v1/${pid}"
    }

    static String getSelfHref(String pid, Class clazz, String uid) {
        return getSelfHref(pid, GrailsNameUtils.getPropertyName(clazz), uid)
    }

    static String getSelfHref(String pid, String domain, String uid) {
        uid = URLEncoder.encode(uid);
        return "${getPatientHref(pid)}/${domain}/show/${uid}"
    }
}
