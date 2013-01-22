package EXT.DOMAIN.cpe.datetime {


/**
 * Defines an exact duration of time in milliseconds.  Duration instances are immutable.
 *
 * A duration is defined by a fixed number of milliseconds.  There is no concept of fields such as days or months, as these fields can vary in length.
 */
public class Duration {
    public static const ZERO:Duration = new Duration(0);

    public static const MILLIS_PER_SECOND:Number = 1000;
    public static const SECONDS_PER_MINUTE:Number = 60;
    public static const MINUTES_PER_HOUR:Number = 60;

    private var _millis:Number;

    public function Duration(duration:Number) {
        _millis = duration;
    }

    public function get millis():Number {
        return _millis;
    }

    public function getMillis():Number {
        return millis;
    }

    public function get seconds():Number {
        return millis / MILLIS_PER_SECOND;
    }

    public function getSeconds():Number {
        return seconds;
    }

    public function get minutes():Number {
        return seconds / SECONDS_PER_MINUTE;
    }

    public function getMinutes():Number {
        return minutes;
    }

    public function get hours():Number {
        return minutes / MINUTES_PER_HOUR;
    }

    public function getHours():Number {
        return hours;
    }

    public function toPeriod():Period {
        return Period.milliseconds(this.millis);
    }

    public static function hours(hours:int):Duration {
        return new Duration(hours * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
    }

    public static function minutes(minutes:int):Duration {
        return new Duration(minutes * SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
    }

    public static function seconds(seconds:int):Duration {
        return new Duration(seconds * MILLIS_PER_SECOND);
    }

    public static function milliseconds(millis:int):Duration {
        return new Duration(millis);
    }

    public static function fromFields(h:int, m:int, s:int, mill:int):Duration {
        return new Duration(hours(h).millis + minutes(m).millis + seconds(s).millis + mill);
    }
}
}
