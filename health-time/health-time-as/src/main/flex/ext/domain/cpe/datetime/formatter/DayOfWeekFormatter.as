package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.DateFormatter;
import mx.formatters.Formatter;

public class DayOfWeekFormatter extends Formatter {
		
		public static const FORMAT:String = "EEE";
		public static const LONG_FORMAT:String = "EEEE";
		
		private var f:DateFormatter;

        private var _long:Boolean = false;

        [Bindable]
        [Inspectable]
        public function get long():Boolean {
            return _long;
        }

        public function set long(value:Boolean):void {
            _long = value;
            f = null;
        }
        
        private function getDateFormatter():DateFormatter {
			if (f == null) {
				f = new DateFormatter();
				f.formatString = long ? LONG_FORMAT : FORMAT;
			}
			return f;
		}
		
		public override function format(o:Object):String {
			if (o == null)
				return null;
			if (o is Date) {
				return getDateFormatter().format(o);
			} else if (o is PointInTime) {
				var p:PointInTime = o as PointInTime;
				if (p.precision.greaterThanOrEqual(Precision.DATE)) {
					var t:PointInTime = p.promote().center;
					var s:String = getDateFormatter().format(t.toDate());
					return s;
				} else {
					throw new ImprecisePointInTimeError(p);
				}
			} else {
				throw new ArgumentError("unable to format object");
			}
		}
	}
}
