package EXT.DOMAIN.cpe.datetime {
public class Precision {

    public static const YEAR:Precision = new Precision(1, "year");
    public static const MONTH:Precision = new Precision(2, "month");
    public static const DATE:Precision = new Precision(3, "date");
    public static const HOUR:Precision = new Precision(4, "hour");
    public static const MINUTE:Precision = new Precision(5, "minute");
    public static const SECOND:Precision = new Precision(6, "second");
    public static const MILLISECOND:Precision = new Precision(7, "millisecond");

    private static var locked:Boolean = false;

    {
        locked = true;
    }

    private var _ordinal:int;
    private var _p:String;

    public function Precision(ordinalNum:int, p:String) {
        if (locked) {
            throw new Error("You can't instantiate Precision");
        }
        _ordinal = ordinalNum;
        _p = p;
    }

    public function toString():String {
        return _p;
    }

    public function compareTo(p:Precision):int {
        if (p == null) throw new ArgumentError("cannot compare precision to null");
        if (_ordinal > p._ordinal) {
            return 1;
        } else if (_ordinal < p._ordinal) {
            return -1;
        }
        return 0;
    }

    public function equals(p:Precision):Boolean {
        return this === p;
    }

    public function lessThan(p:Precision):Boolean {
        return compareTo(p) < 0;
    }

    public function lessThanOrEqual(p:Precision):Boolean {
        return this === p || lessThan(p);
    }

    public function greaterThan(p:Precision):Boolean {
        return compareTo(p) > 0;
    }

    public function greaterThanOrEqual(p:Precision):Boolean {
        return this === p || greaterThan(p);
    }

    public static function lesser(p1:Precision, p2:Precision):Precision {
        if (p2.lessThan(p1)) {
            return p2;
        }
        return p1;
    }

    public static function greater(p1:Precision, p2:Precision):Precision {
        if (p2.greaterThan(p1)) {
            return p2;
        }
        return p1;
    }
}
}
