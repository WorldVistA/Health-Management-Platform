package org.osehra.cpe.vpr.ws.feed

/**
 * Created by IntelliJ IDEA.
 * User: vhaislpuleoa
 * Date: Oct 6, 2010
 * Time: 4:31:49 PM
 * To change this template use File | Settings | File Templates.
 */

import org.osehra.cpe.vpr.Immunization
import org.osehra.cpe.feed.atom.Entry
import org.springframework.beans.factory.InitializingBean
import org.osehra.cpe.vpr.LinkService
import org.springframework.util.Assert
import org.osehra.cpe.feed.atom.Text
import org.osehra.cpe.feed.atom.Link
import org.springframework.core.convert.converter.Converter

class ImmunizationToAtomEntry implements Converter<Immunization, Entry>, InitializingBean{
      LinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, 'linkService must not be null')
    }
  Entry convert(Immunization i) {
    Entry e = new Entry()

    e.id = linkService.getSelfHref(i)
    e.title = new Text("${i.name}")
    //e.author = ("${i.performer}")
    def str = ''
    if (i.reaction) str = 'reaction ' + i.reaction
    if (i.contraindicated.toInteger() > 0) {
        if (str)
            str = str + 'contraindicated ' + i.contraindicated
        else str = ' contraindicated ' + i.contraindicated
    }
    if (i.cptCode) {
        if (str)
            str = str + 'CPT code ' + i.cptCode
        else str = ' CPT Code ' + i.cptCode
    }
    //e.summary = new Text("${i.reaction} ${i.contraindicated} ${i.cptCode}")
    e.summary = new Text("${str}")
    e.updated = i.administeredDateTime
    e.published = i.administeredDateTime

    e.link = new Link(rel: 'alternate', type: 'application/xml', href: linkService.getSelfHref(i))
    e.addToCategories(term: i.name)
    return e
  }
}
