package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestDayOfWeekFormatter extends TestCase {

    public function testDefaults():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        assertFalse(f.long);
    }

    public function testFormatNull():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        assertNull(f.format(null));
    }

    public function testFormatDate():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        assertEquals("Thu", f.format(new Date(2009, 7, 27))); // month '7' is August
    }

    public function testFormatDateLong():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        f.long = true;
        assertEquals("Thursday", f.format(new Date(2009, 7, 27))); // month '7' is August
    }

    public function testFormatPointInTime():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        assertEquals("Wed", f.format(new PointInTime(2009, 8, 26)));
    }

    public function testFormatPointInTimeLong():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        f.long = true;
        assertEquals("Wednesday", f.format(new PointInTime(2009, 8, 26)));
    }

    public function testFormatPointInTimeImprecise():void {
        var f:DayOfWeekFormatter = new DayOfWeekFormatter();
        try {
            f.format(new PointInTime(2009, 8));
            fail("expected imprecise point in time error")
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
    }
}
}
