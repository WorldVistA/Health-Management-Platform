package EXT.DOMAIN.cpe.vpr.ws.xml

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.PatientFacility
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import grails.converters.XML
import java.beans.PropertyDescriptor
import org.codehaus.groovy.grails.support.proxy.ProxyHandler
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.cfg.NamingStrategy
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject

/**
 * Responsible for marshalling Grails 'Domain Classes' to XML.  The primary
 * difference between this implementation and the Grails default DomainClassMarshaller
 * is that this inserts atom 'self' links instead of writing out 'id' attributes, and it
 * also leaves out properties with null values.
 */
class DomainClassMarshaller implements ObjectMarshaller<XML>, InitializingBean {

    private NamingStrategy namingStrategy = new ImprovedNamingStrategy();

    ILinkService linkService

    ProxyHandler proxyHandler

    void afterPropertiesSet() {
        Assert.notNull(linkService, "linkService must not be null")
    }

    boolean supports(Object object) {
        return (object instanceof IPatientObject)
    }

    void marshalObject(Object o, XML xml) {
        Class domainClass = o.class;

        xml.startNode("domain")
        xml.convertAnother(namingStrategy.tableName(domainClass.getName()))
        xml.end()

        List<Link> links = linkService.getLinks(o)
        for (Link link : links) {
            xml.startNode(xml.getElementName(link))
            xml.convertAnother(link)
            xml.end()
        }

        PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(domainClass);
        for (PropertyDescriptor property : properties) {
            if (property.propertyType == Patient.class) continue;
            if (property.name == "id") continue;
            if (property.name.endsWith("Service'")) continue;
//            if (excludes?.contains(property.name)) continue;

            marshalProperty(o, property, xml);
        }
    }

    protected void marshalProperty(o, PropertyDescriptor property, XML xml) {
//        if (property.isAssociation() && property.isBidirectional() && json.depth >= 4) return

        def val = o[property.name]
        if (val) {
            val = proxyHandler.unwrapIfProxy(val)

            // this compensates for hibernate proxy collections
            if (val instanceof SortedMap) {
                val = new TreeMap((SortedMap) val);
            } else if (val instanceof SortedSet) {
                val = new TreeSet((SortedSet) val);
            } else if (val instanceof Set) {
                val = new HashSet((Set) val);
            } else if (val instanceof Map) {
                val = new HashMap((Map) val);
            } else if (val instanceof Collection) {
                val = new ArrayList((Collection) val);
            }

            xml.startNode(property.name)
            xml.convertAnother(val)
            xml.end()
        }
    }
}
