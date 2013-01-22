package EXT.DOMAIN.edp.pointintime.chartClasses {

import flash.events.Event;

import EXT.DOMAIN.edp.pointintime.IntervalOfTime;
import EXT.DOMAIN.edp.pointintime.PointInTime;
import EXT.DOMAIN.edp.pointintime.Precision;

import mx.charts.DateTimeAxis;

public class DateTimeAxis extends mx.charts.DateTimeAxis {

		public function DateTimeAxis() {
			super();
		
			parseFunction = parse;
		}

		private var _dateRange:IntervalOfTime;

		[Bindable(event="dateRangeChanged")]
		public function get dateRange():IntervalOfTime {
			return _dateRange;
		}

		public function set dateRange(r:IntervalOfTime):void {
			if (_dateRange == null && r == null) return;
			if (_dateRange != null && _dateRange.equals(r)) return;
			_dateRange = r;
			dispatchEvent(new Event("dateRangeChanged"));
			if (_dateRange == null) {
				super.maximum = super.minimum = null;
			} else {
				super.minimum = _dateRange.low.toDate();
				super.maximum = _dateRange.high.toDate();
			}
		}

		override public function set minimum(value:Date):void {
			super.minimum = value;
			
			_dateRange = IntervalOfTime.fromDates(value, maximum);
			dispatchEvent(new Event("dateRangeChanged"));			
		}

		override public function set maximum(value:Date):void {
			super.maximum = value;
			
			_dateRange = IntervalOfTime.fromDates(minimum, value);
			dispatchEvent(new Event("dateRangeChanged"));			
		}

		private function parse(o:Object):Date {
			if (o is Date) {
				return o as Date;
			} else if (o is PointInTime) {
				var t:PointInTime = o as PointInTime;
				if (t.precision == Precision.MILLISECOND)
					return t.toDate();
				else
					return t.promote().center.toDate();
			} else {
				return new Date(Date.parse(o.toString()));
			}
		}
	}
}
