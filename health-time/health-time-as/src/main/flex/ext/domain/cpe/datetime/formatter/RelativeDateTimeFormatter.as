package EXT.DOMAIN.cpe.datetime.formatter {
import mx.formatters.Formatter;

public class RelativeDateTimeFormatter extends Formatter{

    private var _dateFormatter:Formatter = new RelativeDateFormatter();
    private var _timeFormatter:Formatter = new DefaultTimeFormatter();
    
    override public function format(value:Object):String {
        return _dateFormatter.format(value) + " " + _timeFormatter.format(value);
    }
}
}
