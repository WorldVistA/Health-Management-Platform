package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.Duration;

import mx.formatters.Formatter;

public class ElapsedTimeFormatter extends Formatter
{

    public override function format(value:Object):String
    {
        if (value == null) return null;
        if (value is Duration) {
            var d:Duration = value as Duration;
            return formatMilliseconds(d.getMillis());
        } else {
            var t:Number = new Number(value);
            return formatMilliseconds(t);
        }
    }

    private function formatMilliseconds(time:Number):String {
        var hours:int = time / (Duration.MILLIS_PER_SECOND * Duration.SECONDS_PER_MINUTE * Duration.MINUTES_PER_HOUR);
        var minutes:int = (time / (Duration.MILLIS_PER_SECOND * Duration.SECONDS_PER_MINUTE)) % Duration.MINUTES_PER_HOUR;

        var hrString:String = hours.toString();
        var minString:String = DateFormatUtils.formatTwoDigits(minutes);

        return hrString + ":" + minString;
    }
}
}
