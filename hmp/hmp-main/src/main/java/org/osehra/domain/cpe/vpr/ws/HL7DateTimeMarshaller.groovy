package org.osehra.cpe.vpr.ws

import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.converters.Converter
import org.osehra.cpe.datetime.PointInTime
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.osehra.cpe.datetime.format.HL7DateTimeFormat


class HL7DateTimeMarshaller implements ObjectMarshaller {

    @Override
    boolean supports(Object object) {
        return (object instanceof PointInTime) || (object instanceof LocalDateTime) || (object instanceof LocalDate) || (object instanceof DateTime)
    }

    @Override
    void marshalObject(Object object, Converter converter) {
        if (object == null) return

        String result

        if (object instanceof PointInTime)
            result = (object as PointInTime).toString()
        else if (object instanceof LocalDateTime)
            result = HL7DateTimeFormat.dateTime().print(object as LocalDateTime)
         else if (object instanceof LocalDate)
            result = HL7DateTimeFormat.dateTime().print(object as LocalDate)
         else if (object instanceof DateTime)
            result = HL7DateTimeFormat.dateTime().print(object as DateTime)

        converter.convertAnother(result)
    }

}
