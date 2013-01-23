
package org.osehra.cpe.vpr.web.converter.dateTime;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.datetime.Precision;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;

public class PointInTimeToStringConverter implements Converter<PointInTime, String>
{

    private static final String NA_VALUE = "";

    @Override
    public String convert( PointInTime source )
    {
        if(source==null){return NA_VALUE;}
        DateTimeFormatter fmt = null;
        Precision p = source.getPrecision();
        switch ( p )
        {
        case MILLISECOND:
        case SECOND:
        case MINUTE:
            fmt = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm" );
            break;
        case HOUR:
        case DATE:
            fmt = DateTimeFormat.forPattern( "yyyy-MM-dd" );
            break;
        case MONTH:
            fmt = DateTimeFormat.forPattern( "yyyy-MM" );
            break;
        default:
            fmt = DateTimeFormat.forPattern( "yyyy" );
        }

        DateTime dt = source.toDateTime( null );
        return fmt.print( dt );
    }
}
