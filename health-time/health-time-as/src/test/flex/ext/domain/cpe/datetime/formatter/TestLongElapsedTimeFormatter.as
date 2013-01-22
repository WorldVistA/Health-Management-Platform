package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.Duration;

public class TestLongElapsedTimeFormatter extends TestCase {
    public function testFormatNull():void {
        var f:LongElapsedTimeFormatter = new LongElapsedTimeFormatter();
        assertNull(f.format(null));
    }

    public function testFormatNumber():void {
        var f:LongElapsedTimeFormatter = new LongElapsedTimeFormatter();
        assertEquals("no elapsed time", f.format(0));
        assertEquals("less than a minute", f.format(34)); // number of milliseconds
        assertEquals("1 minute", f.format(Duration.minutes(1).millis));
        assertEquals("1 hour 59 minutes", f.format(Duration.fromFields(1, 59, 43, 760).millis));
        assertEquals("234 hours 12 minutes", f.format(Duration.fromFields(234, 12, 43, 760).millis));
    }

    public function testFormatDuration():void {
        var f:LongElapsedTimeFormatter = new LongElapsedTimeFormatter();
        assertEquals("no elapsed time", f.format(Duration.ZERO));
        assertEquals("less than a minute", f.format(Duration.milliseconds(1)));
        assertEquals("1 minute", f.format(Duration.minutes(1)));
        assertEquals("1 hour 59 minutes", f.format(Duration.fromFields(1, 59, 43, 760)));
        assertEquals("234 hours 12 minutes", f.format(Duration.fromFields(234, 12, 43, 760).millis));
    }
}
}
