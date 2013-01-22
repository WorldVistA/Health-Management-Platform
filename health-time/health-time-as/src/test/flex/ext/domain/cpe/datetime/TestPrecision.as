package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

public class TestPrecision extends TestCase {

     public function testEquals() :void {
        var pDefault:Precision = Precision.DATE;
        var p:Precision = Precision.YEAR;
        assertFalse(pDefault.equals(p));
        assertFalse(p.equals(pDefault));

        assertFalse(p.equals(null));
        assertTrue(pDefault.equals(Precision.DATE));
        assertTrue(p.equals(p));
    }

    public function testCompareTo():void {
        var pDefault:Precision = Precision.DATE;
        var p:Precision = Precision.YEAR;
        assertTrue(p.compareTo(pDefault) != 0);
        assertTrue(pDefault.compareTo(p) != 0);
        assertTrue(p.compareTo(p) == 0);
        var m:Precision = Precision.MILLISECOND;
        assertTrue(m.compareTo(p) > 0);
        assertTrue(m.compareTo(pDefault) > 0);
        assertTrue(p.compareTo(m) < 0);
        try {
            assertTrue(m.compareTo(null) > 0);
            fail("expected illegal argument error");
        } catch (e:ArgumentError) {
        }
    }

    public function testLessThan():void {
        var p1:Precision = Precision.MILLISECOND;
        var p2:Precision = Precision.MONTH;

        assertTrue(p2.lessThan(p1));
        assertFalse(p1.lessThan(p2));
    }

    public function testLessThanOrEqual():void {
        var p1:Precision = Precision.MILLISECOND;
        var p2:Precision = Precision.MONTH;

        assertTrue(p2.lessThanOrEqual(p1));
        assertFalse(p1.lessThanOrEqual(p2));

        p1 = Precision.MONTH;

        assertTrue(p2.lessThanOrEqual(p1));
        assertTrue(p1.lessThanOrEqual(p2));
    }

    public function testLesser():void {
        var p1:Precision = Precision.DATE;
        var p2:Precision = Precision.MONTH;

        assertStrictlyEquals(Precision.lesser(p1, p2), p2);
        assertStrictlyEquals(Precision.lesser(p2, p1), p2);

        assertStrictlyEquals(Precision.lesser(p2, p2), p2);
    }

    public function testGreater():void {
        var p1:Precision = Precision.DATE;
        var p2:Precision = Precision.MONTH;

        assertStrictlyEquals(Precision.greater(p1, p2), p1);
        assertStrictlyEquals(Precision.greater(p2, p1), p1);

        assertStrictlyEquals(Precision.greater(p1, p1), p1);
    }

    public function testGreaterThan():void {
        var p1:Precision = Precision.MILLISECOND;
        var p2:Precision = Precision.MONTH;

        assertTrue(p1.greaterThan(p2));
        assertFalse(p2.greaterThan(p1));
    }

    public function testGreaterThanOrEqual():void {
        var p1:Precision = Precision.MILLISECOND;
        var p2:Precision = Precision.MONTH;

        assertTrue(p1.greaterThanOrEqual(p2));
        assertFalse(p2.greaterThanOrEqual(p1));

        p1 = Precision.MONTH;

        assertTrue(p2.greaterThanOrEqual(p1));
        assertTrue(p1.greaterThanOrEqual(p2));
    }
}
}
