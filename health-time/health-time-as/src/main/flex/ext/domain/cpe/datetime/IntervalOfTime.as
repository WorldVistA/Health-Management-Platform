package EXT.DOMAIN.cpe.datetime {
public class IntervalOfTime {

    private var _low:PointInTime;
    private var _high:PointInTime;
    private var _lowClosed:Boolean;
    private var _highClosed:Boolean;

    public function IntervalOfTime(t1:PointInTime, t2:PointInTime, lowClosed:Boolean = true,
                                   highClosed:Boolean = false, equalEndpointsAllowed:Boolean = false) {
        if (t2.equals(t1) && !equalEndpointsAllowed) {
            throw new ArgumentError(
                    "The end point should be unequal to the low point in time when equalEndpointsAllowed is false");
        }
        this._lowClosed = lowClosed;
        this._highClosed = highClosed;
        if (t1.after(t2)) {
            setLow(t2);
            setHigh(t1);
        } else {
            setLow(t1);
            setHigh(t2);
        }
    }

    private function setLow(low:PointInTime):void {
        if (Precision.MILLISECOND.equals(low.getPrecision())) {
            this._low = low.clone();
        } else {
            this._low = low.promote().getLow();
        }
    }

    private function setHigh(high:PointInTime):void {
        if (Precision.MILLISECOND.equals(high.getPrecision())) {
            this._high = high.clone();
        } else {
            this._high = high.promote().getHigh();
            if (isHighClosed()) {
                this._high = this._high.subtractMilliseconds(1);
            }
        }
    }

    public function get low():PointInTime {
        return _low;
    }

    public function get high():PointInTime {
        return _high;
    }

    public function getLow():PointInTime {
        return low;
    }

    public function getHigh():PointInTime {
        return high;
    }

    public function isHighClosed():Boolean {
        return _highClosed;
    }

    public function isLowClosed():Boolean {
        return _lowClosed;
    }

    public function get center():PointInTime {
        var c:PointInTime = getLow().clone();
        return c.addDuration(new Duration(getWidth().getMillis() / 2));
    }

    public function getCenter():PointInTime {
        return center;
    }

    public function get width():Duration {
        var millis:Number = _high.toDate().getTime() - _low.toDate().getTime();
        return new Duration(millis);
    }

    public function getWidth():Duration {
        return width;
    }

    //    public Period toPeriod() {
    //        return new Period(low, high);
    //    }
    //
    //    public Period toPeriod(PeriodType periodType) {
    //        return new Period(low, high, periodType);
    //    }

    public function demote():PointInTime {
        return center;
    }

    public function contains(t:PointInTime):Boolean {
        return containsInterval(t.promote());
    }

    public function containsDate(t:Date):Boolean {
        return contains(PointInTime.fromDate(t)); // TODO: compare directly against low and high? (if you do it, remember to unit test)
    }

    public function containsInterval(i:IntervalOfTime):Boolean {
        return getLow().before(i.getLow()) && getHigh().after(i.getLow());
    }

    public function clone():IntervalOfTime {
        return new IntervalOfTime(low.clone(), high.clone(), isLowClosed(), isHighClosed());
    }

    public function intersection(i:IntervalOfTime):IntervalOfTime {
        return intersect(this, i);
    }

    public function hull(i:IntervalOfTime):IntervalOfTime {
        return convexHull(this, i);
    }

    public function intersects(i:IntervalOfTime):Boolean {
        return intersection(i) != null;
    }

    /**
     * Returns an IntervalOfTime that has both endpoints closed.  If the low endpoint is open it will move it forward by one millisecond.
     * If the high endpoint is closed, it will move it backward by one millisecond.
     * @return an IntervalOfTime with closed endpoints baed on the endpoints of this interval.
     */
    public function toClosed():IntervalOfTime {
        var t1:PointInTime = getLow();
        if (!isLowClosed()) {
            t1 = t1.addMilliseconds(1);
        }
        var t2:PointInTime = getHigh();
        if (!isHighClosed()) {
            t2 = t2.subtractMilliseconds(1);
        }
        return new IntervalOfTime(t1, t2, true, true);
    }

    /**
     * Literal form of IntervalOfTime is the HL7 literal form of the endpoints separated by a ';' and surrounded by
     * square brackets either '[' or ']' depending on whether or not each endpoint is open or closed.
     * <p>
     * Example: May 12, 1987 from 8 to 9:30 PM is "[198705122000;198705122130]".
     * </p>
     * @return
     */
    public function toString():String {
        var s:String;
        if (isLowClosed()) {
            s = "[";
        } else {
            s = "]";
        }
        s += getLow().toString();
        s += ";";
        s += getHigh().toString();
        if (isHighClosed()) {
            s += ']';
        } else {
            s += '[';
        }
        return s;
    }

    public function equals(i:IntervalOfTime):Boolean {
        if (i == null) return false;
        return isLowClosed() == i.isLowClosed()
                && isHighClosed() == i.isHighClosed()
                && getLow().equals(i.getLow()) && getHigh().equals(i.getHigh());
    }

    public static function intersect(i1:IntervalOfTime, i2:IntervalOfTime):IntervalOfTime {
        if (i1.getLow().before(i2.getLow())) {
            if (i2.getLow().after(i1.getHigh())) {
                return null;
            }
            return new IntervalOfTime(i2.getLow(), i1.getHigh());
        }
        if (i1.getLow().after(i2.getHigh())) {
            return null;
        }
        return new IntervalOfTime(i1.getLow(), i2.getHigh());
    }

    public static function convexHull(i1:IntervalOfTime, i2:IntervalOfTime):IntervalOfTime {
        var t1:PointInTime;
        var t2:PointInTime;
        var lowClosed:Boolean;
        var highClosed:Boolean;
        if (i1.low.before(i2.low)) {
            t1 = i1.low;
            lowClosed = i1.isLowClosed();
        } else {
            t1 = i2.low;
            lowClosed = i2.isLowClosed();
        }
        if (i1.high.after(i2.high)) {
            t2 = i1.high;
            highClosed = i1.isHighClosed();
        } else {
            t2 = i2.high;
            highClosed = i2.isHighClosed();
        }
        return new IntervalOfTime(t1, t2, lowClosed, highClosed);
    }

    public static function fromDates(t1:Date, t2:Date, lowClosed:Boolean = true,
                                     highClosed:Boolean = false, equalEndpointsAllowed:Boolean = false):IntervalOfTime {
        return new IntervalOfTime(PointInTime.fromDate(t1), PointInTime.fromDate(t2), lowClosed, highClosed, equalEndpointsAllowed);
    }

    public static function today():IntervalOfTime {
        return PointInTime.today().promote();
    }

    public static function thisYear():IntervalOfTime {
        var today:PointInTime = PointInTime.today();
        return forYear(today.year);
    }

    public static function thisMonth():IntervalOfTime {
        var today:PointInTime = PointInTime.today();
        return forMonth(today.year, today.month);
    }

    public static function yearToDate():IntervalOfTime {
        var today:PointInTime = PointInTime.today();
        return new IntervalOfTime(new PointInTime(today.year, 1, 1), today, true, true);
    }

    public static function forYear(year:int):IntervalOfTime {
        return new PointInTime(year).promote();
    }

    public static function forMonth(year:int, month:int):IntervalOfTime {
        return new PointInTime(year, month).promote();
    }

    public static function forDay(year:int, month:int, date:int):IntervalOfTime {
        return new PointInTime(year, month, date).promote();
    }

    public static function forTheLast(period:Period):IntervalOfTime {
        return untilNow(period);
    }

    public static function untilNow(period:Period):IntervalOfTime {
        var now:PointInTime = PointInTime.now();
        return new IntervalOfTime(now.subtractPeriod(period), now, true, true);
    }

    public static function untilToday(period:Period):IntervalOfTime {
        var today:PointInTime = PointInTime.today();
        return new IntervalOfTime(today.subtractPeriod(period), today, true, true);
    }
}
}
