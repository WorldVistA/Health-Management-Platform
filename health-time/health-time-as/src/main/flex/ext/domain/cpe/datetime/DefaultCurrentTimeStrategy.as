package EXT.DOMAIN.cpe.datetime {
public class DefaultCurrentTimeStrategy implements ICurrentTimeStrategy {
		public function now():PointInTime {
			return PointInTime.fromDate(new Date());
		}
	}
}
