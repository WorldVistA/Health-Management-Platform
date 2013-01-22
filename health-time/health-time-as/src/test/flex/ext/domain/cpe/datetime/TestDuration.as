package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

public class TestDuration extends TestCase {

    public function testConstruct():void {
        var d:Duration = new Duration(53434);
        assertEquals(53434, d.getMillis());
        assertEquals(53434, Duration.milliseconds(53434).getMillis());
    }

    public function testConstructSeconds():void {
        assertEquals(1000, Duration.seconds(1).getMillis());
        assertEquals(3000, Duration.seconds(3).getMillis());
    }

     public function testConstructMinutes():void {
        assertEquals(1000 * 60, Duration.minutes(1).getMillis());
        assertEquals(3000 * 60, Duration.minutes(3).getMillis());
    }

     public function testConstructHours():void {
        assertEquals(1000 * 60 * 60, Duration.hours(1).getMillis());
        assertEquals(3000 * 60 * 60, Duration.hours(3).getMillis());
    }

    public function testConstructFromFields():void {
        assertEquals(Duration.hours(3).getMillis() + Duration.minutes(7).getMillis() + Duration.seconds(23).getMillis() + Duration.milliseconds(567).getMillis(), Duration.fromFields(3, 7, 23, 567).getMillis());
    }
    
    public function testGetSeconds():void {
    	var d:Duration = Duration.seconds(53434);
    	assertEquals(53434, d.seconds);
    	assertEquals(53434, d.getSeconds());
    	
    	d = new Duration(1343);
    	assertEquals(1.343, d.seconds);
    	assertEquals(1.343, d.getSeconds());
    }
    
     public function testGetMinutes():void {
    	var d:Duration = Duration.minutes(23434);
    	assertEquals(23434, d.minutes);
    }
   
    public function testGetHours():void {
    	var d:Duration = Duration.hours(2534);
    	assertEquals(2534, d.hours);
    }
}
}
