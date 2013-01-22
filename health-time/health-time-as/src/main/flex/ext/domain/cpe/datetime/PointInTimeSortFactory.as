package EXT.DOMAIN.cpe.datetime {
import mx.collections.Sort;
import mx.collections.SortField;

/**
 * Factory for creating PointInTime aware Sort objects.
 *
 * @see mx.collections.Sort;
 * @see EXT.DOMAIN.edp.pointintime.PointInTime;
 */
public class PointInTimeSortFactory {
    /**
     * Creates a 'descending' Sort object.  Descending is reverse chronological order.
     * @return a Sort configured to do a descending sort on a PointInTime and/or Date field.
     *
     * @see mx.collections.Sort
     */
    public static function createDescendingSort():Sort {
        var s:Sort = new Sort();
        var field:SortField = new SortField(null);
        field.descending = true;
        field.compareFunction = PointInTime.compare;
        s.fields = [field];
        return s;
    }
}
}
