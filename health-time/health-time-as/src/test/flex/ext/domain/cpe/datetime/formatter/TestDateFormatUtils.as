package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

public class TestDateFormatUtils extends TestCase {
    public function testFormatTwoDigits():void {
        assertEquals("00", DateFormatUtils.formatTwoDigits(0));
        assertEquals("01", DateFormatUtils.formatTwoDigits(1));
        assertEquals("02", DateFormatUtils.formatTwoDigits(2));
        assertEquals("03", DateFormatUtils.formatTwoDigits(3));
        assertEquals("04", DateFormatUtils.formatTwoDigits(4));
        assertEquals("05", DateFormatUtils.formatTwoDigits(5));
        assertEquals("06", DateFormatUtils.formatTwoDigits(6));
        assertEquals("07", DateFormatUtils.formatTwoDigits(7));
        assertEquals("08", DateFormatUtils.formatTwoDigits(8));
        assertEquals("09", DateFormatUtils.formatTwoDigits(9));
        assertEquals("10", DateFormatUtils.formatTwoDigits(10));
        assertEquals("99", DateFormatUtils.formatTwoDigits(99));
        assertEquals("00", DateFormatUtils.formatTwoDigits(100));
        assertEquals("23", DateFormatUtils.formatTwoDigits(11111123));
    }
}
}
