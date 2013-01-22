package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.Assert;

public class PointInTimeAssert {
     public static function assertPointInTimeNotEquals(t1:PointInTime, t2:PointInTime):void {
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t2.equals(t1));
    }

    public static function assertPointInTimeEquals(t1:PointInTime, t2:PointInTime):void {
        Assert.assertTrue(t1.equals(t2));
        Assert.assertTrue(t2.equals(t1));
    }
}
}
