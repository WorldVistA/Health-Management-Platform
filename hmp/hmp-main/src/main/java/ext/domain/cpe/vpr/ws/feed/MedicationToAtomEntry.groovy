package EXT.DOMAIN.cpe.vpr.ws.feed

/**
 * Created by IntelliJ IDEA.
 * User: vhaislpuleoa
 * Date: Oct 21, 2010
 * Time: 4:45:22 PM
 * To change this template use File | Settings | File Templates.
 */

import EXT.DOMAIN.cpe.vpr.Medication
import EXT.DOMAIN.cpe.feed.atom.Entry
import org.springframework.beans.factory.InitializingBean
import EXT.DOMAIN.cpe.vpr.LinkService
import org.springframework.util.Assert
import EXT.DOMAIN.cpe.feed.atom.Text
import EXT.DOMAIN.cpe.feed.atom.Link
import org.springframework.core.convert.converter.Converter

import EXT.DOMAIN.cpe.datetime.PointInTime

class MedicationToAtomEntry implements Converter<Medication, Entry>, InitializingBean{
      LinkService linkService

    void afterPropertiesSet() {
        Assert.notNull(linkService, 'linkService must not be null')
    }
  Entry convert(Medication m) {
         Entry e = new Entry()

    e.id = linkService.getSelfHref(m)
    e.title = new Text("${m.qualifiedName}")
    def str = 'Status: '
    if (m.vaStatus) str = str + m.vaStatus
    else str = str + 'unknown'
    e.summary = new Text("${m.sig} ${str}  ${' Start: ' + m.overallStart ?: '  '} ${' Stop: ' + m.overallStop ?: '  '}")
    e.updated = m.overallStart ?: PointInTime.today()
    e.published = m.overallStart ?: PointInTime.today()

    e.link = new Link(rel: 'alternate', type: 'application/xml', href: linkService.getSelfHref(m))
    /*if (m.productFormName)e.addToCategories(term: m.productFormName)
    if (m.productForm)e.addToCategories(term: m.productForm)
    if (m.vaType) e.addToCategories(term: m.vaType)
    if (m.vaStatus)e.addToCategories(term: m.vaStatus)
    if (m.medStatus)e.addToCategories(term: m.medStatus.name)  */
      
    return e
  }
}
