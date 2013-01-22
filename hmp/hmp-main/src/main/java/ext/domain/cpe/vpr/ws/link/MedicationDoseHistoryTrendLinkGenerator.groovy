package EXT.DOMAIN.cpe.vpr.ws.link

import EXT.DOMAIN.cpe.vpr.Medication
import EXT.DOMAIN.cpe.feed.atom.Link

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import grails.util.GrailsNameUtils
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject
import EXT.DOMAIN.cpe.vpr.Patient

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
