package EXT.DOMAIN.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Person {
    String name
    String uri
    String email

    static constraints = {
        name(nullable: false, blank: false)
        uri(url: true, nullable: true, blank: false)
        email(email: true, nullable: true, blank: false)
    }
}
