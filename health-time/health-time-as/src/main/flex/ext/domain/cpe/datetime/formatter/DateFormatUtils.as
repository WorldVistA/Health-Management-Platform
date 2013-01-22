package EXT.DOMAIN.cpe.datetime.formatter {
public class DateFormatUtils {
    public static function formatTwoDigits(n:int):String {
        if (n < 10) return "0" + n;
        if (n < 100) return String(n);
        var nStr:String = String(n);
        return nStr.substr(nStr.length - 2, 2);
    }
}
}
