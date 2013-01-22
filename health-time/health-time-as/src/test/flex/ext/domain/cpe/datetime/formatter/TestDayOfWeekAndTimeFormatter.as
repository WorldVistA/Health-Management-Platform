package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestDayOfWeekAndTimeFormatter extends TestCase {

    private var f:DayOfWeekAndTimeFormatter = new DayOfWeekAndTimeFormatter();

    public function testDefaults():void {
        assertFalse(f.long);
        assertFalse(f.showSeconds);
    }

    public function testFormatNull():void {
        assertNull(f.format(null));
    }

    public function testFormatDate():void {
        assertEquals("Thu 14:57", f.format(new Date(2009, 7, 27, 14, 57, 23))); // month '7' is August
    }

    public function testFormatDateShowSeconds():void {
        f.showSeconds = true;
        assertEquals("Thu 14:57:23", f.format(new Date(2009, 7, 27, 14, 57, 23))); // month '7' is August
    }

    public function testFormatDateLong():void {
        f.long = true;
        assertEquals("Thursday 14:57", f.format(new Date(2009, 7, 27, 14, 57, 23))); // month '7' is August
    }

    public function testFormatDateLongShowSeconds():void {
        f.long = true;
        f.showSeconds = true;
        assertEquals("Thursday 14:57:23", f.format(new Date(2009, 7, 27, 14, 57, 23))); // month '7' is August
    }

    public function testFormatPointInTime():void {
        assertEquals("Wed", f.format(new PointInTime(2009, 8, 26)));
        assertEquals("Wed 14:57", f.format(new PointInTime(2009, 8, 26, 14, 57)));
        assertEquals("Wed 14:57", f.format(new PointInTime(2009, 8, 26, 14, 57, 23)));
    }

    public function testFormatPointInTimeLong():void {
        f.long = true;
        assertEquals("Wednesday", f.format(new PointInTime(2009, 8, 26)));
        assertEquals("Wednesday 14:57", f.format(new PointInTime(2009, 8, 26, 14, 57)));
        assertEquals("Wednesday 14:57", f.format(new PointInTime(2009, 8, 26, 14, 57, 23)));
    }

    public function testFormatPointInTimeImprecise():void {
        try {
            f.format(new PointInTime(2009, 8));
            fail("expected imprecise point in time error")
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
        try {
            f.format(new PointInTime(2009, 8, 26, 14));
            fail("expected imprecise point in time error")
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
    }
}
}
