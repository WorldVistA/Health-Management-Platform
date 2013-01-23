package org.osehra.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable


@Validateable
class Category {
    String term
    String scheme
    String label

    static constraints = {
        term(nullable:false, blank:false)
        scheme(url:true, nullable:true, blank:false)
        label(nullable:true, blank:false)
    }
}
