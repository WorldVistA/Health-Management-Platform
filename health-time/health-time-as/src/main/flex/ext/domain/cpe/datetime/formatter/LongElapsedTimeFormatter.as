package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.Duration;

import mx.formatters.Formatter;

public class LongElapsedTimeFormatter extends Formatter {

    public override function format(value:Object):String {
        if (value == null)
            return null;
        if (value is Duration) {
            var d:Duration = value as Duration;
            return formatMilliseconds(d.getMillis());
        } else {
            var t:Number = new Number(value);
            return formatMilliseconds(t);
        }
    }

    private function formatMilliseconds(time:Number):String {
        if (time == 0) return "no elapsed time";

        var minutes:int = (time / (Duration.MILLIS_PER_SECOND * Duration.SECONDS_PER_MINUTE)) % Duration.MINUTES_PER_HOUR;
        if (minutes == 0) return "less than a minute";

        var hours:int = time / (Duration.MILLIS_PER_SECOND * Duration.SECONDS_PER_MINUTE * Duration.MINUTES_PER_HOUR);

        var result:String = "";
        if (hours > 0) {
            result += hours.toString() + " hour";
            if (hours >= 2)
                result += "s";
            result += " ";
        }
        result += minutes.toString() + " minute";
        if (minutes >= 2)
            result += "s";
        return result;
    }
}
}
