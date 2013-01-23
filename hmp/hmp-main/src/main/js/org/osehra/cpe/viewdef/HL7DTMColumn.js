Ext.define('org.osehra.cpe.viewdef.HL7DTMColumn', {
	requires: ['org.osehra.hmp.util.HL7DTMFormatter'],
	extend : 'Ext.grid.column.Column',
	alias : [ 'widget.hl7dtmcolumn' ],

	/**
	 * {String} format A formatting string as used by to
	 * format a HL7Date passed as a String  for this Column. This defaults to the default date from
	 * {@link Ext.Date#defaultFormat} which itself my be overridden in a locale
	 * file.
	 */

	initComponent : function() {
		var me = this;
		me.callParent(arguments);

		// 
		/**
		 * Format partial HL7 date and time to human readable string.
		 * The hl7 date stored using the following format YYYY[MM[DD[HHMM[SS[.S[S[S[S]]]]]]]][+/-ZZZZ]
		 * ex:19750723152342.398-07 or 1975
		 */
		me.renderer = function(value) {
			return org.osehra.hmp.util.HL7DTMFormatter.format(value);
		}
	}
});
