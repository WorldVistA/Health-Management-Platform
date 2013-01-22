package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

public class TestTimeZoneOffset extends TestCase {
    public function testConstruct():void {
        var offset:TimeZoneOffset = new TimeZoneOffset(-360);
        assertEquals(-360, offset.minutes);
        assertEquals(-6, offset.hours);
        assertEquals(-360 * 60 * 1000, offset.milliseconds);
    }

    public function testOffsetForHoursMinutes():void {
        var offset:TimeZoneOffset = TimeZoneOffset.forOffsetHoursMinutes(-7, 0);
        assertEquals(-420, offset.minutes);
        assertEquals(-7, offset.hours);
        
        offset = TimeZoneOffset.forOffsetHoursMinutes(3, 30);
        assertEquals(210, offset.minutes);        
        assertEquals(3.5, offset.hours);
    }

    public function testParse():void {
        var offset:TimeZoneOffset = TimeZoneOffset.parse("-0700");
        assertEquals(-420, offset.minutes);

        offset = TimeZoneOffset.parse("+0330");
        assertEquals(210, offset.minutes);
    }

    public function testParseInvalidLengthStrings():void {
        try {
            TimeZoneOffset.parse("+030000");
            fail("Expected ArgumentError");
        } catch(e:ArgumentError) {
            // NOOP
        }
        try {
            TimeZoneOffset.parse("-070");
            fail("Expected ArgumentError");
        } catch(e:ArgumentError) {
            // NOOP
        }
    }

    public function testParseInvalidSignCharacter():void {
        try {
            TimeZoneOffset.parse("Z0900");
            fail("Expected ArgumentError");
        } catch(e:ArgumentError) {
            // NOOP
        }
    }
}
}
