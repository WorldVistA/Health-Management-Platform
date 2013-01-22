package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.DateBase;
import mx.formatters.DateFormatter;
import mx.formatters.Formatter;

public class DefaultAccessibleDateFormatter extends Formatter {

    public static const FORMAT:String = "MMMM DD,YYYY";

    private static var f:DateFormatter;

    private function getDateFormatter():DateFormatter {
        if (f == null) {
            f = new DateFormatter();
            f.formatString = FORMAT;
        }
        return f;
    }

    public override function format(o:Object):String {
        if (o == null) return null;
        if (o is Date) {                                                               
            return getDateFormatter().format(o);
        } else if (o is PointInTime) {
            var p:PointInTime = o as PointInTime;
            if (p.precision == Precision.YEAR) {
                return String(p.year);
            } else if (p.precision == Precision.MONTH) {
                return formatMonth(p.month) + " " + p.year;
            } else {
                return formatMonth(p.month) + " " + formatDate(p.date) + "," + formatYear(p.year);
            }
        } else {
            throw new ArgumentError("unable to format object");
        }
    }

    private function formatDate(date:int):String {
        return DateFormatUtils.formatTwoDigits(date);
    }

    private function formatMonth(month:int):String {
        return DateBase.monthNamesLong[month - 1];
    }

    private function formatYear(year:int):String {
        return String(year);
    }
}
}
