package EXT.DOMAIN.cpe.datetime {
import flash.errors.IllegalOperationError;

public class ImprecisePointInTimeError extends IllegalOperationError {

    private var t:PointInTime;

    public function ImprecisePointInTimeError(t:PointInTime) {
        super("the specified point in time was not precise enough to support the requested operation");
        this.t = t;
    }

    public function get pointInTime():PointInTime {
        return t;
    }
}
}
