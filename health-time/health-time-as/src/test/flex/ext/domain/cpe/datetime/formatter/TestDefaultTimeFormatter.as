package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestDefaultTimeFormatter extends TestCase {
    public function testFormatNull():void {
        var f:DefaultTimeFormatter = new DefaultTimeFormatter();
        assertNull(f.format(null));
    }

    public function testFormatDate():void {
        var f:DefaultTimeFormatter = new DefaultTimeFormatter();
        assertFalse(f.showSeconds);
        var t:Date = new Date(1975, 6, 23, 10, 57, 18); // 6 is july
        assertEquals("10:57", f.format(t));
    }

    public function testFormatDateShowSeconds():void {
        var f:DefaultTimeFormatter = new DefaultTimeFormatter();
        f.showSeconds = true;
        var t:Date = new Date(1975, 6, 23, 10, 57, 18); // 6 is july
        assertEquals("10:57:18", f.format(t));
    }

    public function testFormatPointInTime():void {
        var f:DefaultTimeFormatter = new DefaultTimeFormatter();
        assertFalse(f.showSeconds);
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 57, 18);
        assertEquals("10:57", f.format(t));

        try {
            t = new PointInTime(1981, 9, 3);
            f.format(t);
            fail("expected imprecise point in time error");
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
    }

    public function testFormatPointInTimeShowSeconds():void {
        var f:DefaultTimeFormatter = new DefaultTimeFormatter();
        f.showSeconds = true;
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 57, 18);
        assertEquals("10:57:18", f.format(t));

        try {
            t = new PointInTime(1981, 9, 3);
            f.format(t);
            fail("expected imprecise point in time error");
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
    }
}
}
