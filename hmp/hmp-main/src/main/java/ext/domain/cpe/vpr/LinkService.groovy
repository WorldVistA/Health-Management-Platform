package org.osehra.cpe.vpr

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.vpr.ws.link.ILinkGenerator
import org.osehra.cpe.vpr.ws.link.PatientRelatedSelfLinkGenerator;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.osehra.cpe.vpr.pom.IPatientDAO

@Service
class LinkService implements ILinkService {

	@Autowired
	ApplicationContext applicationContext

    @Autowired
    IPatientDAO patientDao

    String getPatientHref(String pid) {
        Patient pt = patientDao.findByAnyPid(pid)
        return getSelfHref(pt)
    }

    String getSelfHref(Object o) {
        return getSelfLink(o)?.href
    }

    List<Link> getLinks(def domainObject) {
        if (!domainObject) return []
		Map<String, ILinkGenerator> generatorBeans = applicationContext.getBeansOfType(ILinkGenerator)
        //Map<String, ILinkGenerator> generatorBeans = grailsApplication.mainContext.getBeansOfType(ILinkGenerator)
        def generators = generatorBeans.values().findAll { ILinkGenerator g -> g.supports(domainObject) }
        List<Link> links = []
        generators.each { ILinkGenerator g ->
            Link link = g.generateLink(domainObject)
            if (link) links.add(link)
        }
        return links
    }

    Link getSelfLink(def domainObject) {
        ILinkGenerator generator = applicationContext.getBean(PatientRelatedSelfLinkGenerator)
        if (generator.supports(domainObject))
            return generator.generateLink(domainObject)
        else
            return null
    }
}
