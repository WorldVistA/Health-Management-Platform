package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.Duration;

public class TestElapsedTimeFormatter extends TestCase {
    public function testFormatNull():void {
        var f:ElapsedTimeFormatter = new ElapsedTimeFormatter();
        assertNull(f.format(null));
    }

    public function testFormatNumber():void {
        var f:ElapsedTimeFormatter = new ElapsedTimeFormatter();
        assertEquals("0:00", f.format(34)); // number of milliseconds
        assertEquals("0:01", f.format(Duration.minutes(1).millis));
        assertEquals("1:59", f.format(Duration.fromFields(1, 59, 43, 760).millis));
        assertEquals("234:12", f.format(Duration.fromFields(234, 12, 43, 760).millis));
    }

    public function testFormatDuration():void {
        var f:ElapsedTimeFormatter = new ElapsedTimeFormatter();
        assertEquals("0:00", f.format(Duration.milliseconds(1)));
        assertEquals("0:01", f.format(Duration.minutes(1)));
        assertEquals("1:59", f.format(Duration.fromFields(1, 59, 43, 760)));
        assertEquals("234:12", f.format(Duration.fromFields(234, 12, 43, 760).millis));
    }
}
}
