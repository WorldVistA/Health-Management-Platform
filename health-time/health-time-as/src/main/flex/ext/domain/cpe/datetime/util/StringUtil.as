package EXT.DOMAIN.cpe.datetime.util {

import mx.utils.StringUtil;

public class StringUtil {

    /**
     * Checks if a String is empty ("") or null.
     *
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     *
     * @param str  the String to check, may be null
     * @return true if the String is empty or null
     */
    public static function isEmpty(str:String):Boolean
    {
        if (str == null || str.length == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Checks if a String is not empty ("") or null.
     *
     * StringUtil.isNotEmpty(null)      = false
     * StringUtil.isNotEmpty("")        = false
     * StringUtil.isNotEmpty(" ")       = true
     * StringUtil.isNotEmpty("bob")     = true
     * StringUtil.isNotEmpty("  bob  ") = true
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static function isNotEmpty(str:String):Boolean
    {
        return !isEmpty(str);
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     *
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static function isBlank(str:String):Boolean
    {
        return isEmpty(mx.utils.StringUtil.trim(str));
    }

    /**
     * Checks if a String is not whitespace, empty ("") or null.
     *
     * StringUtil.isNotBlank(null)      = false
     * StringUtil.isNotBlank("")        = false
     * StringUtil.isNotBlank(" ")       = false
     * StringUtil.isNotBlank("bob")     = true
     * StringUtil.isNotBlank("  bob  ") = true
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static function isNotBlank(str:String):Boolean
    {
        return !isBlank(str);
    }

    /**
     * String returning <code>null</code> if the String is
     * empty ("") after the trim or if it is <code>null</code>.
     *
     * <pre>
     * StringUtil.trimToNull(null)          = null
     * StringUtil.trimToNull("")            = null
     * StringUtil.trimToNull("     ")       = null
     * StringUtil.trimToNull("abc")         = "abc"
     * StringUtil.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String,
     *  <code>null</code> if only chars &lt;= 32, empty or null String input
     */
    public static function trimToNull(str:String):String
    {
        str = mx.utils.StringUtil.trim(str);
        return isEmpty(str) ? null : str;
    }

    /**
     * Compares two string ignoring case to see if they are equal
     *
     * @param str1 First string to be compared
     * @param str2 Second string to be compared
     * @return true if the strings are equal
     */
    public static function equalsIgnoreCase(str1:String, str2:String):Boolean
    {
        return (toLowerCase(str1) == toLowerCase(str2));
    }


	public static function startsWith( string:String, pattern:String):Boolean {
		string  = string.toLowerCase();
		pattern = pattern.toLowerCase();
		return pattern == string.substr( 0, pattern.length );
	}


    /**
     * Takes a string and coverts it to upper case handling null.
     *
     * @param str to upcase
     * @return the upcased string
     */
    public static function toUpperCase(str:String):String
    {
        return str == null ? null : str.toUpperCase();
    }

    /**
     * Takes a string and coverts it to lower case handling null.
     *
     * @param str to lower
     * @return the lower case string
     */
    public static function toLowerCase(str:String):String
    {
        return str == null ? null : str.toLocaleLowerCase();
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * empty or <code>null</code>, the value of <code>defaultStr</code>.</p>
     *
     * <pre>
     * StringUtil.defaultIfEmpty(null, "NULL")  = "NULL"
     * StringUtil.defaultIfEmpty("", "NULL")    = "NULL"
     * StringUtil.defaultIfEmpty("bat", "NULL") = "bat"
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is empty ("") or <code>null</code>, may be null
     * @return the passed in String, or the default
     */
    public static function defaultIfEmpty(str:String, defaultStr:String):String
    {
        return isEmpty(str) ? defaultStr : str;
    }

    public static function pad(width:int, str:String, character:String = ' '):String {
    	if (character == null || character.length != 1) throw new ArgumentError("pad character must be length 1");
    	if (width < 0) throw new ArgumentError("pad width must be greater than or equal to 0");
    	var result:String = "";
    	if (width == 0) return result;
    	if (str == null) {
    		for (var j:int = 0; j < width; j++) {
    			result += character;
    		}
    		return result;
    	}
    	if (str.length > width) {
    		return str.substr(0, width);
    	}
    	var padWidth:int = width - str.length;
    	for (var i:int = 0; i < padWidth; i++) {
    		result += character;
    	}
    	result += str;
    	return result;
    }

    /**
     * Substitutes "{n}" tokens within the specified string with the respective arguments passed in.
     *
     * @param str The string to make substitutions in.
     * @param parameters Additional parameters that can be substituted in the str parameter at each {n} location, where n is an integer (zero based) index value into the array of values specified.
     * @return New string with all of the {n} tokens replaced with the respective arguments specified.
     * @see mx.utils.StringUtil.substitute
     */
    public static function substitute(str:String, ...parameters):String {
    	var args:Array = [str];
    	for each (var param:* in parameters) {
    		args.push(param);
    	}
    	var f:Function = mx.utils.StringUtil.substitute;
    	return f.apply(NaN, args);
    }
}
}
