package EXT.DOMAIN.cpe.vpr.ws.json

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller

import EXT.DOMAIN.cpe.jsonc.JsonCResponse


class JsonCResponseMarshaller implements ObjectMarshaller<JSON> {

    boolean supports(Object object) {
        if (object == null) return false;
        return JsonCResponse.isAssignableFrom(object.getClass());
    }

    void marshalObject(Object object, JSON json) {
        JsonCResponse response = object as JsonCResponse

        json.writer.object()

        json.property("apiVersion", response.apiVersion)
        if (response.id != null) json.property("id", response.id)
        if (response.context != null) json.property("context", response.context)
        if (response.method != null) json.property("method", response.method)
        if (response.params != null) json.property("params", response.params)
        if (response.error) {
            json.writer.key("error")
            json.writer.object()
            json.property("code", response.error.code)
            json.property("message", response.error.message)

            if (response.error.errors)
                json.property("errors", response.error.errors)

            json.writer.endObject();
        } else {
            json.property("data", response.data)
        }
        json.property("success", response.success)

        json.writer.endObject()
    }

}

