package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.vpr.Medication
import org.osehra.cpe.feed.atom.Link

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert
import org.osehra.cpe.vpr.mapping.ILinkService
import grails.util.GrailsNameUtils
import org.osehra.cpe.vpr.pom.IPatientObject
import org.osehra.cpe.vpr.Patient

@Component
class MedicationDoseHistoryTrendLinkGenerator implements ILinkGenerator {

	@Autowired
    ILinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, "'linkService' must not be null")
    }

    boolean supports(Object object) {
        return object instanceof Medication
    }

    Link generateLink(Object object) {
        Medication med = object as Medication
        return new Link(rel: LinkRelation.TREND.toString(), href: "${linkService.getPatientHref(med.getPid())}/${GrailsNameUtils.getPropertyName(Medication)}/all?qualifiedName=${med.qualifiedName.encodeAsURL()}")
    }
}
