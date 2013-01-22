package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestDefaultDateFormatter extends TestCase {

	public function testFormatNull():void {
		var f:DefaultDateFormatter = new DefaultDateFormatter();
        assertNull(f.format(null));
	}

    public function testFormatDate():void {
        var f:DefaultDateFormatter = new DefaultDateFormatter();
        var t:Date = new Date(1975, 6, 23, 10, 57); // 6 is july
        assertEquals("Jul 23,75", f.format(t));
    }

    public function testFormatPointInTime():void {
        var f:DefaultDateFormatter = new DefaultDateFormatter();
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 57);
        assertEquals("Jul 23,75", f.format(t));

        t = new PointInTime(1981, 9, 3);
        assertEquals("Sep 03,81", f.format(t));

        t = new PointInTime(1975, 7);
        assertEquals("Jul 1975", f.format(t));

        t = new PointInTime(1975);
        assertEquals("1975", f.format(t));
    }
}
}
