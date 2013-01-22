package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.DateFormatter;
import mx.formatters.Formatter;

public class DefaultDateTimeFormatter extends Formatter implements IConfigurableTimeFormatter {
    
    public static const FORMAT:String = DefaultDateFormatter.FORMAT + " JJ:NN";
    public static const FORMAT_SHOWING_SECONDS:String = FORMAT + ":SS";

    private var f:DateFormatter;
    private var df:DefaultDateFormatter = new DefaultDateFormatter();

	private var _showSeconds:Boolean = false;
	
	/**
	 * 
	 * 
	 * @default false 
	 */
	public function get showSeconds():Boolean {
		return _showSeconds;
	}
	
	public function set showSeconds(show:Boolean):void {
		_showSeconds = show;
		f = null;
	}

    private function getDateFormatter():DateFormatter {
        if (f == null) {
            f = new DateFormatter();
            f.formatString = showSeconds ? FORMAT_SHOWING_SECONDS : FORMAT;
        }
        return f;
    }

    public override function format(o:Object):String {
    	if (o == null) return null;
        if (o is Date) {
            return getDateFormatter().format(o);
        } else if (o is PointInTime) {
            var p:PointInTime = o as PointInTime;
            if (p.precision.lessThanOrEqual(Precision.DATE)) {
                return df.format(p);
            } else if (p.precision == Precision.HOUR) {
                return df.format(p) + " " + DateFormatUtils.formatTwoDigits(p.hour) + "-" + DateFormatUtils.formatTwoDigits(p.hour + 1);
            } else {
                var s:String = df.format(p) + " " + DateFormatUtils.formatTwoDigits(p.hour) + ":" + DateFormatUtils.formatTwoDigits(p.minute);
                if (showSeconds && p.precision.greaterThanOrEqual(Precision.SECOND)) {
                	s = s + ":" + DateFormatUtils.formatTwoDigits(p.second);
                }
                return s;
            }
        } else {
            throw new ArgumentError("unable to format object");
        }
    }
}
}
