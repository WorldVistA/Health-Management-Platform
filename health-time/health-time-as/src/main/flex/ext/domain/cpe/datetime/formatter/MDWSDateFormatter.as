package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.util.StringUtil;

import mx.formatters.Formatter;

public class MDWSDateFormatter extends Formatter {
    public function MDWSDateFormatter() {
    }

    public override function format(o:Object):String {
        if (o == null) return "";
        if (o is Date) {
            return formatDate(o as Date);
        } else if (o is PointInTime) {
            return formatPointInTime(o as PointInTime);
        } else {
            throw new ArgumentError("unable to format object");
        }
    }

    private function formatPointInTime(t:PointInTime):String {
        var fileManStr:String = FileManDateFormatter.getInstance().format(t);
        return fileMan2Mdws(fileManStr);
    }

    private function formatDate(t:Date):String {
        var fileManStr:String = FileManDateFormatter.getInstance().format(t);
        return fileMan2Mdws(fileManStr);
    }

    public static function parseDate(text:String):Date {
        var fileManDateTime:String = mdws2FileMan(text);
        return FileManDateFormatter.parseDate(fileManDateTime);
    }

    public static function parsePointInTime(text:String):PointInTime {
        var fileManDateTime:String = mdws2FileMan(text);
        return FileManDateFormatter.parsePointInTime(fileManDateTime);
    }

    private static function fileMan2Mdws(text:String):String {
        if (StringUtil.isBlank(text)) return null;
        var c:String = text.substr(0, 1);
        var century:int = int(c);
        century += 17;
        var mdwsDateTime:String = century.toString() + text.substr(1, text.length - 1);
        return mdwsDateTime;
    }

    private static function mdws2FileMan(text:String):String {
        if (StringUtil.isBlank(text)) return null;
        var c:String = text.substr(0, 2);
        var century:int = int(c);
        century -= 17;
        var fileManDateTime:String = century.toString() + text.substr(2, text.length - 2);
        return fileManDateTime;
    }

}
}
