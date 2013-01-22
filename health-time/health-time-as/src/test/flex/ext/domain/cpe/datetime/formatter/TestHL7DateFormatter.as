package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

public class TestHL7DateFormatter extends TestCase {

	public function testFormatNull():void {
		 var f:HL7DateFormatter = new HL7DateFormatter();
		 assertEquals("", f.format(null));
	}

    public function testFormatDate():void {
        var f:HL7DateFormatter = new HL7DateFormatter();
        var t:Date = new Date(1975, 6, 23, 10, 56, 14, 123);  // month 6 is July
        var s:String = f.format(t);
        var offsetMinutes:Number = t.getTimezoneOffset() % 60;
        var offsetHours:Number = t.getTimezoneOffset() / 60;
        var offsetSign:String = offsetMinutes < 0 ? "+" : "-";
        var offsetString:String = offsetSign + (offsetHours < 10 ? "0" + offsetHours : offsetHours) + (offsetMinutes < 10 ? "0" + offsetMinutes : offsetMinutes);
        assertEquals("19750723105614.123" + offsetString, s);
    }

    public function testFormatPointInTime():void {
        var f:HL7DateFormatter = new HL7DateFormatter();
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 56, 14, 123);
        assertEquals("19750723105614.123", f.format(t));
    }

    public function testParseDate():void {
        var offsetMinutes:Number = new Date().getTimezoneOffset();
        trace(offsetMinutes);

        var t:Date = HL7DateFormatter.parseDate("19750723105614.123-0700");
        assertNotNull(t);
        assertEquals(1975, t.getUTCFullYear());
        assertEquals(6, t.getUTCMonth()); // 6 is July
        assertEquals(23, t.getUTCDate());
        assertEquals(3, t.getUTCHours());
        assertEquals(56, t.getUTCMinutes());
        assertEquals(14, t.getUTCSeconds());
        assertEquals(123, t.getUTCMilliseconds());
    }

    public function testParseDateMissingTimeZone():void {
        try {
            var t:Date = HL7DateFormatter.parseDate("19750723105614.123");
            fail("expected ArgumentError");
        } catch (e:ArgumentError) {
            // NOOP
        }
    }

    public function testParseDateInvalidYear():void {
        try {
            var t:Date = HL7DateFormatter.parseDate("19RE0723105614.123-0700");
            fail("expected ArgumentError");
        } catch (e:ArgumentError) {
            // NOOP
        }
    }

	public function testParseDateWithNullArgOrEmptyString():void {
       assertNull(HL7DateFormatter.parseDate(null));
       assertNull(HL7DateFormatter.parseDate(""));
    }

    public function testParsePointInTime():void {
        var t:PointInTime = HL7DateFormatter.parsePointInTime("1975");
        assertEquals(Precision.YEAR, t.precision);
        assertEquals(1975, t.year);
        t = HL7DateFormatter.parsePointInTime("197507");
        assertEquals(Precision.MONTH, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        t = HL7DateFormatter.parsePointInTime("19750723");
        assertEquals(Precision.DATE, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        t = HL7DateFormatter.parsePointInTime("1975072315");
        assertEquals(Precision.HOUR, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        t = HL7DateFormatter.parsePointInTime("197507231057");
        assertEquals(Precision.MINUTE, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(10, t.hour);
        assertEquals(57, t.minute);
        t = HL7DateFormatter.parsePointInTime("19750723105713");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(10, t.hour);
        assertEquals(57, t.minute);
        assertEquals(13, t.second);
        try {
            t = HL7DateFormatter.parsePointInTime("19981118071");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("1997071107171");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("199901211");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        t = HL7DateFormatter.parsePointInTime("1999012110");
        assertEquals(Precision.HOUR, t.precision);
        assertEquals(1999, t.year);
        assertEquals(1, t.month);
        assertEquals(21, t.date);
        assertEquals(10, t.hour);
        try {
            t = HL7DateFormatter.parsePointInTime("19990121100");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("197500000");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("19750000000");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("19981118070");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("1997071107170");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("1975000000000");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
        try {
            t = HL7DateFormatter.parsePointInTime("20091103120354.");
            fail("expected ArgumentError")
        } catch (e:ArgumentError) {
            // NOOP
        }
    }

    public function testParsePointInTimeWithNullArgOrEmptyString():void {
       assertNull(HL7DateFormatter.parsePointInTime(null));
       assertNull(HL7DateFormatter.parsePointInTime(""));
    }

    public function testParsePointInTimeWithTimeZoneOffset():void {
        var t:PointInTime = HL7DateFormatter.parsePointInTime("1975-0900");
        assertEquals(Precision.YEAR, t.precision);
        assertEquals(1975, t.year);
        assertFalse(t.isLocal());
        assertNotNull(t.timezoneOffset);
        assertEquals(-540, t.timezoneOffset.minutes);

        t = HL7DateFormatter.parsePointInTime("1975+0330");
        assertEquals(Precision.YEAR, t.precision);
        assertEquals(1975, t.year);
        assertFalse(t.isLocal());
        assertNotNull(t.timezoneOffset);
        assertEquals(210, t.timezoneOffset.minutes);
    }
}
}
