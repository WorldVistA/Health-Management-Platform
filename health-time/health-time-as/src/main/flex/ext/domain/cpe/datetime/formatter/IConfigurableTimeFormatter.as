package EXT.DOMAIN.cpe.datetime.formatter {

	public interface IConfigurableTimeFormatter {
		[Inspectable]
		function get showSeconds():Boolean;

		function set showSeconds(show:Boolean):void;
		
		function format(o:Object):String;
	}
}
