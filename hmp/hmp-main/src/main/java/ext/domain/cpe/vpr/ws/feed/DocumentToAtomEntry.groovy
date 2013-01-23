package org.osehra.cpe.vpr.ws.feed

import org.osehra.cpe.feed.atom.Entry
import org.osehra.cpe.vpr.Document
import org.springframework.core.convert.converter.Converter
import org.osehra.cpe.feed.atom.Text
import org.osehra.cpe.vpr.DocumentClinician

import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.vpr.LinkService
import org.springframework.util.Assert
import org.springframework.beans.factory.InitializingBean
import org.osehra.cpe.feed.atom.Content

class DocumentToAtomEntry implements Converter<Document, Entry>, InitializingBean {

    LinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, 'linkService must not be null')
    }

    Entry convert(Document d) {
        Entry e = new Entry()
        e.id = linkService.getSelfHref(d)
        e.title = new Text(d.localTitle)
        e.updated = d.referenceDateTime  // TODO: maybe find latest signedDate for this
        e.published = d.referenceDateTime
        for (DocumentClinician s: d.clinicians) {
            e.addToAuthors(name: s.clinician.name)
        }
        if (d.subject) e.summary = new Text(d.subject)
        e.link = new Link(rel: 'alternate', type: 'application/xml', href: linkService.getSelfHref(d))


//        def xml = new XmlSlurper().parseText("<content>${d.content}</content>");
        e.content = new Content()
        e.content.type = "xhtml"
        e.content.text = "<pre>${d.text.encodeAsHTML()}</pre>"
//        xml.addendum.each {
//            e.content.text += "<hr /><pre id='addendum-${it.@id}'>${it.text()}</pre>"
//        }

        if (d.documentClass) e.addToCategories(term: d.documentClass)
        if (d.status) e.addToCategories(term: d.status)
        //if (d.componentType) e.addToCategories(term: d.componentType)
        e.addToCategories(term:d.facilityCode, label:d.facilityName)
        return e;
    }

}
