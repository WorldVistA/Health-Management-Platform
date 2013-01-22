package EXT.DOMAIN.cpe.vpr.ws.xml

import grails.converters.XML

class MapMarshaller extends org.codehaus.groovy.grails.web.converters.marshaller.xml.MapMarshaller {
    @Override
    void marshalObject(Object o, XML xml) {
        Map<Object, Object> map = (Map<Object, Object>) o;
        for (Map.Entry<Object, Object> entry: map.entrySet()) {
            xml.startNode(entry.getKey().toString());
            xml.convertAnother(entry.getValue());
            xml.end();
        }
    }
}
