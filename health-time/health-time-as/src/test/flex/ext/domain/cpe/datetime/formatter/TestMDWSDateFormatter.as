package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

public class TestMDWSDateFormatter extends TestCase {
    private var formatter:MDWSDateFormatter = new MDWSDateFormatter();

	public function testFormatNull():void {
		 assertEquals("", formatter.format(null));
	}

    public function testFormatPointInTime():void {
        assertEquals("19750000", formatter.format(new PointInTime(1975)));
        assertEquals("19750700", formatter.format(new PointInTime(1975, 7)));
        assertEquals("19750723", formatter.format(new PointInTime(1975, 7, 23)));
        assertEquals("19750723.10", formatter.format(new PointInTime(1975, 7, 23, 10)));
        assertEquals("19750723.1056", formatter.format(new PointInTime(1975, 7, 23, 10, 56)));
        assertEquals("19750723.105614", formatter.format(new PointInTime(1975, 7, 23, 10, 56, 14)));
        assertEquals("19750723.105614", formatter.format(new PointInTime(1975, 7, 23, 10, 56, 14, 456)));
        assertEquals("20010000", formatter.format(new PointInTime(2001)));
    }

    public function testFormatDate():void {
        assertEquals("20010911.000000", formatter.format(new Date(2001, 8, 11, 0, 0, 0, 0))); // 8 is September
        assertEquals("19750723.105600", formatter.format(new Date(1975, 6, 23, 10, 56))); // 6 is July
    }

    public function testParseDate():void {
        var t:Date = MDWSDateFormatter.parseDate("19750723.105614");
        assertEquals(1975, t.fullYear);
        assertEquals(6, t.month); // 6 is July
        assertEquals(23, t.date);
        assertEquals(10, t.hours);
        assertEquals(56, t.minutes);
        assertEquals(14, t.seconds);

        t = MDWSDateFormatter.parseDate("19750723.10565");
        assertEquals(1975, t.fullYear);
        assertEquals(6, t.month); // 6 is July
        assertEquals(23, t.date);
        assertEquals(10, t.hours);
        assertEquals(56, t.minutes);
        assertEquals(50, t.seconds);

        t = MDWSDateFormatter.parseDate("19990121.1");
        assertEquals(1999, t.fullYear);
        assertEquals(0, t.month); // 0 is January
        assertEquals(21, t.date);
        assertEquals(10, t.hours);
        assertEquals(0, t.minutes);
        assertEquals(0, t.seconds);
    }

    public function testParseDateForMidnight():void {
        var t:Date = MDWSDateFormatter.parseDate("19750723.24");
        assertEquals(1975, t.fullYear);
        assertEquals(6, t.month); // 6 is July
        assertEquals(23, t.date);
        assertEquals(23, t.hours);
        assertEquals(59, t.minutes);
        assertEquals(59, t.seconds);
    }

    public function testParseDateFromBlankString():void {
        var t:Date = MDWSDateFormatter.parseDate("");
        assertNull(t);
        t = MDWSDateFormatter.parseDate(" ");
        assertNull(t);
        t = MDWSDateFormatter.parseDate(null);
        assertNull(t);
    }


    public function testParseDateWithWrongLengthString():void {
        try {
            MDWSDateFormatter.parseDate("19750723");
            fail("expected ArgumentError");
        } catch(e:ArgumentError) {
            // NOOP
        }
    }

    public function testParsePointInTime():void {
        var t:PointInTime = MDWSDateFormatter.parsePointInTime("19750000");
        assertEquals(Precision.YEAR, t.precision);
        assertEquals(1975, t.year);
        t = MDWSDateFormatter.parsePointInTime("19750700");
        assertEquals(Precision.MONTH, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        t = MDWSDateFormatter.parsePointInTime("19750723");
        assertEquals(Precision.DATE, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        t = MDWSDateFormatter.parsePointInTime("19750723.15");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        assertEquals(0, t.minute);
        assertEquals(0, t.second);
        t = MDWSDateFormatter.parsePointInTime("19750723.1057");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(10, t.hour);
        assertEquals(57, t.minute);
        assertEquals(0, t.second);
        t = MDWSDateFormatter.parsePointInTime("19750723.105713");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(10, t.hour);
        assertEquals(57, t.minute);
        assertEquals(13, t.second);
        t = MDWSDateFormatter.parsePointInTime("19981118.071");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1998, t.year);
        assertEquals(11, t.month);
        assertEquals(18, t.date);
        assertEquals(7, t.hour);
        assertEquals(10, t.minute);
        assertEquals(0, t.second);
        t = MDWSDateFormatter.parsePointInTime("19970711.07171");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1997, t.year);
        assertEquals(7, t.month);
        assertEquals(11, t.date);
        assertEquals(7, t.hour);
        assertEquals(17, t.minute);
        assertEquals(10, t.second);
        t = MDWSDateFormatter.parsePointInTime("19990121.1");
        assertEquals(Precision.SECOND, t.precision);
        assertEquals(1999, t.year);
        assertEquals(1, t.month);
        assertEquals(21, t.date);
        assertEquals(10, t.hour);
        assertEquals(0, t.minute);
        assertEquals(0, t.second);
        //        t = FileManDateFormatter.parsePointInTime("2990121.100");
        //        assertEquals(Precision.MINUTE, t.precision);
        //        assertEquals(1999, t.year);
        //        assertEquals(1, t.month);
        //        assertEquals(21, t.date);
        //        assertEquals(10, t.hour);
        //        assertEquals(0, t.minute);
        //        t = FileManDateFormatter.parsePointInTime("2750000.0");
        //        assertEquals(Precision.YEAR, t.precision);
        //        assertEquals(1975, t.year);
        //        t = FileManDateFormatter.parsePointInTime("2750000.000");
        //        assertEquals(Precision.YEAR, t.precision);
        //        assertEquals(1975, t.year);
        //        t = FileManDateFormatter.parsePointInTime("2981118.070");
        //        assertEquals(Precision.MINUTE, t.precision);
        //        assertEquals(1998, t.year);
        //        assertEquals(11, t.month);
        //        assertEquals(18, t.date);
        //        assertEquals(7, t.hour);
        //        assertEquals(0, t.minute);
        //        t = FileManDateFormatter.parsePointInTime("2970711.07170");
        //        assertEquals(Precision.SECOND, t.precision);
        //        assertEquals(1997, t.year);
        //        assertEquals(7, t.month);
        //        assertEquals(11, t.date);
        //        assertEquals(7, t.hour);
        //        assertEquals(17, t.minute);
        //        assertEquals(0, t.second);
        //        t = FileManDateFormatter.parsePointInTime("2750000.00000");
        //        assertEquals(Precision.YEAR, t.precision);
        //        assertEquals(1975, t.year);
    }

     public function testParsePointInTimeFromBlankString():void {
        var t:PointInTime = MDWSDateFormatter.parsePointInTime("");
        assertNull(t);
        t = FileManDateFormatter.parsePointInTime(" ");
        assertNull(t);
        t = FileManDateFormatter.parsePointInTime(null);
        assertNull(t);
    }
}
}
