package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.TimeZoneOffset;

import mx.formatters.DateFormatter;
import mx.formatters.Formatter;

/**
     * <p>DateTime format for HL7 timestamp:
     * YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]][+/-ZZZZ]
     * <p/>
     * <p>In the current and future versions of HL7, the precision is indicated by limiting the number of digits used.
     * Thus, YYYY is used to specify a precision of "year," YYYYMM specifies a precision of "month," YYYYMMDD specifies
     * a precision of "day," YYYYMMDDHH is used to specify a precision of "hour," YYYYMMDDHHMM is used to specify a
     * precision of "minute," YYYYMMDDHHMMSS is used to specify a precision of seconds, and YYYYMMDDHHMMSS.SSSS is used
     * to specify a precision of ten thousandths of a second. In each of these cases, the time zone is an optional
     * component. Maximum length of the time stamp is 24 characters. </p>
     * <p/>
     * <p>Examples: <samp> |19760704010159-0600| 1:01:59 on July 4, 1976 in the Eastern Standard Time zone.
     * |19760704010159-0500| 1:01:59 on July 4, 1976 in the Eastern Daylight Saving Time zone. |198807050000|   Midnight
     * of the night extending from July 4 to July 5, 1988 in the local time zone of the sender. |19880705|    Same as
     * prior example, but precision extends only to the day.  Could be used for a birthdate, if the time of birth is
     * unknown. </samp> </p>
     */
public class HL7DateFormatter extends Formatter {

    private static const MIN_HL7_DATE_LENGTH:int = 4;
    private static const MAX_HL7_DATE_LENGTH:int = 24;

    private static var _instance:Formatter;

    public static function get instance():Formatter {
        return getInstance();
    }

    public static function getInstance():Formatter {
        if (_instance == null) {
            _instance = new HL7DateFormatter();
        }
        return _instance;
    }

    public static function parsePointInTime(text:String):PointInTime {
        if (text == null) return null;
        if (text.length == 0) return null;
        if (text.length < MIN_HL7_DATE_LENGTH || text.length > MAX_HL7_DATE_LENGTH) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        if (text.length == 15) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);

        var timestamp:String = text;
        var offset:TimeZoneOffset = null;
        var timezoneOffsetStartIndex:int = getTimezoneOffsetStartIndex(text);
        if (timezoneOffsetStartIndex != -1) {
            timestamp = text.substring(0, timezoneOffsetStartIndex);
            offset = TimeZoneOffset.parse(text.substring(timezoneOffsetStartIndex, text.length));
        }

        var year:int = int(timestamp.substring(0, 4));
        if (isNaN(year)) throw new ArgumentError("Invalid year");
        if (timestamp.length == MIN_HL7_DATE_LENGTH)
            return new PointInTime(year, -1, -1, -1, -1, -1, -1, offset);

        if (timestamp.length == 5) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        var month:int = int(timestamp.substring(4, 6));
        if (isNaN(month)) throw new ArgumentError("Invalid month");
        if (timestamp.length == 6)
            return new PointInTime(year, month, -1, -1, -1, -1, -1, offset);

        if (timestamp.length == 7) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        var date:int = int(timestamp.substring(6, 8));
        if (isNaN(date)) throw new ArgumentError("Invalid date");
        if (timestamp.length == 8)
            return new PointInTime(year, month, date, -1, -1, -1, -1, offset);

        if (timestamp.length == 9) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        var hours:int = int(timestamp.substring(8, 10));
        if (isNaN(hours)) throw new ArgumentError("Invalid hours");
        if (timestamp.length == 10)
            return new PointInTime(year, month, date, hours, -1, -1, -1, offset);

        if (timestamp.length == 11) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        var minutes:int = int(timestamp.substring(10, 12));
        if (isNaN(minutes)) throw new ArgumentError("Invalid minutes");
        if (timestamp.length == 12)
            return new PointInTime(year, month, date, hours, minutes, -1, -1, offset);

        if (timestamp.length == 13) throw new ArgumentError("invalid HL7 timestamp string length: " + text.length);
        var seconds:int = int(timestamp.substring(12, 14));
        if (isNaN(seconds)) throw new ArgumentError("Invalid seconds");
        if (timestamp.length == 14)
            return new PointInTime(year, month, date, hours, minutes, seconds, -1, offset);

        var milliseconds:int = int(text.substring(15, 18));

        return new PointInTime(year, month, date, hours, minutes, seconds, milliseconds, offset);;
    }

    private static function getTimezoneOffsetStartIndex(text:String):int {
        var sign:int = text.lastIndexOf("+");
        if (sign == -1) sign = text.lastIndexOf("-");
        return sign;
    }

    public static function parseDate(text:String):Date {
    	if (text == null) return null;
    	if (text.length == 0) return null;
        if (text.length != 23) throw new ArgumentError("Invalid HL7 timestamp string: must be of format YYYYMMDDHHMMSS.SSS+/-ZZZZ"); // TODO: support ten-thousands of a second soon

        var year:Number = Number(text.substring(0, 4));
        var month:Number = Number(text.substring(4, 6));
        var date:Number = Number(text.substring(6, 8));
        var hours:Number = Number(text.substring(8, 10));
        var minutes:Number = Number(text.substring(10, 12));
        var seconds:Number = Number(text.substring(12, 14));
        var milliseconds:Number = Number(text.substring(15, 18));

        if (isNaN(year)) throw new ArgumentError("Invalid year");
        if (isNaN(month)) throw new ArgumentError("Invalid month");
        if (isNaN(date)) throw new ArgumentError("Invalid date");
        if (isNaN(hours)) throw new ArgumentError("Invalid hours");
        if (isNaN(minutes)) throw new ArgumentError("Invalid minutes");
        if (isNaN(seconds)) throw new ArgumentError("Invalid seconds");
        if (isNaN(milliseconds)) throw new ArgumentError("Invalid milliseconds");

        var offsetSign:String = text.charAt(18);
        var offsetHours:Number = Number(text.substring(19, 21));
        var offsetMinutes:Number = Number(text.substring(21, 23));

        var offsetMilliseconds:Number = ((offsetHours * 60) + offsetMinutes) * 60 * 1000;
        if (offsetSign == "+")
            milliseconds += offsetMilliseconds;
        else if (offsetSign == "-")
            milliseconds -= offsetMilliseconds;
        else
            throw new ArgumentError("Invalid time zone offset");
        return new Date(Date.UTC(year, month - 1, date, hours, minutes, seconds, milliseconds));
    }

    private static var dateFormatter:DateFormatter

    public function HL7DateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = new DateFormatter();
            dateFormatter.formatString = "YYYYMMDDJJNNSS";
        }
    }

    public override function format(o:Object):String {
		if (o == null) return "";
        if (o is Date) {
            var t:Date = o as Date;
            var s:String = dateFormatter.format(t);
            s += "." + t.milliseconds;
            s += formatTimeZoneOffset(t);
            return s;
        } else if (o is PointInTime) {
            var p:PointInTime = o as PointInTime;
            return p.toString();
        } else {
            throw new ArgumentError("unable to format object");
        }
    }

    private function formatTimeZoneOffset(t:Date):String {
        var offsetMinutes:Number = t.getTimezoneOffset() % 60;
        var offsetHours:Number = t.getTimezoneOffset() / 60;
        var offsetSign:String = offsetMinutes < 0 ? "+" : "-";
        var offsetString:String = offsetSign + (offsetHours < 10 ? "0" + offsetHours : offsetHours) + (offsetMinutes < 10 ? "0" + offsetMinutes : offsetMinutes);
        return offsetString;
    }
}
}
