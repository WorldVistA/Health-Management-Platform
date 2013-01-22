package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.DateBase;
import mx.formatters.DateFormatter;
import mx.formatters.Formatter;

public class DefaultDateFormatter extends Formatter {

    public static const FORMAT:String = "MMM DD,YY";    
	public static const LONG_FORMAT:String = "MMMM DD, YYYY";

    private var f:DateFormatter;
	private var _long:Boolean;

	[Bindable]
	[Inspectable]
	public function get long():Boolean {
		return _long;
	}

	public function set long(value:Boolean):void {
		_long = value;
		f = null;
	}

    private function getDateFormatter():DateFormatter {
        if (f == null) {
            f = new DateFormatter();
            f.formatString = long ? LONG_FORMAT : FORMAT;
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
                return formatMonth(p.month) + " " + formatDate(p.date) + (long ? ", " : ",") + formatYear(p.year);
            }
        } else {
            throw new ArgumentError("unable to format object");
        }
    }

    private function formatDate(date:int):String {
        return DateFormatUtils.formatTwoDigits(date);
    }

    private function formatMonth(month:int):String {
    	if (long)
    		return DateBase.monthNamesLong[month - 1];
    	else
        	return DateBase.monthNamesShort[month - 1];
    }

    private function formatYear(year:int):String {
    	if (long)
    		return year.toString();
    	else
        	return DateFormatUtils.formatTwoDigits(year);
    }
}
}
