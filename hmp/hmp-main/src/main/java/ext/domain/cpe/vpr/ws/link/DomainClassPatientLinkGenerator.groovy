package EXT.DOMAIN.cpe.vpr.ws.link

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class DomainClassPatientLinkGenerator implements ILinkGenerator, InitializingBean {

	@Autowired
    ILinkService linkService

    List<Class> omitClasses

    void afterPropertiesSet() {
        Assert.notNull(linkService, "linkService must not be null")
    }

    boolean supports(Object object) {
        return object instanceof IPatientObject && !(object instanceof Patient);
    }

    Link generateLink(Object object) {
        if (omitClasses?.contains(object.class)) return null

        IPatientObject patientRelated = (IPatientObject)object;
        String href = linkService.getPatientHref(patientRelated.getPid());
        return new Link(rel: LinkRelation.PATIENT.toString(), href: href)
    }
}
