package EXT.DOMAIN.cpe.datetime.formatter {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.ICurrentTimeStrategy;
import EXT.DOMAIN.cpe.datetime.PointInTime;

public class TestRelativeDateFormatter extends TestCase implements ICurrentTimeStrategy {

    private static const MOCK_NOW:PointInTime = new PointInTime(2009, 8, 28, 16, 05, 48);

    private var f:RelativeDateFormatter;

    override public function setUp():void {
        f = new RelativeDateFormatter();
        PointInTime.setCurrentTimeStrategy(this);
    }

    override public function tearDown():void {
       PointInTime.setCurrentTimeStrategy(null);
    }

    public function now():PointInTime {
        return MOCK_NOW;
    }

    public function testTodayPointInTime():void {
        var t:PointInTime = new PointInTime(2009, 8, 28);
        assertEquals("Today", f.format(t));
        t = new PointInTime(2009, 8, 28, 12, 34, 12, 340);
        assertEquals("Today", f.format(t));
    }

    public function testYesterdayPointInTime():void {
        var t:PointInTime = new PointInTime(2009, 8, 27);
        assertEquals("Yesterday", f.format(t));
         t = new PointInTime(2009, 8, 27, 12, 34, 12, 340);
        assertEquals("Yesterday", f.format(t));
    }

    public function testLastWeekPointInTime():void {
        var t:PointInTime = new PointInTime(2009, 8, 24);
        assertEquals("Mon", f.format(t));
    }

     public function testMoreThanAWeekAgoPointInTime():void {
        var t:PointInTime = new PointInTime(2009, 8, 20);
        assertEquals("Aug 20,09", f.format(t));
    }
}
}
