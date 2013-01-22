package EXT.DOMAIN.cpe.vpr.sync.vista

import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.ws.xml.AbstractXmlSlurperUnmarshaller
import groovy.util.slurpersupport.GPathResult

class FooUnmarshaller extends AbstractXmlSlurperUnmarshaller {

    Patient patient

    boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Foo);
    }

    protected Object unmarshalGPathResult(GPathResult xml) {
        return new Foo(bar:xml.@bar.toString(),baz:xml.baz.@value.toBoolean());
    }

}
