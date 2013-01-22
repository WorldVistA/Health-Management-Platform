package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.Formatter;

public class DayOfWeekAndTimeFormatter extends Formatter implements IConfigurableTimeFormatter {

    private var _dayOfWeekFormatter:DayOfWeekFormatter = new DayOfWeekFormatter();
    private var _timeFormatter:DefaultTimeFormatter = new DefaultTimeFormatter();

    [Bindable]
    [Inspectable]
    public function get long():Boolean {
        return _dayOfWeekFormatter.long;
    }

    public function set long(value:Boolean):void {
        _dayOfWeekFormatter.long = value;
    }

    [Bindable]
    [Inspectable]
    public function get showSeconds():Boolean {
        return _timeFormatter.showSeconds;
    }

    public function set showSeconds(value:Boolean):void {
        _timeFormatter.showSeconds = value;
    }

    override public function format(o:Object):String {
        if (o == null) return null;
        if (o is Date) {
            return _dayOfWeekFormatter.format(o) + " " + _timeFormatter.format(o);
        } else if (o is PointInTime) {
            var p:PointInTime = o as PointInTime;
            if (p.precision.greaterThanOrEqual(Precision.MINUTE)) {
                return _dayOfWeekFormatter.format(o) + " " + _timeFormatter.format(o);
            } else if (p.precision.equals(Precision.DATE)) {
                return _dayOfWeekFormatter.format(o);
            } else {
                throw new ImprecisePointInTimeError(p);
            }
        } else {
            throw new ArgumentError("unable to format object");
        }
    }
}
}
