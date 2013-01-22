package EXT.DOMAIN.cpe.vpr

import EXT.DOMAIN.cpe.feed.atom.Feed
import EXT.DOMAIN.cpe.feed.atom.Person
import org.springframework.core.convert.ConversionService
import EXT.DOMAIN.cpe.feed.atom.Entry
import EXT.DOMAIN.cpe.feed.atom.Text

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.ws.link.LinkRelation
import EXT.DOMAIN.cpe.datetime.PointInTime

import org.springframework.stereotype.Service

@Service
class FeedService {

    boolean transactional = false

    LinkService linkService

    ConversionService vprConversionService

    Feed createFeed(Patient pt, String id, String title, List entries) {
        Feed f = new Feed()
        f.id = id
        f.title = new Text(title)
        f.updated = PointInTime.fromDateFields(pt.lastUpdated)
        f.author = new Person(name: "${pt.givenNames} ${pt.familyName}", uri: linkService.getSelfHref(pt))
        f.link = new Link(rel: LinkRelation.SELF.toString(), href: id, type: 'application/atom+xml')

        entries.each {
            Entry e = vprConversionService.convert(it, Entry)
            if (e) f.entries.add(e)
        }

        return f
    }
}
