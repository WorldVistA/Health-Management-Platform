package EXT.DOMAIN.edp.pointintime.dataGridClasses {
import EXT.DOMAIN.edp.core.model.ApplicationModel;
import EXT.DOMAIN.edp.pointintime.PointInTime;
import EXT.DOMAIN.edp.widget.dataGridClasses.DeepDataGridColumn;

import mx.formatters.Formatter;

import org.swizframework.Swiz;

/**
	 * DataGridColumn that runs the value specified by the dataField through the default
	 * session date formatters.
	 *
	 * TODO: hook up comparator for sorting 
	 */
	public class DateTimeDataGridColumn extends DeepDataGridColumn {
		
		[Autowire]
		public var app:ApplicationModel;
		
		[Bindable]
		[Inspectable]
		public var showTime:Boolean = true;
		
		[Bindable]
		[Inspectable]
		public var formatter:Formatter = null;
		
		public function DateTimeDataGridColumn(columnName:String = null) {
			super(columnName);
			
			this.sortCompareFunction = compare;
		}
		
		override protected function valueToLabel(value:Object):String {
			if (formatter != null)
				return formatter.format(value);
				
			if (app == null)
				Swiz.autowire(this);
			if (app == null || app.session == null) 
				return super.valueToLabel(value);
			try {
				if (showTime)
					return app.session.dateTimeFormatter.format(value);
				else
					return app.session.dateFormatter.format(value);
			} catch (e:ArgumentError) {
				return "Unable to format '" +dataField+ "' as date/time";
			}
			return null;
		}
		
		private function compare(o1:Object, o2:Object):int {
			var t1:Object = itemToValue(o1);
			var t2:Object = itemToValue(o2);
			return PointInTime.dateCompare(t1, t2);
		}
	}
}
