package org.osehra.cpe.vpr.ws.feed

import org.osehra.cpe.vpr.Result
import org.osehra.cpe.feed.atom.Entry
import org.springframework.beans.factory.InitializingBean
import org.osehra.cpe.vpr.LinkService
import org.springframework.util.Assert
import org.osehra.cpe.feed.atom.Text
import org.osehra.cpe.feed.atom.Link
import org.springframework.core.convert.converter.Converter

class ResultToAtomEntry implements Converter<Result, Entry>, InitializingBean {

    LinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, 'linkService must not be null')
    }

    Entry convert(Result r) {
        Entry e = new Entry()

        e.id = linkService.getSelfHref(r)
        e.title = new Text("${r.typeName} ${r.result}${r.interpretation ? r.interpretation.code : ''} ${r.units ?: ''}")
        e.updated = r.resulted ?: r.observed

        if (r.observed) e.published = r.observed

        e.link = new Link(rel: 'alternate', type: 'application/xml', href: linkService.getSelfHref(r))

        if (r.interpretation) e.addToCategories(term: r.interpretation)
        if (r.resultStatus) e.addToCategories(term: r.resultStatus)

        e.addToCategories(term:r.facilityCode, label:r.facilityName)

        return e;
    }
}
