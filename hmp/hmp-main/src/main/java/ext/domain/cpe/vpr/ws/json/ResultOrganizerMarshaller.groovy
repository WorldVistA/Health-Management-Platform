package org.osehra.cpe.vpr.ws.json

import java.beans.PropertyDescriptor;

import grails.converters.JSON
import org.osehra.cpe.vpr.ResultOrganizer
import org.osehra.cpe.vpr.Result


class ResultOrganizerMarshaller extends DomainClassMarshaller {

    private static final List<String> RESULT_EXCLUDES = ['organizers', 'accession', 'facility', 'category', 'kind', 'observed', 'resulted', 'resultStatus', 'specimen'] + DEFAULT_EXCLUDES

    @Override
    boolean supports(Object object) {
        return object instanceof ResultOrganizer;
    }

    @Override
    protected void marshalProperty(Object o, PropertyDescriptor property, JSON json) {
        if (property.name == "results") {
            ResultOrganizer organizer = o as ResultOrganizer;
            json.property("totalResults", organizer.results.size())
            json.writer.key("results")
            json.writer.array();
            organizer.results.each { Result result ->
                marshalResult(result, json)
            }
            json.writer.endArray();
        } else {
            super.marshalProperty(o, property, json)
        }
    }

    // an abbreviated version of a result without the organizer properties
    private void marshalResult(Result result, JSON json) {
        if (result == null) return;
        marshalObject(result, json, RESULT_EXCLUDES)
    }
}
