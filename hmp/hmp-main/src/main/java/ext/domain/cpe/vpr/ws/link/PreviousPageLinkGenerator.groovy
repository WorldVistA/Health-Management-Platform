package org.osehra.cpe.vpr.ws.link

import org.springframework.stereotype.Component;

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.jsonc.JsonCCollection

// TODO: I suspect this logic should get folded into JsonCCollection somehow
@Component
class PreviousPageLinkGenerator implements ILinkGenerator {

    boolean supports(Object object) {
        if (!(object instanceof JsonCCollection)) return false

        JsonCCollection cr = object as JsonCCollection
        return cr.startIndex > 0
    }

    Link generateLink(Object object) {
        JsonCCollection cr = object as JsonCCollection
        String url = getUrl(cr)
        if (!url)
            return null
        else
            return new Link(rel: LinkRelation.PREVIOUS.toString(), href: "${url}${url.contains('?') ? '&' : '?'}startIndex=${Math.max(cr.startIndex - cr.itemsPerPage, 0)}&count=${cr.itemsPerPage}")
    }

    private String getUrl(JsonCCollection jsonc) {
        return jsonc.getSelfLink()
    }
}
