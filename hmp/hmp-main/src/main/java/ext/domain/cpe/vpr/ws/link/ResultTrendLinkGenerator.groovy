package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.mapping.ILinkService
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriComponents

@Component
class ResultTrendLinkGenerator implements ILinkGenerator, InitializingBean {

	@Autowired
    ILinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, "'linkService' must not be null")
    }

    boolean supports(Object object) {
        return object instanceof Result
    }

    Link generateLink(Object object) {
        Result result = object as Result
        UriComponents uriComponents
        if (result.typeCode) {
            uriComponents = UriComponentsBuilder.fromUriString("${linkService.getPatientHref(result.pid)}/result/all?typeCode=${result.typeCode}").build()
        } else {
            uriComponents = UriComponentsBuilder.fromUriString("${linkService.getPatientHref(result.pid)}/result/all?typeName=${result.typeName}").build()
        }
        return new Link(rel: LinkRelation.TREND, href: uriComponents.encode().toUriString())
    }

}
