package EXT.DOMAIN.cpe.vpr.web.converter.dateTime

import EXT.DOMAIN.cpe.datetime.format.PointInTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.core.convert.ConversionService;

class PointInTimeTagLib
{
    static namespace = 'hmp'

    def formatDate = { attrs, body ->
        DateTimeFormatter formatter = attrs.format ? PointInTimeFormat.forPattern(attrs.format) : PointInTimeFormat.dateTime()
        out << formatter.print(attrs.date)
    }
}
