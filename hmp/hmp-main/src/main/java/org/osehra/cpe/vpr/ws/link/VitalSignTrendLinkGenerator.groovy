package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.VitalSign

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert
import org.osehra.cpe.vpr.mapping.ILinkService
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriComponents

@Component
class VitalSignTrendLinkGenerator implements ILinkGenerator {

    @Autowired
    ILinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, "'linkService' must not be null")
    }

    boolean supports(Object object) {
        return object instanceof VitalSign
    }

    Link generateLink(Object object) {
        VitalSign vitalSign = object as VitalSign
        String patientHref = linkService.getPatientHref(vitalSign.pid)
        UriComponents uriComponents
        if (vitalSign.typeCode) {
            uriComponents = UriComponentsBuilder.fromUriString("{base}/vital/all?typeCode={typeCode}").buildAndExpand([base: patientHref, typeCode: vitalSign.typeCode])
        } else {
            uriComponents = UriComponentsBuilder.fromUriString("{base}/vital/all?typeName={typeName}").buildAndExpand([base: patientHref, typeName: vitalSign.typeName])
        }
        return new Link(rel: LinkRelation.TREND, href: uriComponents.encode().toUriString())
    }
}
