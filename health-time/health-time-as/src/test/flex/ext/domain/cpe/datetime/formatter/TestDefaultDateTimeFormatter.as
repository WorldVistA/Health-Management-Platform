package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestDefaultDateTimeFormatter extends TestCase {

    protected var f:DefaultDateTimeFormatter;

    override public function setUp():void {
        f = new DefaultDateTimeFormatter();
    }

    public function testDefaults():void {
        assertFalse(f.showSeconds);
    }

    public function testFormatNull():void {
		assertNull(f.format(null));
	}

    public function testFormatDate():void {
        var t:Date = new Date(1975, 6, 23, 10, 57, 18); // 6 is july
        assertEquals("Jul 23,75 10:57", f.format(t));
    }

    public function testFormatDateShowSeconds():void {
        f.showSeconds = true;
        var t:Date = new Date(1975, 6, 23, 10, 57, 18); // 6 is july
        assertEquals("Jul 23,75 10:57:18", f.format(t));
    }

    public function testFormatPointInTime():void {
        var t:PointInTime = new PointInTime(1975, 7, 23);
        assertEquals("Jul 23,75", f.format(t));

        t = new PointInTime(1981, 9, 3);
        assertEquals("Sep 03,81", f.format(t));

        t = new PointInTime(1975, 7);
        assertEquals("Jul 1975", f.format(t));

        t = new PointInTime(1975);
        assertEquals("1975", f.format(t));

        t = new PointInTime(1975, 7, 23, 10);
        assertEquals("Jul 23,75 10-11", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57);
        assertEquals("Jul 23,75 10:57", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57, 42);
        assertEquals("Jul 23,75 10:57", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57, 42, 398);
        assertEquals("Jul 23,75 10:57", f.format(t));
    }
    
    public function testFormatPointInTimeShowSeconds():void {
    	f.showSeconds = true;

        var t:PointInTime = new PointInTime(1975, 7, 23);
        assertEquals("Jul 23,75", f.format(t));

        t = new PointInTime(1981, 9, 3);
        assertEquals("Sep 03,81", f.format(t));

        t = new PointInTime(1975, 7);
        assertEquals("Jul 1975", f.format(t));

        t = new PointInTime(1975);
        assertEquals("1975", f.format(t));

        t = new PointInTime(1975, 7, 23, 10);
        assertEquals("Jul 23,75 10-11", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57);
        assertEquals("Jul 23,75 10:57", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57, 42);
        assertEquals("Jul 23,75 10:57:42", f.format(t));

        t = new PointInTime(1975, 7, 23, 10, 57, 42, 398);
        assertEquals("Jul 23,75 10:57:42", f.format(t));
    }
}
}
