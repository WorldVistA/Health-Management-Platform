package EXT.DOMAIN.cpe.datetime {
public class TimeZoneOffset {

    private static const MINUTES_PER_HOUR:int = 60;
    private static const MILLISECONDS_PER_MINUTE:Number = 60 * 1000;

    private var _offsetMinutes:int;

    public function TimeZoneOffset(offsetMinutes:int):void {
        _offsetMinutes = offsetMinutes;
    }

    public function get milliseconds():Number {
        return _offsetMinutes * MILLISECONDS_PER_MINUTE;
    }

    public function get minutes():Number {
        return _offsetMinutes;
    }

    public function get hours():Number {
        return _offsetMinutes / MINUTES_PER_HOUR;
    }

    public function equals(offset:TimeZoneOffset):Boolean {
        return this.minutes == offset.minutes;
    }

    public static function forOffsetHoursMinutes(hours:int, minutes:int):TimeZoneOffset {
        return new TimeZoneOffset((hours * MINUTES_PER_HOUR) + Math.abs(minutes));
    }

    public static function parse(text:String):TimeZoneOffset {
        if (text.length != 5) throw new ArgumentError("invalid time zone offset string");
        var sign:String = text.charAt(0);
        if (sign != "+" && sign != "-") throw new ArgumentError("invalid time zone offset sign");
        var hours:int = int(text.substring(1, 3));
        var minutes:int = int(text.substring(3, 5));
        if (isNaN(hours) || isNaN(minutes)) throw new ArgumentError("invalid time zone offset string");
        return forOffsetHoursMinutes(sign == "-" ? -hours : hours, minutes);
    }
}
}
