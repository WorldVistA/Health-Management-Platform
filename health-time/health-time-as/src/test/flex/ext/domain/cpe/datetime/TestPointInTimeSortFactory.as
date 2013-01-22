package EXT.DOMAIN.cpe.datetime {
import flexunit.framework.TestCase;

import EXT.DOMAIN.cpe.datetime.formatter.HL7DateFormatter;

import mx.collections.ArrayCollection;
import mx.collections.Sort;

public class TestPointInTimeSortFactory extends TestCase {

    public function testCreateSort():void {
        var c:ArrayCollection = new ArrayCollection();
        var sort:Sort = PointInTimeSortFactory.createDescendingSort();
        assertNotNull(sort);
        c.sort = sort;

        var t1:PointInTime = HL7DateFormatter.parsePointInTime("20090223110900");
        var t2:PointInTime = HL7DateFormatter.parsePointInTime("20090223111300");
        var t3:PointInTime = HL7DateFormatter.parsePointInTime("20090223111700");

        c.addItem(t1);
        c.addItem(t2);
        c.addItem(t3);

        c.refresh();

        // sort should be most recent first
        assertStrictlyEquals(t3, c.getItemAt(0));
        assertStrictlyEquals(t2, c.getItemAt(1));
        assertStrictlyEquals(t1, c.getItemAt(2));
    }

}
}
