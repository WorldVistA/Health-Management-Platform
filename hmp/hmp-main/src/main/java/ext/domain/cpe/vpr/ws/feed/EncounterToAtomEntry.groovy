package EXT.DOMAIN.cpe.vpr.ws.feed

import EXT.DOMAIN.cpe.feed.atom.Entry
import EXT.DOMAIN.cpe.vpr.Encounter
import org.springframework.core.convert.converter.Converter
import EXT.DOMAIN.cpe.vpr.EncounterProvider
import EXT.DOMAIN.cpe.feed.atom.Text

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.LinkService
import org.springframework.util.Assert
import org.springframework.beans.factory.InitializingBean

class EncounterToAtomEntry implements Converter<Encounter, Entry>, InitializingBean {

    LinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, 'linkService must not be null')
    }

    Entry convert(Encounter e) {
        Entry entry = new Entry()
        entry.id = linkService.getSelfHref(e)
        entry.title = new Text("${e.typeName}, ${e.location}")
        if (e.stay) {
            entry.published = e.stay.arrivalDateTime
            entry.updated = e.stay.dischargeDateTime
        } else {
            entry.updated = e.dateTime
        }
        for (EncounterProvider p: e.providers) {
            entry.addToAuthors(name: p.clinician.name)
        }
        entry.link = new Link(rel: 'alternate', type: 'application/xml', href: linkService.getSelfHref(e))
        if (e.patientClass) entry.addToCategories(term: e.patientClass.name)
        if (e.location) entry.addToCategories(term: e.location)
        if (e.reason) entry.addToCategories(term: e.reason)
        entry.addToCategories(term: e.facilityCode, label: e.facilityName)
        return entry;
    }

}
