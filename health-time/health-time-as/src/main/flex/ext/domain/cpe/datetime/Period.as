package EXT.DOMAIN.cpe.datetime {


/**
 * An immutable time period specifying a set of duration field values.
 *
 * A time period is divided into a number of fields, such as hours and seconds.
 */
public class Period {

    public static const ZERO:Period = new Period(0);

    public static const DAYS_PER_WEEK:int = 7;
    public static const MILLIS_PER_SECOND:Number = 1000;
    public static const SECONDS_PER_MINUTE:Number = 60;
    public static const MINUTES_PER_HOUR:Number = 60;

    private var fields:Object = new Object();

    public function Period(years:int, months:int = 0, weeks:int = 0, days:int = 0, hours:int = 0, minutes:int = 0, seconds:int = 0, milliseconds:int = 0) {
        if (years > 0) fields.years = years;
        if (months > 0) fields.months = months;
        if (weeks > 0) fields.weeks = weeks;
        if (days > 0) fields.days = days;
        if (hours > 0) fields.hours = hours;
        if (minutes > 0) fields.minutes = minutes;
        if (seconds > 0) fields.seconds = seconds;
        if (milliseconds > 0) fields.milliseconds = milliseconds;
    }

    public function get years():int {
        return fields.hasOwnProperty("years") ? fields.years : 0;
    }

    public function get months():int {
        return fields.hasOwnProperty("months") ? fields.months : 0;
    }

    public function get weeks():int {
        return fields.hasOwnProperty("weeks") ? fields.weeks : 0;
    }

    public function get days():int {
        return fields.hasOwnProperty("days") ? fields.days : 0;
    }

    public function get hours():int {
        return fields.hasOwnProperty("hours") ? fields.hours : 0;
    }

    public function get minutes():int {
        return fields.hasOwnProperty("minutes") ? fields.minutes : 0;
    }

    public function get seconds():int {
        return fields.hasOwnProperty("seconds") ? fields.seconds : 0;
    }

    public function get milliseconds():int {
        return fields.hasOwnProperty("milliseconds") ? fields.milliseconds : 0;
    }

    public function plus(p:Period):Period {
        return null;
    }

    public function minus(p:Period):Period {
        return null;
    }

    public static function years(years:int):Period {
        return new Period(years);
    }

    public static function months(months:int):Period {
        return new Period(0, months);
    }

    public static function weeks(weeks:int):Period {
        return new Period(0, 0, weeks);
    }

    public static function days(days:int):Period {
        return new Period(0, 0, 0, days);
    }

    public static function hours(hours:int):Period {
        return new Period(0, 0, 0, 0, hours);
    }

    public static function minutes(minutes:int):Period {
        return new Period(0, 0, 0, 0, 0, minutes);
    }

    public static function seconds(seconds:int):Period {
        return new Period(0, 0, 0, 0, 0, 0, seconds);
    }

    public static function milliseconds(milliseconds:int):Period {
        return new Period(0, 0, 0, 0, 0, 0, 0, milliseconds);
    }

    public static function fortnight():Period {
        return Period.weeks(2);
    }
}
}
