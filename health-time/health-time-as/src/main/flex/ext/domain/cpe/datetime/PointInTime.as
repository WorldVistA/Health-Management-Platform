package EXT.DOMAIN.cpe.datetime {
import mx.utils.ObjectUtil;

/**
 * Class supporting the HL7 time stamp (TS) data type.  PointInTime instances are immutable.
 * <p/>
 * Format: YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ]
 * <p/>
 * Contains the exact time of an event, including the date and time. The date portion of a time stamp follows the rules
 * of a date field and the time portion follows the rules of a time field. The specific data representations used in the
 * HL7 encoding rules are compatible with ISO 8824-1987(E).
 * <p/>
 * In prior versions of HL7, an optional second component indicates the degree of precision of the time stamp (Y = year,
 * L = month, D = day, H = hour, M = minute, S = second). This optional second component is retained only for purposes
 * of backward compatibility.
 * <p/>
 * By site-specific agreement, YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ] may be used where
 * backward compatibility must be maintained.
 * <p/>
 * In the current and future versions of HL7, the precision is indicated by limiting the number of digits used, unless
 * the optional second component is present. Thus, YYYY is used to specify a precision of "year," YYYYMM specifies a
 * precision of "month," YYYYMMDD specifies a precision of "day," YYYYMMDDHH is used to specify a precision of "hour,"
 * YYYYMMDDHHMM is used to specify a precision of "minute," YYYYMMDDHHMMSS is used to specify a precision of seconds,
 * and YYYYMMDDHHMMSS.SSSS is used to specify a precision of ten thousandths of a second. In each of these cases, the
 * time zone is an optional component. Maximum length of the time stamp is 26. Examples:
 * <p/>
 * <samp> |19760704010159-0600| 1:01:59 on July 4, 1976 in the Eastern Standard Time zone.
 * <p/>
 * |19760704010159-0500| 1:01:59 on July 4, 1976 in the Eastern Daylight Saving Time zone.
 * <p/>
 * |198807050000|   Midnight of the night extending from July 4 to July 5, 1988 in the local time zone of the sender.
 * <p/>
 * |19880705|    Same as prior example, but precision extends only to the day.  Could be used for a birthdate, if the
 * time of birth is unknown. </samp>
 * <p/>
 * The HL7 Standard strongly recommends that all systems routinely send the time zone offset but does not require it.
 * All HL7 systems are required to accept the time zone offset, but its implementation is application specific. For many
 * applications the time of interest is the local time of the sender. For example, an application in the Eastern
 * Standard Time zone receiving notification of an admission that takes place at 11:00 PM in San Francisco on December
 * 11 would prefer to treat the admission as having occurred on December 11 rather than advancing the date to December
 * 12.
 * <p/>
 * One exception to this rule would be a clinical system that processed patient data collected in a clinic and a nearby
 * hospital that happens to be in a different time zone. Such applications may choose to convert the data to a common
 * representation. Similar concerns apply to the transitions to and from daylight saving time. HL7 supports such
 * requirements by requiring that the time zone information be present when the information is sent. It does not,
 * however, specify which of the treatments discussed here will be applied by the receiving system.
 */
public class PointInTime {

    private static var currentTimeStrategy:ICurrentTimeStrategy = new DefaultCurrentTimeStrategy();

    private var _precision:Precision;
    private var _d:Date;
    private var _timezoneOffset:TimeZoneOffset;

    public function PointInTime(year:int, month:int = -1, date:int = -1, hour:int = -1, minute:int = -1, second:int = -1, millisecond:int = -1, offset:TimeZoneOffset = null) {
        if (month != -1 && month < 1 && month > 12) throw new ArgumentError("PointInTime month must be between 1 and 12");
        if (date != -1 && date < 1 && date > 31) throw new ArgumentError("PointInTime date must be between 1 and 31");
        if (hour != -1 && hour < 0 && hour > 23) throw new ArgumentError("PointInTime hour must be between 0 and 23");
        if (minute != -1 && minute < 0 && minute > 59) throw new ArgumentError("PointInTime minute must be between 0 and 59");
        if (second != -1 && second < 0 && second > 59) throw new ArgumentError("PointInTime second must be between 0 and 59");
        if (millisecond != -1 && millisecond < 0 && millisecond > 999) throw new ArgumentError("PointInTime millisecond must be between 0 and 999");
        if (millisecond != -1) {
            _precision = Precision.MILLISECOND;
            _d = new Date(year, month - 1, date, hour, minute, second, millisecond);
        } else if (second != -1) {
            _precision = Precision.SECOND;
            _d = new Date(year, month - 1, date, hour, minute, second);
        } else if (minute != -1) {
            _precision = Precision.MINUTE;
            _d = new Date(year, month - 1, date, hour, minute);
        } else if (hour != -1) {
            _precision = Precision.HOUR;
            _d = new Date(year, month - 1, date, hour);
        } else if (date != -1) {
            _precision = Precision.DATE;
            _d = new Date(year, month - 1, date);
        } else if (month != -1) {
            _precision = Precision.MONTH;
            _d = new Date(year, month - 1, 1);
        } else {
            _precision = Precision.YEAR;
            _d = new Date(year, 0, 1);
        }
        _timezoneOffset = offset;
    }

    public function get year():int {
        return int(_d.fullYear);
    }

    public function get month():int {
        if (!isMonthSet()) throw new ImprecisePointInTimeError(this);
        return _d.month + 1;
    }

    public function get date():int {
        if (!isDateSet()) throw new ImprecisePointInTimeError(this);
        return _d.date;
    }

    public function get hour():int {
        if (!isHourSet()) throw new ImprecisePointInTimeError(this);
        return _d.hours;
    }

    public function get minute():int {
        if (!isMinuteSet()) throw new ImprecisePointInTimeError(this);
        return _d.minutes;
    }

    public function get second():int {
        if (!isSecondSet()) throw new ImprecisePointInTimeError(this);
        return _d.seconds;
    }

    public function get millisecond():int {
        if (!isMillisecondSet()) throw new ImprecisePointInTimeError(this);
        return _d.milliseconds;
    }

    public function getYear():int {
        return year;
    }

    public function getMonth():int {
        return month;
    }

    public function getDate():int {
        return date;
    }

    public function getHour():int {
        return hour;
    }

    public function getMinute():int {
        return minute;
    }

    public function getSecond():int {
        return second;
    }

    public function getMillisecond():int {
        return millisecond;
    }

    public function get precision():Precision {
        return _precision;
    }

    public function getPrecision():Precision {
        return precision;
    }

    public function get timezoneOffset():TimeZoneOffset {
        return _timezoneOffset;
    }

    public function isMonthSet():Boolean {
        return precision.greaterThanOrEqual(Precision.MONTH);
    }

    public function isDateSet():Boolean {
        return precision.greaterThanOrEqual(Precision.DATE);
    }

    public function isHourSet():Boolean {
        return precision.greaterThanOrEqual(Precision.HOUR);
    }

    public function isMinuteSet():Boolean {
        return precision.greaterThanOrEqual(Precision.MINUTE);
    }

    public function isSecondSet():Boolean {
        return precision.greaterThanOrEqual(Precision.SECOND);
    }

    public function isMillisecondSet():Boolean {
        return precision.greaterThanOrEqual(Precision.MILLISECOND);
    }

    /**
     * A PointInTime is "local" if there is no time zone specified.
     * @return <code>false</code> if there is a time zone set on this instance, <code>true</code> otherwise
     */
    public function isLocal():Boolean {
        return _timezoneOffset == null;
    }

    /**
     * A PointInTime can be promoted to an IntervalOfTIme whereby the boundaries and width is inferred from the precision of the PointInTime and the duration of the least significant calendar period specified.
     * The low and high boundaries are guaranteed to have millisecond precision.  The high boundary is open.
     * <p>
     * For example, the PointInTime literal "200009" is converted to an IntervalOfTime with low boundary "20000901000000.000" and high boundary "2000100100000.000"
     * </p>
     * @return an IntervalOfTime
     */
    public function promote():IntervalOfTime {
        var low:PointInTime = createLow();
        var high:PointInTime = createHigh();
        return new IntervalOfTime(low, high);
    }

    public function compareTo(t:PointInTime):int {
        if (t == null)
            return 1;
        if (this === t) return 0;
        if (precision.equals(t.precision)) {
            if (year > t.year) return 1;
            if (year < t.year) return -1;
            if (precision == Precision.YEAR) return 0;

            if (month > t.month) return 1;
            if (month < t.month) return -1;
            if (precision == Precision.MONTH) return 0;

            if (date > t.date) return 1;
            if (date < t.date) return -1;
            if (precision == Precision.DATE) return 0;

            if (hour > t.hour) return 1;
            if (hour < t.hour) return -1;
            if (precision == Precision.HOUR) return 0;

            if (minute > t.minute) return 1;
            if (minute < t.minute) return -1;
            if (precision == Precision.MINUTE) return 0;

            if (second > t.second) return 1;
            if (second < t.second) return -1;
            if (precision == Precision.SECOND) return 0;

            if (millisecond > t.millisecond) return 1;
            if (millisecond < t.millisecond) return -1;

            return 0;
        }
        var leastPrecise:PointInTime = lessPrecise(this, t);
        var morePrecise:PointInTime = mostPrecise(this, t);
        var i:IntervalOfTime = leastPrecise.promote();
        if (i.contains(morePrecise)) {
            return getPrecision().compareTo(t.getPrecision());
        }
        var lcd:PointInTime = morePrecise.toPrecision(leastPrecise.getPrecision());
        if (this == leastPrecise)
            return compareTo(lcd);
        else
            return lcd.compareTo(leastPrecise);
    }

    public function compareToDate(t:Date):int {
        return compareTo(fromDate(t));
    }

    public function after(t:PointInTime):Boolean {
        if (getPrecision().lessThan(t.getPrecision())) {
            var interval:IntervalOfTime = promote();
            if (interval.contains(t))
                return false;
            return interval.getHigh().compareTo(t) > 0;
        } else if (getPrecision().greaterThan(t.getPrecision())) {
            var interval2:IntervalOfTime = t.promote();
            if (interval2.contains(this))
                return false;
            return compareTo(interval2.getLow()) > 0;
        } else {
            return compareTo(t) > 0;
        }
    }

    public function before(t:PointInTime):Boolean {
        if (getPrecision().lessThan(t.getPrecision())) {
            var interval:IntervalOfTime = promote();
            if (interval.contains(t))
                return false;
            return interval.getLow().compareTo(t) < 0;
        } else if (getPrecision().greaterThan(t.getPrecision())) {
            var interval2:IntervalOfTime = t.promote();
            if (interval2.contains(this))
                return false;
            return compareTo(interval2.getHigh()) < 0;
        } else {
            return compareTo(t) < 0;
        }
    }

    public function equals(t:PointInTime):Boolean {
        if (t == null) return false;
        if (this.isLocal() != t.isLocal()) return false;
        if (this.precision != t.precision) return false;
        if (this.year != t.year) return false;
        if (this.isMonthSet() && this.month != t.month) return false;
        if (this.isDateSet() && this.date != t.date) return false;
        if (this.isHourSet() && this.hour != t.hour) return false;
        if (this.isMinuteSet() && this.minute != t.minute) return false;
        if (this.isSecondSet() && this.second != t.second) return false;
        if (this.isMillisecondSet() && this.millisecond != t.millisecond) return false;
        if (!this.isLocal() && !this.timezoneOffset.equals(t.timezoneOffset)) return false;
        return true;
    }

    public function clone():PointInTime {
        var p:PointInTime = new PointInTime(year, isMonthSet() ? month : -1, isDateSet() ? date : -1, isHourSet() ? hour : -1, isMinuteSet() ? minute : -1, isSecondSet() ? second : -1, isMillisecondSet() ? millisecond : -1);
        return p;
    }

    public function addYears(years:int):PointInTime {
        return addPeriod(Period.years(years));
    }

    public function addMonths(months:int):PointInTime {
        return addPeriod(Period.months(months));
    }

    public function addDays(days:int):PointInTime {
        return addPeriod(Period.days(days));
    }

    public function addMinutes(minutes:int):PointInTime {
        return addPeriod(Period.minutes(minutes));
    }

    public function addSeconds(seconds:int):PointInTime {
        return addPeriod(Period.seconds(seconds));
    }

    public function addHours(hours:int):PointInTime {
        return addPeriod(Period.hours(hours));
    }

    public function addMilliseconds(millis:Number):PointInTime {
        return addDuration(new Duration(millis));
    }

    public function addDuration(duration:Duration):PointInTime {
        return fromDateWithPrecision(new Date(_d.getTime() + duration.getMillis()), this.precision);
    }

    public function addPeriod(period:Period):PointInTime {
        var d:Date = new Date(_d.getTime());
        if (period.milliseconds > 0) d.milliseconds += period.milliseconds;
        if (period.seconds > 0) d.seconds += period.seconds;
        if (period.minutes > 0) d.minutes += period.minutes;
        if (period.hours > 0) d.hours += period.hours;
        if (period.days > 0) d.date += period.days;
        if (period.weeks > 0) d.date += period.weeks * Period.DAYS_PER_WEEK;
        if (period.months > 0) d.month += period.months;
        if (period.years > 0) d.fullYear += period.years;
        return fromDateWithPrecision(d, this.precision);
    }

    public function subtractYears(years:int):PointInTime {
        return subtractPeriod(Period.years(years));
    }

    public function subtractMonths(months:int):PointInTime {
        return subtractPeriod(Period.months(months));
    }

    public function subtractDays(days:int):PointInTime {
        return subtractPeriod(Period.days(days));
    }

    public function subtractMinutes(minutes:int):PointInTime {
        return subtractPeriod(Period.minutes(minutes));
    }

    public function subtractSeconds(seconds:int):PointInTime {
        return subtractPeriod(Period.seconds(seconds));
    }

    public function subtractHours(hours:int):PointInTime {
        return subtractPeriod(Period.hours(hours));
    }

    public function subtractMilliseconds(millis:Number):PointInTime {
        return subtractDuration(new Duration(millis));
    }

    public function subtractDuration(duration:Duration):PointInTime {
        return fromDateWithPrecision(new Date(_d.getTime() - duration.getMillis()), this.precision);
    }

    public function subtractPeriod(period:Period):PointInTime {
        var d:Date = new Date(_d.getTime());
        if (period.milliseconds > 0) d.milliseconds -= period.milliseconds;
        if (period.seconds > 0) d.seconds -= period.seconds;
        if (period.minutes > 0) d.minutes -= period.minutes;
        if (period.hours > 0) d.hours -= period.hours;
        if (period.days > 0) d.date -= period.days;
        if (period.weeks > 0) d.date -= period.weeks * Period.DAYS_PER_WEEK;
        if (period.months > 0) d.month -= period.months;
        if (period.years > 0) d.fullYear -= period.years;
        return fromDateWithPrecision(d, this.precision);
    }

    public function difference(t:PointInTime):Duration {
        if (!t.getPrecision().equals(getPrecision())) throw new ImprecisePointInTimeError(lessPrecise(this, t));
        var t1:Date = this.promote().getLow().toDate();
        var t2:Date = t.promote().getLow().toDate();
        return new Duration(t1.getTime() - t2.getTime());
    }

    /**
     * <p>Returns a string representation of this point in time.</p> <p>Default format is HL7 timestamp:
     * YYYY[MM[DD[HHMM[SS[.S[S[S[S]]]]]]]][+/-ZZZZ]
     * <p/>
     * <p>In the current and future versions of HL7, the precision is indicated by limiting the number of digits used.
     * Thus, YYYY is used to specify a precision of "year," YYYYMM specifies a precision of "month," YYYYMMDD specifies
     * a precision of "day," YYYYMMDDHH is used to specify a precision of "hour," YYYYMMDDHHMM is used to specify a
     * precision of "minute," YYYYMMDDHHMMSS is used to specify a precision of seconds, and YYYYMMDDHHMMSS.SSSS is used
     * to specify a precision of ten thousandths of a second. In each of these cases, the time zone is an optional
     * component. Maximum length of the time stamp is 26 characters. </p>
     * <p/>
     * <p>Examples: <samp> |19760704010159-0600| 1:01:59 on July 4, 1976 in the Eastern Standard Time zone.
     * |19760704010159-0500| 1:01:59 on July 4, 1976 in the Eastern Daylight Saving Time zone. |198807050000|   Midnight
     * of the night extending from July 4 to July 5, 1988 in the local time zone of the sender. |19880705|    Same as
     * prior example, but precision extends only to the day.  Could be used for a birthdate, if the time of birth is
     * unknown. </samp> </p>
     */
    public function toString():String {
        var s:String = String(year);
        if (precision === Precision.MILLISECOND) {
            s += twoDigitString(month) + twoDigitString(date) + twoDigitString(hour) + twoDigitString(minute) + twoDigitString(second) + "." + threeDigitString(millisecond);
        } else if (precision === Precision.SECOND) {
            s += twoDigitString(month) + twoDigitString(date) + twoDigitString(hour) + twoDigitString(minute) + twoDigitString(second);
        } else if (precision === Precision.MINUTE) {
            s += twoDigitString(month) + twoDigitString(date) + twoDigitString(hour) + twoDigitString(minute);
        } else if (precision === Precision.HOUR) {
            s += twoDigitString(month) + twoDigitString(date) + twoDigitString(hour);
        } else if (precision === Precision.DATE) {
            s += twoDigitString(month) + twoDigitString(date);
        } else if (precision === Precision.MONTH) {
            s += twoDigitString(month);
        }
        return s;
    }

    public function toDate():Date {
        if (Precision.MILLISECOND !== getPrecision()) throw new ImprecisePointInTimeError(this);
        return new Date(year, month - 1, date, hour, minute, second, millisecond);
    }

    public function toPointInTimeAtMidnight():PointInTime {
        if (getPrecision().lessThan(Precision.DATE)) throw new ImprecisePointInTimeError(this);
        var date:PointInTime = new PointInTime(getYear(), getMonth(), getDate());
        var day:IntervalOfTime = date.promote();
        var midnight:PointInTime = day.getHigh();
        return midnight.clone();
    }

    private function twoDigitString(n:int):String {
        if (n > 99) throw new RangeError();
        if (n == 0) return "00";
        else if (n < 10) return "0" + n;
        else return String(n);
    }

    private function threeDigitString(n:int):String {
        if (n > 999) throw new RangeError();
        if (n == 0) return "000";
        else if (n < 10) return "00" + n;
        else if (n >= 10 && n < 100) return "0" + n;
        else return String(n);
    }

    private function createHigh():PointInTime {
        var p:PointInTime = null;

        if (Precision.YEAR.equals(getPrecision())) {
            p = new PointInTime(this.year + 1);
        } else if (Precision.MONTH.equals(getPrecision())) {
            var m:int = this.month + 1;
            p = new PointInTime((m > 12 ? this.year + 1 : this.year), (m > 12 ? 1 : m));
        } else if (Precision.DATE.equals(getPrecision())) {
            p = this.addDays(1);
        } else if (Precision.HOUR.equals(getPrecision())) {
            p = this.addHours(1);
        } else if (Precision.MINUTE.equals(getPrecision())) {
            p = this.addMinutes(1);
        } else if (Precision.SECOND.equals(getPrecision())) {
            p = this.addSeconds(1);
        } else {
            p = this.addMilliseconds(1);
        }

        var year:int = p.year;
        var month:int = isMonthSet() ? p.month : 1;
        var date:int = isDateSet() ? p.date : 1;
        var hour:int = isHourSet() ? p.hour : 0;
        var minute:int = isMinuteSet() ? p.minute : 0;
        var second:int = isSecondSet() ? p.second : 0;
        var millisecond:int = isMillisecondSet() ? p.millisecond : 0;

        return new PointInTime(year, month, date, hour, minute, second, millisecond);
    }

    private function createLow():PointInTime {
        var month:int = isMonthSet() ? this.month : 1;
        var date:int = isDateSet() ? this.date : 1;
        var hour:int = isHourSet() ? this.hour : 0;
        var minute:int = isMinuteSet() ? this.minute : 0;
        var second:int = isSecondSet() ? this.second : 0;
        var millisecond:int = isMillisecondSet() ? this.millisecond : 0;
        return new PointInTime(this.year, month, date, hour, minute, second, millisecond);
    }

    public function toPrecision(precision:Precision):PointInTime {
        if (precision.equals(getPrecision())) return this;
        if (precision.greaterThan(getPrecision())) throw new ImprecisePointInTimeError(this);
        if (precision == Precision.SECOND) {
            return new PointInTime(getYear(), getMonth(), getDate(), getHour(), getMinute(), getSecond());
        } else if (precision == Precision.MINUTE) {
            return new PointInTime(getYear(), getMonth(), getDate(), getHour(), getMinute());
        } else if (precision == Precision.HOUR) {
            return new PointInTime(getYear(), getMonth(), getDate(), getHour());
        } else if (precision == Precision.DATE) {
            return new PointInTime(getYear(), getMonth(), getDate());
        } else if (precision == Precision.MONTH) {
            return new PointInTime(getYear(), getMonth());
        } else {
            return new PointInTime(getYear());
        }
    }

    private static function lessPrecise(t1:PointInTime, t2:PointInTime):PointInTime {
        if (t2.getPrecision().lessThan(t1.getPrecision()))
            return t2;
        return t1;
    }

    private static function mostPrecise(t1:PointInTime, t2:PointInTime):PointInTime {
        if (t2.getPrecision().greaterThan(t1.getPrecision()))
            return t2;
        return t1;
    }

    public static function fromDate(d:Date):PointInTime {
        return new PointInTime(d.fullYear, d.month + 1, d.date, d.hours, d.minutes, d.seconds, d.milliseconds);
    }

    public static function fromDateWithPrecision(d:Date, p:Precision):PointInTime {
        if (Precision.YEAR == p) {
            return new PointInTime(d.fullYear);
        } else if (Precision.MONTH == p) {
            return new PointInTime(d.fullYear, d.month + 1);
        } else if (Precision.DATE == p) {
            return new PointInTime(d.fullYear, d.month + 1, d.date);
        } else if (Precision.HOUR == p) {
            return new PointInTime(d.fullYear, d.month + 1, d.date, d.hours);
        } else if (Precision.MINUTE == p) {
            return new PointInTime(d.fullYear, d.month + 1, d.date, d.hours, d.minutes);
        } else if (Precision.SECOND == p) {
            return new PointInTime(d.fullYear, d.month + 1, d.date, d.hours, d.minutes, d.seconds);
        } else {
            return new PointInTime(d.fullYear, d.month + 1, d.date, d.hours, d.minutes, d.seconds, d.milliseconds);
        }
    }

    public static function now():PointInTime {
        return currentTimeStrategy.now();
    }

    public static function today():PointInTime {
        return currentTimeStrategy.now().toPrecision(Precision.DATE);
    }

    public static function compare(t1:PointInTime, t2:PointInTime, fields:Array = null):int {
        if (t1 == null && t2 == null) return 0;
        if (t1 != null && t2 == null) return 1;
        if (t1 == null && t2 != null) return -1;
        return t1.compareTo(t2);
    }

    /**
     * Comparison function that handles Date objects and PointInTime objects.
     */
    public static function dateCompare(t1:Object, t2:Object):int {
        if (t1 == null && t2 == null) return 0;
        if (t1 != null && t2 == null) return 1;
        if (t1 == null && t2 != null) return -1;
        if (t1 is PointInTime) {
            if (t2 is PointInTime) {
                return (t1 as PointInTime).compareTo(t2 as PointInTime);
            } else if (t2 is Date) {
                return (t1 as PointInTime).compareToDate(t2 as Date);
            } else {
                throw new ArgumentError("argument t2 must be an instance of Date or of PointInTime");
            }
        } else if (t1 is Date) {
            if (t2 is PointInTime) {
                var t:PointInTime = PointInTime.fromDate(t1 as Date);
                return t.compareTo(t2 as PointInTime);
            } else if (t2 is Date) {
                return ObjectUtil.dateCompare(t1 as Date, t2 as Date);
            } else {
                throw new ArgumentError("argument t2 must be an instance of Date or of PointInTime");
            }
        } else {
            throw new ArgumentError("argument t1 must be an instance of Date or of PointInTime");
        }
    }

    public static function setCurrentTimeStrategy(s:ICurrentTimeStrategy):void {
        currentTimeStrategy = (s != null ? s : new DefaultCurrentTimeStrategy());

    }
}
}
