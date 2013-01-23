package org.osehra.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Link {
    /**
     * The URI of the referenced resource.
     */
    String href

    /**
     * Contains a single link relationship type. It can be a full URI (see extensibility), or one of the following predefined values:
     * <ul>
     * <li><code>alternate</code>: an alternate representation of the entry or feed, for example a permalink to the html version of the entry, or the front page of the weblog.</li>
     * <li><code>enclosure</code>: a related resource which is potentially large in size and might require special handling, for example an audio or video recording.</li>
     * <li><code>related</code>: an document related to the entry or feed.</li>
     * <li><code>self</code>: the feed itself.</li>
     * <li><code>via</code>: the source of the information provided in the entry.</li>
     * </ul>
     */
    String rel

    /**
     * indicates the media type of the resource.
     */
    String type

    /**
     * Human readable information about the link, typically for display purposes.
     */
    String title

    /**
     * Indicates the language of the referenced resource.
     */
    String hreflang

    /**
     * The length of the resource, in bytes.
     */
    Long length

    static constraints = {
        href(url:true, nullable:false)
        rel(nullable:true, blank:false)
        type(nullable:true, blank:false)
        title(nullable:true, blank:false)
        hreflang(nullable:true, blank:false)
        length(nullable:true, blank:false)
    }
}
