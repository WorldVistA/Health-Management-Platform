package EXT.DOMAIN.cpe.vpr.ws.json

import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.PatientFacility
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import grails.converters.JSON
import java.beans.PropertyDescriptor
import org.codehaus.groovy.grails.support.proxy.ProxyHandler
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.cfg.NamingStrategy
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert
import org.springframework.beans.BeanUtils
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject

public class DomainClassMarshaller implements ObjectMarshaller<JSON>, InitializingBean {

    public static final List<String> DEFAULT_EXCLUDES = ['version'];

    private NamingStrategy namingStrategy = new ImprovedNamingStrategy();

    ILinkService linkService

    ProxyHandler proxyHandler

    List<String> excludes = []

    void afterPropertiesSet() {
        Assert.notNull(linkService, "linkService must not be null")
    }

    public boolean supports(Object object) {
        return (object instanceof Patient) || (object instanceof IPatientObject)
    }

    public void marshalObject(Object o, JSON json) throws ConverterException {
        marshalObject(o, json, DEFAULT_EXCLUDES);
    }

    public void marshalObject(o, JSON json, List<String> excludes) throws ConverterException {
        JSONWriter writer = json.getWriter();

        Class domainClass = o.class;

        writer.object();
//        writer.key("class").value(domainClass.getClazz().getName());
        writer.key("domain").value(namingStrategy.tableName(domainClass.getName()));

        List<Link> links = linkService.getLinks(o)
        for (Link link: links) {
            json.property("link", link)
        }

        PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(domainClass);
        for (PropertyDescriptor property: properties) {
            if (property.propertyType == Patient.class) continue;
            if (property.name == "id") continue;
            if (property.name.endsWith("Service'")) continue;
            if (excludes?.contains(property.name)) continue;

            marshalProperty(o, property, json);
        }

        writer.endObject()
    }

    protected void marshalProperty(o, PropertyDescriptor property, JSON json) {
//        if (property.isAssociation() && property.isBidirectional() && json.depth >= 4) return

        def val = o[property.name]
        if (val) {
            val = proxyHandler.unwrapIfProxy(val)

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

            json.property(property.name, val)
        }
    }

    protected void asAtomLink(JSON json, String rel, String href) {
        if (href == null) return

        Link link = new Link(rel: rel, href: href)
        json.property("link", link)
    }
}


