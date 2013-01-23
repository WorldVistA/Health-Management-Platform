package org.osehra.cpe.vpr.web.converter.dateTime;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.format.HL7DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

public class StringToPointInTimeConverter implements Converter<String, PointInTime> {
    @Override
    public PointInTime convert(String s) {
        return HL7DateTimeFormat.parse(s);
    }
}
