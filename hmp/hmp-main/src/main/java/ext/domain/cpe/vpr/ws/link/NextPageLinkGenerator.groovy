package EXT.DOMAIN.cpe.vpr.ws.link

import org.springframework.stereotype.Component;

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.jsonc.JsonCCollection

// TODO: I suspect this logic should get folded into JsonCCollection somehow
@Component
class NextPageLinkGenerator implements ILinkGenerator {

    boolean supports(Object object) {
        if (!(object instanceof JsonCCollection)) return false

        JsonCCollection cr = object as JsonCCollection
        return cr.startIndex + cr.itemsPerPage < cr.totalItems
    }

    Link generateLink(Object object) {
        JsonCCollection cr = object as JsonCCollection
        String url = getUrl(cr)
        if (!url)
            return null
        else
            return new Link(rel: LinkRelation.NEXT.toString(), href: "${url}${url.contains('?') ? '&' : '?'}startIndex=${cr.startIndex + cr.itemsPerPage}&count=${cr.itemsPerPage}")
    }

    private String getUrl(JsonCCollection jsonc) {
        return jsonc.getSelfLink()
    }
}
