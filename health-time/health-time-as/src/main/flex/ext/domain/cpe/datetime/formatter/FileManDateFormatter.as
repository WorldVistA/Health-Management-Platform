package EXT.DOMAIN.cpe.datetime.formatter {
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.util.StringUtil;

import mx.formatters.Formatter;

/**
	 * Class for parsing and formatting VA FileMan Dates and Date/Times.
	 * <p/>
	 * FileMan stores dates and date/times of the form "YYYMMDD.HHMMSS", where: <ul> <li>YYY is number of years since 1700
	 * (hence always 3 digits)</li> <li>MM is month number (00-12)</li> <li>DD is day number (00-31)</li> <li>HH is hour
	 * number (00-23)</li> <li>MM is minute number (01-59)</li> <li>SS is the seconds number (01-59)</li> </ul> <p>This
	 * format allows for representation of imprecise dates like JULY '78 or 1978 (which would be equivalent to 2780700 and
	 * 2780000, respectively). Dates are always returned as a canonic number (no trailing zeroes after the decimal)</p>
	 */
	public class FileManDateFormatter extends Formatter {

		private static const MIN_FILE_MAN_DATE_LENGTH:int = 7;
		private static const MAX_FILE_MAN_DATE_LENGTH:int = 14;
		private static const YEARS_PER_CENTURY:int = 100;
		private static const BASE_CENTURY:int = 17;

		private static var _instance:Formatter;

		public static function get instance():Formatter {
			return getInstance();
		}

		public static function getInstance():Formatter {
			if (_instance == null) {
				_instance = new FileManDateFormatter();
			}
			return _instance;
		}

		public override function format(o:Object):String {
			if (o == null)
				return "";
			if (o is Date) {
				return formatDate(o as Date);
			} else if (o is PointInTime) {
				return formatPointInTime(o as PointInTime);
			} else {
				throw new ArgumentError("unable to format object");
			}
		}

		private function formatDate(t:Date):String {
			var s:String = String(getThreeDigitYear(t.fullYear));
			s += DateFormatUtils.formatTwoDigits(t.month + 1);
			s += DateFormatUtils.formatTwoDigits(t.date);
			s += ".";
			s += DateFormatUtils.formatTwoDigits(t.hours);
			s += DateFormatUtils.formatTwoDigits(t.minutes);
			s += DateFormatUtils.formatTwoDigits(t.seconds);
			return s;
		}

		private function formatPointInTime(t:PointInTime):String {
			var s:String = String(getThreeDigitYear(t.year));
			if (t.isMonthSet()) {
				s += DateFormatUtils.formatTwoDigits(t.month);
			} else {
				s += "00";
			}
			if (t.isDateSet()) {
				s += DateFormatUtils.formatTwoDigits(t.date);
			} else {
				s += "00";
			}
			if (t.isHourSet()) {
				s += "." + DateFormatUtils.formatTwoDigits(t.hour);
			}
			if (t.isMinuteSet()) {
				s += DateFormatUtils.formatTwoDigits(t.minute);
			}
			if (t.isSecondSet()) {
				s += DateFormatUtils.formatTwoDigits(t.second);
			}
			return s;
		}

		private function getThreeDigitYear(year:int):int {
			if (year < 0) {
				year = -year;
			}
			var century:int = year / YEARS_PER_CENTURY;
			return (century - BASE_CENTURY) * YEARS_PER_CENTURY + year % YEARS_PER_CENTURY;
		}

		public static function parseDate(text:String):Date {
			if (StringUtil.isBlank(text))
				return null;
			if (text.length < MIN_FILE_MAN_DATE_LENGTH + 2 || text.length > MAX_FILE_MAN_DATE_LENGTH)
				throw new ArgumentError(StringUtil.substitute("FileMan date times must be between {0} and {1} characters in length to parse into a Date, '{2}' is {3} characters in length.", (MIN_FILE_MAN_DATE_LENGTH + 2), MAX_FILE_MAN_DATE_LENGTH, text, text.length));
			while (text.length < 14) {
				text = text + "0"; // add trailing zero(s) to make seconds substring easier to work with
			}

			var year:int = parseFullYearFromThreeDigitYear(text.substring(0, 3));

			var month:int = int(text.substring(3, 5));
			if (isNaN(month) || !isBetween(month, 1, 12))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time month '{0}' in '{1}'", month, text));

			var date:int = int(text.substring(5, 7));
			if (isNaN(date) || !isBetween(date, 1, 31))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time date '{0}' in '{1}'", date, text));

			if (text.charAt(7) != ".")
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time string: '{0}'", text));

			var hours:int = int(text.substring(8, 10));
			if (isNaN(hours) || !isBetween(hours, 0, 24))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time hours '{0}' in '{1}'", hours, text));

			var minutes:int = int(text.substring(10, 12));
			if (isNaN(minutes) || !isBetween(minutes, 0, 59))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time minutes '{0}' in '{1}'", minutes, text));

			var seconds:int = int(text.substring(12, 14));
			if (isNaN(seconds) || !isBetween(seconds, 0, 59))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time seconds '{0}' in '{1}'", seconds, text));

			if (hours == 24 && minutes == 0 && seconds == 0) {
				hours = 23;
				minutes = 59;
				seconds = 59;
			}
			return new Date(year, month - 1, date, hours, minutes, seconds);
		}

		private static function isBetween(n:int, min:int, max:int):Boolean {
			return n >= min && n <= max;
		}

		private static function parseFullYearFromThreeDigitYear(text:String):int {
			var year:int = int(text);
			if (isNaN(year))
				throw new ArgumentError("Invalid FileMan date time year string: " + text);
			return year + (BASE_CENTURY * YEARS_PER_CENTURY);
		}

		public static function parsePointInTime(text:String):PointInTime {
			if (StringUtil.isBlank(text))
				return null;
			if (text.length < MIN_FILE_MAN_DATE_LENGTH || text.length > MAX_FILE_MAN_DATE_LENGTH)
				throw new ArgumentError(StringUtil.substitute("FileMan date times must be between '{0}' and '{1}' characters in length; '{2}' was '{3}' characters in length.", MIN_FILE_MAN_DATE_LENGTH, MAX_FILE_MAN_DATE_LENGTH, text, text.length));
			if (text.length == 8)
				throw new ArgumentError(StringUtil.substitute("FileMan date times cannot be 8 characters long; '{0}' was {1} characters in length.", text, text.length));

			var year:int = parseFullYearFromThreeDigitYear(text.substring(0, 3));

			var month:int = int(text.substring(3, 5));
			if (isNaN(month) || !isBetween(month, 0, 12))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time month '{0}' in '{1}'", month, text));

			var date:int = int(text.substring(5, 7));
			if (isNaN(date) || !isBetween(date, 0, 31))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time date '{0}' in '{1}'", month, text));

			if (text.length == MIN_FILE_MAN_DATE_LENGTH)
				return new PointInTime(year, month == 0 ? -1 : month, date == 0 ? -1 : date);

			if (text.charAt(7) != ".")
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time string '{}'", text));

			if (text.length >= 9) {
				while (text.length < 14) {
					text = text + "0"; // add trailing zero(s) to make seconds substring easier to work with
				}
			}
			var hours:int = int(text.substring(8, 10));
			if (isNaN(hours) || !isBetween(hours, 0, 24))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time hours '{0}' in '{1}'", hours, text));

			var minutes:int = int(text.substring(10, 12));
			if (isNaN(minutes) || !isBetween(minutes, 0, 59))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time minutes '{0}' in '{1}'", minutes, text));

			var seconds:int = int(text.substring(12, 14));
			if (isNaN(seconds) || !isBetween(seconds, 0, 59))
				throw new ArgumentError(StringUtil.substitute("Invalid FileMan date time seconds '{0}' in '{1}'", seconds, text));

			if (hours == 24 && minutes == 0 && seconds == 0) {
				hours = 23;
				minutes = 59;
				seconds = 59;
			}
			return new PointInTime(year, month, date, hours, minutes, seconds);
		}
	}
}
