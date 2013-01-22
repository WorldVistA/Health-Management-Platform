package EXT.DOMAIN.cpe.vpr.web.converter.dateTime;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.format.HL7DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

public class StringToPointInTimeConverter implements Converter<String, PointInTime> {
    @Override
    public PointInTime convert(String s) {
        return HL7DateTimeFormat.parse(s);
    }
}
