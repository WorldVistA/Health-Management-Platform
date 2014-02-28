package org.osehra.cpe.vpr

import org.osehra.cpe.feed.atom.Feed
import org.osehra.cpe.feed.atom.Person
import org.springframework.core.convert.ConversionService
import org.osehra.cpe.feed.atom.Entry
import org.osehra.cpe.feed.atom.Text

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.ws.link.LinkRelation
import org.osehra.cpe.datetime.PointInTime

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
