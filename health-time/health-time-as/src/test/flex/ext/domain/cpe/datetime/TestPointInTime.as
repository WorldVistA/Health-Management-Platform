package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

public class TestPointInTime extends TestCase implements ICurrentTimeStrategy {

    public static const NOW:PointInTime = new PointInTime(2009, 12, 21, 11, 47, 21, 678);

    //
    // implementation of ICurrentTimeStrategy
    //
    public function now():PointInTime {
        return NOW;
    }

    public function testClone():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 3, 52);
        var clonedT:PointInTime = t.clone();
        assertNotNull(clonedT);
        assertTrue(t !== clonedT);
        assertTrue(t.equals(clonedT));

        t = new PointInTime(1975, 7, 23);
        clonedT = t.clone();
        assertNotNull(clonedT);
        assertTrue(t !== clonedT);
        assertTrue(t.equals(clonedT));

        t = new PointInTime(1975, 7, 23, 3, 52, 10, 46);
        clonedT = t.clone();
        assertNotNull(clonedT);
        assertTrue(t !== clonedT);
        assertTrue(t.equals(clonedT));
    }

    public function testCreateWithYear():void {
        var t:PointInTime = new PointInTime(1975);
        assertStrictlyEquals(Precision.YEAR, t.precision);
        assertFalse(t.isMonthSet());
        assertFalse(t.isDateSet());
        assertFalse(t.isHourSet());
        assertFalse(t.isMinuteSet());
        assertFalse(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals("1975", t.toString());

        try {
            t.month;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.date;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.hour;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.minute;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.second;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearAndMonth():void {
        var t:PointInTime = new PointInTime(1975, 7);
        assertStrictlyEquals(Precision.MONTH, t.precision);
        assertTrue(t.isMonthSet());
        assertFalse(t.isDateSet());
        assertFalse(t.isHourSet());
        assertFalse(t.isMinuteSet());
        assertFalse(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals("197507", t.toString());

        try {
            t.date;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.hour;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.minute;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.second;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearMonthAndDate():void {
        var t:PointInTime = new PointInTime(1975, 7, 23);
        assertEquals(Precision.DATE, t.precision);
        assertTrue(t.isMonthSet());
        assertTrue(t.isDateSet());
        assertFalse(t.isHourSet());
        assertFalse(t.isMinuteSet());
        assertFalse(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals("19750723", t.toString());

        try {
            t.hour;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.minute;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.second;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearMonthDateAndHour():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 15);
        assertEquals(Precision.HOUR, t.precision);
        assertTrue(t.isMonthSet());
        assertTrue(t.isDateSet());
        assertTrue(t.isHourSet());
        assertFalse(t.isMinuteSet());
        assertFalse(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        assertEquals("1975072315", t.toString());

        try {
            t.minute;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.second;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearMonthDateHourAndMinute():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 15, 23);
        assertEquals(Precision.MINUTE, t.precision);
        assertTrue(t.isMonthSet());
        assertTrue(t.isDateSet());
        assertTrue(t.isHourSet());
        assertTrue(t.isMinuteSet());
        assertFalse(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        assertEquals(23, t.minute);
        assertEquals("197507231523", t.toString());

        try {
            t.second;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearMonthDateHourMinuteAndSecond():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 15, 23, 42);
        assertEquals(Precision.SECOND, t.precision);
        assertTrue(t.isMonthSet());
        assertTrue(t.isDateSet());
        assertTrue(t.isHourSet());
        assertTrue(t.isMinuteSet());
        assertTrue(t.isSecondSet());
        assertFalse(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        assertEquals(23, t.minute);
        assertEquals(42, t.second);
        assertEquals("19750723152342", t.toString());

        try {
            t.millisecond;
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            assertStrictlyEquals(t, ex.pointInTime);
        }
    }

    public function testCreateWithYearMonthDateHourMinuteSecondAndMillisecond():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 15, 23, 42, 398);
        assertEquals(Precision.MILLISECOND, t.precision);
        assertTrue(t.isMonthSet());
        assertTrue(t.isDateSet());
        assertTrue(t.isHourSet());
        assertTrue(t.isMinuteSet());
        assertTrue(t.isSecondSet());
        assertTrue(t.isMillisecondSet());
        assertEquals(1975, t.year);
        assertEquals(7, t.month);
        assertEquals(23, t.date);
        assertEquals(15, t.hour);
        assertEquals(23, t.minute);
        assertEquals(42, t.second);
        assertEquals(398, t.millisecond);
        assertEquals("19750723152342.398", t.toString());
    }

    public function testEquality():void {
        var t1:PointInTime = new PointInTime(1975);
        var t2:PointInTime = new PointInTime(1975);
        assertEquals(t1.precision, t2.precision);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);

        t1 = new PointInTime(1975, 7);
        t2 = new PointInTime(1975, 7);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23);
        t2 = new PointInTime(1975, 7, 23);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10);
        t2 = new PointInTime(1975, 7, 23, 10);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10, 54);
        t2 = new PointInTime(1975, 7, 23, 10, 54);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10, 54, 41);
        t2 = new PointInTime(1975, 7, 23, 10, 54, 41);
        PointInTimeAssert.assertPointInTimeEquals(t1, t2);
    }

    public function testInequalityOfValue():void {
        var t1:PointInTime = new PointInTime(1975);
        var t2:PointInTime = new PointInTime(1984);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);

        t1 = new PointInTime(1975, 7);
        t2 = new PointInTime(1975, 3);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23);
        t2 = new PointInTime(1975, 7, 11);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10);
        t2 = new PointInTime(1975, 7, 23, 6);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10, 54);
        t2 = new PointInTime(1975, 7, 23, 10, 31);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);

        t1 = new PointInTime(1975, 7, 23, 10, 54, 17);
        t2 = new PointInTime(1975, 7, 23, 10, 54, 47);
        PointInTimeAssert.assertPointInTimeNotEquals(t1, t2);
    }

    public function testInequalityOfDifferentPrecision():void {
        var times:Array = new Array();
        times[0] = new PointInTime(1975);
        times[1] = new PointInTime(1975, 7);
        times[2] = new PointInTime(1975, 7, 23);
        times[3] = new PointInTime(1975, 7, 23, 10);
        times[4] = new PointInTime(1975, 7, 23, 10, 54);
        times[5] = new PointInTime(1975, 7, 23, 10, 54, 41);
        for (var i:int = 0; i < times.length; i++) {
            for (var j:int = 0; j < times.length; j++) {
                if (i == j)
                    continue;
                PointInTimeAssert.assertPointInTimeNotEquals(times[i], times[j]);
            }
        }
    }

    public function testComparisonOfSamePrecision():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1984, 3, 11);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(1975, 7, 23, 10, 30);
        t2 = new PointInTime(1984, 7, 23, 10, 30);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(1984, 7, 22, 10);
        t2 = new PointInTime(1984, 7, 23, 10);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(1984, 7, 23, 10);
        t2 = new PointInTime(1984, 7, 23, 10);
        assertTrue(t1.compareTo(t2) == 0);
        assertTrue(t2.compareTo(t1) == 0);
    }

    public function testComparisonOfDifferentPrecision():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1984, 3, 11, 10);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(2004, 12, 16, 18, 0);
        t2 = new PointInTime(2004, 12, 16, 18, 0, 1);
        assertTrue(t1.precision.compareTo(t2.precision) < 0);
        assertTrue(t2.precision.compareTo(t1.precision) > 0);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(2004, 12, 15);
        t2 = new PointInTime(2004, 12, 16, 18, 0, 1);
        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);

        t1 = new PointInTime(2004, 12, 17);
        t2 = new PointInTime(2004, 12, 16, 18, 0);
        assertTrue(t1.compareTo(t2) > 0);
        assertTrue(t2.compareTo(t1) < 0);
    }

    public function testComparisonWithNull():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        assertTrue(t1.compareTo(null) > 0);
    }

    public function testComparisonWithDate():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:Date = new Date(1975, 6, 23, 10, 55, 17, 244); // 6 is July
        assertTrue(t1.compareToDate(t2) < 0);

        t1 = new PointInTime(1975, 7, 23, 10, 55, 17, 244);
        assertTrue(t1.compareToDate(t2) == 0);
    }

    public function testAddYears():void {
        var t:PointInTime = new PointInTime(2003, 9, 9);
        t = t.addYears(2);
        assertEquals(2005, t.year);
        assertEquals(9, t.month);
        assertEquals(9, t.date);
    }

    public function testAddMonths():void {
        var t:PointInTime = new PointInTime(2003, 9, 9);
        t = t.addMonths(18);
        assertEquals(2005, t.year);
        assertEquals(3, t.month);
        assertEquals(9, t.date);
    }

    public function testAddDays():void {
        var t:PointInTime = new PointInTime(2003, 9, 9);
        t = t.addDays(60);
        assertEquals(2003, t.year);
        assertEquals(11, t.month);
        assertEquals(8, t.date);
    }

    public function testAddHours():void {
        var t:PointInTime = new PointInTime(2003, 9, 9, 18, 25);
        t = t.addHours(7);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(10, t.date);
        assertEquals(1, t.hour);
        assertEquals(25, t.getMinute());
    }

    public function testAddMinutes():void {
        var t:PointInTime = new PointInTime(2003, 9, 9, 18, 25);
        t = t.addMinutes(82);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(9, t.date);
        assertEquals(19, t.hour);
        assertEquals(47, t.getMinute());
    }

    public function testAddSeconds():void {
        var t:PointInTime = new PointInTime(2003, 9, 9, 18, 25, 56);
        t = t.addSeconds(93);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(9, t.date);
        assertEquals(18, t.hour);
        assertEquals(27, t.getMinute());
        assertEquals(29, t.getSecond());
    }

    public function testFromDate():void {
        var t:Date = new Date(1957, 6, 23, 10, 18, 57, 434);
        var p:PointInTime = PointInTime.fromDate(t);
        assertEquals(1957, p.year);
        assertEquals(7, p.month);
        assertEquals(23, p.date);
        assertEquals(10, p.hour);
        assertEquals(18, p.minute);
        assertEquals(57, p.second);
        assertEquals(434, p.millisecond);
    }

    public function testAddMilliseconds():void {
        var t:PointInTime = new PointInTime(2003, 9, 9, 18, 25, 56, 672);
        t = t.addMilliseconds(567);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(9, t.date);
        assertEquals(18, t.hour);
        assertEquals(25, t.getMinute());
        assertEquals(57, t.getSecond());
        assertEquals(239, t.getMillisecond());
    }

    public function testSubstractMilliseconds():void {
        var t:PointInTime = new PointInTime(2003, 9, 9, 18, 25, 56, 423);
        t = t.subtractMilliseconds(567);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(9, t.date);
        assertEquals(18, t.hour);
        assertEquals(25, t.getMinute());
        assertEquals(55, t.getSecond());
        assertEquals(856, t.getMillisecond());
    }

    public function testSubtractDays():void {
        var t:PointInTime = new PointInTime(2003, 9, 25);
        t = t.subtractDays(30);
        assertEquals(2003, t.year);
        assertEquals(8, t.month);
        assertEquals(26, t.date);
    }

    public function testSubtractMonths():void {
        var t:PointInTime = new PointInTime(2003, 9, 25);
        t = t.subtractMonths(18);
        assertEquals(2002, t.year);
        assertEquals(3, t.month);
        assertEquals(25, t.date);
    }

    public function testSubtractYears():void {
        var t:PointInTime = new PointInTime(2003, 9, 25);
        t = t.subtractYears(5);
        assertEquals(1998, t.year);
        assertEquals(9, t.month);
        assertEquals(25, t.date);
    }

    public function testSubtractPeriod():void {
        var t:PointInTime = new PointInTime(2003, 9, 25);
        t = t.subtractPeriod(new Period(5, 4, 0, 2));
        assertTrue(t.precision === Precision.DATE);
        assertEquals(1998, t.year);
        assertEquals(5, t.month);
        assertEquals(23, t.date);

        t = new PointInTime(2003, 9, 25);
        t = t.subtractPeriod(Period.months(18));
        assertEquals(2002, t.year);
        assertEquals(3, t.month);
        assertEquals(25, t.date);

        t = new PointInTime(2003, 9, 25);
        t = t.subtractPeriod(Period.weeks(2));
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(11, t.date);
    }

    public function testSubtractDuration():void {
        var t:PointInTime = new PointInTime(2003, 9, 25);
        t = t.subtractDuration(Duration.fromFields(23, 50, 40, 0));
        assertTrue(t.precision === Precision.DATE);
        assertEquals(2003, t.year);
        assertEquals(9, t.month);
        assertEquals(24, t.date);
    }

    public function testDifferenceWithPointInTimeOfSamePrecision():void {
        var t1:PointInTime = new PointInTime(2003, 9, 25);
        var t2:PointInTime = new PointInTime(2003, 8, 26);
        var d:Duration = t1.difference(t2);
        assertEquals(Duration.hours(720).getMillis(), d.getMillis());

        t1 = new PointInTime(2003, 9, 25, 10, 35, 12);
        t2 = new PointInTime(2003, 9, 25, 18, 12, 10);
        d = t1.difference(t2);
        assertEquals(Duration.fromFields(-7, -36, -58, 0).getMillis(), d.getMillis());
    }

    public function testDifferenceWithPointInTimeIfDifferentPrecision():void {
        var t1:PointInTime = new PointInTime(2003, 9, 25);
        var t2:PointInTime = new PointInTime(2003, 8, 26, 8, 30);
        try {
            t1.difference(t2);
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
        }
    }

    public function testPromoteYearPrecision():void {
        var t:PointInTime = new PointInTime(1975);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 1, 1, 0, 0, 0, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1976, 1, 1, 0, 0, 0, 0, i.getHigh());
    }

    public function testPromoteMonthPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 1, 0, 0, 0, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 8, 1, 0, 0, 0, 0, i.getHigh());
    }

    public function testPromoteDatePrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 0, 0, 0, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 24, 0, 0, 0, 0, i.getHigh());
    }

    public function testPromoteHourPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 0, 0, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 11, 0, 0, 0, i.getHigh());
    }

    public function testPromoteMinutePrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 42);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 42, 0, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 43, 0, 0, i.getHigh());
    }

    public function testPromoteSecondPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 42, 15);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 42, 15, 0, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 42, 16, 0, i.getHigh());
    }

    public function testPromoteMillisecondPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 42, 15, 532);
        var i:IntervalOfTime = t.promote();
        assertTrue(i.isLowClosed());
        assertFalse(i.isHighClosed());
        assertEquals(Precision.MILLISECOND, i.getLow().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 42, 15, 532, i.getLow());
        assertEquals(Precision.MILLISECOND, i.getHigh().precision);
        TestIntervalOfTime.assertFieldsEqual(1975, 7, 23, 10, 42, 15, 533, i.getHigh());
    }

    public function testBefore():void {
        var t1:PointInTime = new PointInTime(1975, 7, 24);
        var t2:PointInTime = new PointInTime(1975, 7, 23);
        assertTrue(t2.before(t1));
        assertFalse(t2.before(t2));
        t2 = new PointInTime(1975, 7, 24, 10, 0, 0, 0);
        assertFalse(t2.before(t1));
        t2 = new PointInTime(1975, 7, 22, 23, 59, 59, 999);
        assertTrue(t2.before(t1));

        t1 = new PointInTime(2004, 12, 15, 11, 39, 05);
        t2 = new PointInTime(2004, 12, 15, 16, 37, 25);
        assertTrue(t1.before(t2));
        assertTrue(t1.compareTo(t2) < 0);
    }

    public function testAfter():void {
        var t1:PointInTime = new PointInTime(1975, 7, 23);
        var t2:PointInTime = new PointInTime(1975, 7, 24);
        assertTrue(t2.after(t1));
        assertFalse(t2.after(t2));
        t2 = new PointInTime(1975, 7, 23, 23, 59, 59, 999);
        assertFalse(t2.after(t1));
        t2 = new PointInTime(1975, 7, 24, 0, 0, 0, 0);
        assertTrue(t2.after(t1));
    }

    public function testToDate():void {
        var t:PointInTime = new PointInTime(1975, 7, 23);
        try {
            var t2:Date = t.toDate();
            fail("expected imprecise point in time error");
        } catch (ex:ImprecisePointInTimeError) {
            // NOOP
        }
        t = new PointInTime(1975, 7, 23, 10, 55, 34, 123);
        var t1:Date = t.toDate();
        assertEquals(1975, t1.getFullYear());
        assertEquals(6, t1.month); // july is 6
        assertEquals(23, t1.date);
        assertEquals(10, t1.getHours());
        assertEquals(55, t1.getMinutes());
        assertEquals(34, t1.getSeconds());
        assertEquals(123, t1.getMilliseconds());
    }

    public function testToLesserPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 55);
        t = t.toPrecision(Precision.DATE);
        assertTrue(t.getPrecision().equals(Precision.DATE));
    }

    public function testToGreaterPrecision():void {
        var t:PointInTime = new PointInTime(1975, 7, 23, 10, 55);
        try {
            t = t.toPrecision(Precision.SECOND);
            fail("expected imprecise point in time error")
        } catch (e:ImprecisePointInTimeError) {
            // NOOP
        }
    }

    public function testMidnight():void {
        var midnight:PointInTime = new PointInTime(1984, 3, 31).toPointInTimeAtMidnight();

        assertEquals(1984, midnight.year);
        assertEquals(4, midnight.month);
        assertEquals(1, midnight.date);
        assertEquals(0, midnight.hour);
        assertEquals(0, midnight.getMinute());
        assertEquals(0, midnight.getSecond());
        assertEquals(0, midnight.getMillisecond());
    }

    public function testToday():void {
        var today:Date = new Date();

        var t:PointInTime = PointInTime.today();
        assertEquals(Precision.DATE, t.precision);
        assertEquals(today.fullYear, t.year);
        assertEquals(today.month, t.month - 1);
        assertEquals(today.date, t.date);
    }

    public function testNow():void {
        PointInTime.setCurrentTimeStrategy(this);

        var t:PointInTime = PointInTime.now();
        assertEquals(Precision.MILLISECOND, t.precision);
        assertEquals(NOW.year, t.year);
        assertEquals(NOW.month, t.month);
        assertEquals(NOW.date, t.date);
        assertEquals(NOW.hour, t.hour);
        assertEquals(NOW.minute, t.minute);
        assertEquals(NOW.second, t.second);
        assertEquals(NOW.millisecond, t.millisecond);

        PointInTime.setCurrentTimeStrategy(null);
    }
}
}
