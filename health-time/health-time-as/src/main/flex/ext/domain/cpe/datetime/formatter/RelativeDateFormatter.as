package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.ImprecisePointInTimeError;
import EXT.DOMAIN.cpe.datetime.IntervalOfTime;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.Precision;

import mx.formatters.Formatter;

public class RelativeDateFormatter extends Formatter {

    private var dateFormatter:Formatter = new DefaultDateFormatter();
    private var dayOfWeekFormatter:Formatter = new DayOfWeekFormatter();

    override public function format(o:Object):String {
        if (o is Date) {
            return formatPointInTime(PointInTime.fromDate(o as Date));
        } else if (o is PointInTime) {
            return formatPointInTime(o as PointInTime);
        } else {
            throw new ArgumentError("unable to format object");
        }
    }

    private function formatPointInTime(t:PointInTime):String {
        if (t.precision.lessThan(Precision.DATE))
            throw new ImprecisePointInTimeError(t);
        var today:PointInTime = PointInTime.today();
        if (today.equals(t) || today.promote().contains(t))
            return "Today";
        var yesterday:PointInTime = today.subtractDays(1);
        if (yesterday.equals(t) || yesterday.promote().contains(t))
            return "Yesterday";
        var lastWeek:IntervalOfTime = new IntervalOfTime(today.subtractDays(7), today);
        if (lastWeek.contains(t))
            return dayOfWeekFormatter.format(t);
        return dateFormatter.format(t);
    }
}
}
