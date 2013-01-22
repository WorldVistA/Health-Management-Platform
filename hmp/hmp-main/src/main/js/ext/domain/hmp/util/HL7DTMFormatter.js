Ext.define('EXT.DOMAIN.hmp.util.HL7DTMFormatter', {
	
statics: {
	/**
	 * Format partial HL7 date and time to human readable string. The hl7 date
	 * stored using the following format
	 * YYYY[MM[DD[HHMM[SS[.S[S[S[S]]]]]]]][+/-ZZZZ] ex:19750723152342.398-07 or
	 * 1975
	 * JC - 2012-08-09 17:23 - In case the user wants to specify their own format, let's allow reqFmt as an optional argument.
	 */
	format: function(value, reqFmt){
	    var FRMT_FULL = "{0}-{1}-{2}&nbsp;{3}:{4}";// "yyyy-MM-dd HH:mm"
	    var FRMT_DAY = "{0}-{1}-{2}";// "yyyy-MM-dd"
	    var FRMT_MNT = "{0}-{1}";// "yyyy-MM"
	    var FRMT_YR = "{0}";// "yyyy"
	    
	    if(!value){return "";}
	    
		// Split dateTime string to date/time token, milliseconds and zone
		var parts = value.toString().split(/[.\+\-]/);
		
		// Extract all fields based on position
		var year = parts[0].slice(0, 4);
		var month = parts[0].slice(4, 6);
		var day = parts[0].slice(6, 8);
		var hour = parts[0].slice(8, 10);
		var min = parts[0].slice(10, 12);
		var sec = parts[0].slice(12, 14);
		
		var mlsec = (value.search(/\./))?mlsec = parts[1]:"";
		var zone = (value.search(/[+\-]/))?mlsec = parts[2]:"";
			
		var fmt
		if(reqFmt)
		{
			fmt = reqFmt;
		}else if(mlsec||sec||min)
		{
			fmt = FRMT_FULL;
		}else if(hour||day)
		{
			fmt = FRMT_DAY;
		}else if(month)
		{
			fmt = FRMT_MNT;
		}
		else
		{
			fmt = FRMT_YR;
		}

		return Ext.String.format(fmt,year,month,day,hour,min);
	},
	UTC: function(value) {
	    
	    if(!value){return "";}
	    
		// Split dateTime string to date/time token, milliseconds and zone
		var parts = value.toString().split(/[.\+\-]/);
		
		// Extract all fields based on position
		var year = parts[0].slice(0, 4);
		var month = new Number(parts[0].slice(4, 6))-1; // UTC wants 0-based months instead of 1-based months.
		var day = parts[0].slice(6, 8);
		var hour = parts[0].slice(8, 10);
		var min = parts[0].slice(10, 12);
		var sec = parts[0].slice(12, 14);
		
		var mlsec = (value.search(/\./))?mlsec = parts[1]:"";
		var zone = (value.search(/[+\-]/))?mlsec = parts[2]:"";

		return Date.UTC(year,month,day,hour,min,sec);
	}
  }
});
