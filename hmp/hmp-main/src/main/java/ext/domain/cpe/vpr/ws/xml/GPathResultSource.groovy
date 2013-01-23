package org.osehra.cpe.vpr.ws.xml

import groovy.util.slurpersupport.GPathResult
import javax.xml.transform.Source

/**
 * TODOC: Provide summary documentation of class GPathResultSource
 */
class GPathResultSource implements Source {
    String systemId
    GPathResult xml
}
