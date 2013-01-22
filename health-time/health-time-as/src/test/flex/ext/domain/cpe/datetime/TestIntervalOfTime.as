package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

public class TestIntervalOfTime extends TestCase {

    public static function assertFieldsEqual(
            year:int,
            month:int,
            date:int,
            hours:int,
            minutes:int,
            seconds:int,
            milliseconds:int,
            t:PointInTime):void {
        assertEquals(year, t.getYear());
        assertEquals(month, t.getMonth());
        assertEquals(date, t.getDate());
        assertEquals(hours, t.getHour());
        assertEquals(minutes, t.getMinute());
        assertEquals(seconds, t.getSecond());
        assertEquals(milliseconds, t.getMillisecond());
    }

    public function testCreateWithSamePrecision():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 8, 12);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals("[19750723000000.000;19810813000000.000[", i.toString());
        assertFalse(t1 === i.getLow());
        assertFalse(t2 === i.getHigh());
        assertTrue(Precision.MILLISECOND == i.getLow().getPrecision());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertTrue(Precision.MILLISECOND == i.getHigh().getPrecision());
        assertFieldsEqual(1981, 8, 13, 0, 0, 0, 0, i.getHigh());
    }

    public function testCreateWithDifferentPrecision():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 8, 11, 10, 35, 54, 134);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertFalse(t1 === i.getLow());
        assertFalse(t2 === i.getHigh());
        assertEquals("[19750723000000.000;19810811103554.134[", i.toString());
        assertTrue(Precision.MILLISECOND == i.getLow().getPrecision());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertTrue(Precision.MILLISECOND == i.getHigh().getPrecision());
        assertFieldsEqual(1981, 8, 11, 10, 35, 54, 134, i.getHigh());
    }

    public function testCreateWithHighBeforeLow():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 3, 11);
        var i:IntervalOfTime = new IntervalOfTime(t2, t1);
        assertTrue(Precision.MILLISECOND == i.getLow().getPrecision());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertTrue(Precision.MILLISECOND == i.getHigh().getPrecision());
        assertFieldsEqual(1981, 3, 12, 0, 0, 0, 0, i.getHigh());
    }

    public function testCreateWithLowAndHighClosed():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 3, 11);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2, true, true);
        assertTrue(i.isLowClosed());
        assertTrue(i.isHighClosed());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertFieldsEqual(1981, 3, 11, 23, 59, 59, 999, i.getHigh());
    }

    public function testCreateWithLowAndHighOpen():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 3, 11);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2, false, false);
        assertFalse(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertFieldsEqual(1981, 3, 12, 0, 0, 0, 0, i.getHigh());
    }

    public function testCreateWithLowOpenAndHighClosed():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1981, 3, 11);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2, false, true);
        assertFalse(i.isLowClosed());
        assertTrue(i.isHighClosed());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertFieldsEqual(1981, 3, 11, 23, 59, 59, 999, i.getHigh());
    }

    public function testWidth():void {
        var t1:PointInTime = PointInTime.fromDate(new Date(1111111111));
        var t2:PointInTime = PointInTime.fromDate(new Date(2222222222));
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        var width:Duration = i.getWidth();
        assertEquals(1111111111, width.getMillis());
    }

    public function testCenterOfEqualPrecision():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1975, 7, 25);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        var center:PointInTime = i.getCenter();
        assertTrue(Precision.MILLISECOND == center.getPrecision());
        assertFieldsEqual(1975, 7, 24, 12, 0, 0, 0, center);
    }

    public function testContains():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1984, 3, 11);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        assertTrue(i.contains(new PointInTime(1980)));
        assertFalse(i.contains(new PointInTime(1974)));
    }

    public function testClone():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23, 0, 0, 0, 0);
        var t2:PointInTime = new PointInTime(1984, 3, 11, 0, 0, 0, 0);
        var i:IntervalOfTime = new IntervalOfTime(t1, t2);
        var clonedInterval:IntervalOfTime = i.clone();
        PointInTimeAssert.assertPointInTimeEquals(i.getLow(), clonedInterval.getLow());
        PointInTimeAssert.assertPointInTimeEquals(i.getHigh(), clonedInterval.getHigh());
        assertFalse(i.getLow() === clonedInterval.getLow());
        assertFalse(i.getHigh() === clonedInterval.getHigh());
    }

    public function testIntersection():void {
        var t1:PointInTime = new PointInTime(1975);
        var t2:PointInTime = new PointInTime(1984);
        assertNull(t1.promote().intersection(t2.promote()));

        var i1:IntervalOfTime = new IntervalOfTime(new PointInTime(1975), new PointInTime(1984));
        var i2:IntervalOfTime = new IntervalOfTime(new PointInTime(1980), new PointInTime(1990));
        var intersection1:IntervalOfTime = i1.intersection(i2);
        assertTrue(intersection1.isLowClosed());
        assertFalse(intersection1.isHighClosed());
        assertFieldsEqual(1980, 1, 1, 0, 0, 0, 0, intersection1.getLow());
        assertFieldsEqual(1985, 1, 1, 0, 0, 0, 0, intersection1.getHigh());

        var intersection2:IntervalOfTime = i2.intersection(i1);
        assertTrue(intersection1.equals(intersection2));
    }

    public function testConvexHull():void {
        var t1:PointInTime = new PointInTime(1975);
        var t2:PointInTime = new PointInTime(1990);
        assertNotNull(t1.promote().hull(t2.promote()));

        var i1:IntervalOfTime = new IntervalOfTime(new PointInTime(1975), new PointInTime(1984));
        var i2:IntervalOfTime = new IntervalOfTime(new PointInTime(1980), new PointInTime(1990));

        var h1:IntervalOfTime = i1.hull(i2);
        assertTrue(h1.isLowClosed());
        assertFalse(h1.isHighClosed());
        assertFieldsEqual(1975, 1, 1, 0, 0, 0, 0, h1.getLow());
        assertFieldsEqual(1991, 1, 1, 0, 0, 0, 0, h1.getHigh());

        var h2:IntervalOfTime = i2.hull(i1);
        assertTrue(h1.equals(h2));
    }

    public function testNullIntervalOfTime():void {
        var pit:PointInTime = new PointInTime(2000, 1, 1, 0, 0, 0, 0);
        var interval:IntervalOfTime = null;
        try {
            interval = new IntervalOfTime(pit, pit);
            fail("should have thrown an exception");
        } catch (ex:ArgumentError) {
            // NOOP
        }
        interval = new IntervalOfTime(pit, pit, true, false, true);
        PointInTimeAssert.assertPointInTimeEquals(interval.getLow(), interval.getHigh());
    }

    public function testToClosedFromLowClosedHighOpen():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23, 0, 0, 0, 0);
        var t2:PointInTime = new PointInTime(1981, 3, 11, 0, 0, 0, 0);
        var i1:IntervalOfTime = new IntervalOfTime(t1, t2, true, false);
        var i2:IntervalOfTime = i1.toClosed();
        assertTrue(i2.isLowClosed());
        assertTrue(i2.isHighClosed());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i2.getLow());
        assertFieldsEqual(1981, 3, 10, 23, 59, 59, 999, i2.getHigh());
    }

    public function testToClosedFromLowOpenHighOpen():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23, 0, 0, 0, 0);
        var t2:PointInTime = new PointInTime(1981, 3, 11, 0, 0, 0, 0);
        var i1:IntervalOfTime = new IntervalOfTime(t1, t2, false, false);
        var i2:IntervalOfTime = i1.toClosed();
        assertTrue(i2.isLowClosed());
        assertTrue(i2.isHighClosed());
        assertFieldsEqual(1975, 7, 23, 0, 0, 0, 1, i2.getLow());
        assertFieldsEqual(1981, 3, 10, 23, 59, 59, 999, i2.getHigh());
    }
}
}
