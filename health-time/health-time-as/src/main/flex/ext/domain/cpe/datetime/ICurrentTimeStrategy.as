package EXT.DOMAIN.cpe.datetime
{
	/**
	 * Represents a method of retreiving the current time as a PointInTime object.
	 * 
	 * Implementations might return current system time in the Flash VM, or might return
	 * time retrieved from a server, for instance.
	 * 
	 * @see EXT.DOMAIN.cpe.datetime.PointInTime.now
	 * @see EXT.DOMAIN.cpe.datetime.PointInTime.setCurrentTimeStrategy
	 */
	public interface ICurrentTimeStrategy {
		function now():PointInTime;
	}
}
