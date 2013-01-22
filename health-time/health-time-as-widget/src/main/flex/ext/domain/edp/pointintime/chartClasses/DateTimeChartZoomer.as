package EXT.DOMAIN.edp.pointintime.chartClasses {
import EXT.DOMAIN.edp.pointintime.IntervalOfTime;
import EXT.DOMAIN.edp.widget.chartClasses.ChartZoomer;

import mx.charts.chartClasses.IAxis;

/**
	 * ChartZoomer that is custom DateTimeAxis aware.
	 */
	public class DateTimeChartZoomer extends ChartZoomer {
		public function DateTimeChartZoomer() {
			super();
		}
		
		override protected function setAxisRange(axis:IAxis, min:Number, max:Number):void {
			if (axis is DateTimeAxis) {
				DateTimeAxis(axis).dateRange = IntervalOfTime.fromDates(new Date(min), new Date(max));
			} else {
				super.setAxisRange(axis, min, max);
			}
			
		}
	}
}
