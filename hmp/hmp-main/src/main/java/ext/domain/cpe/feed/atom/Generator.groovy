package EXT.DOMAIN.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Generator {
    String uri
    String version
    String text

    static constraints = {
        text(nullable: false, blank: false)
    }
}
