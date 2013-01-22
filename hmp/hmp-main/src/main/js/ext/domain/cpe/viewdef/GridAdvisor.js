/*
 * The idea here is that the actual grid columns (not data field) would be defined by the Advisor,
 * which would eliminate the field/column confusion in the ViewDef.
 * 
 * Could also have many other responsibilities:
 * - Helping the editor out with rendering custom fields.
 * - Custom detail formats/widgets/etc.
 */
Ext.define('EXT.DOMAIN.cpe.viewdef.GridAdvisor', {
    requires: [
        'EXT.DOMAIN.hmp.UserContext',
        'Ext.ux.CheckColumn',
        'EXT.DOMAIN.cpe.viewdef.GridEditors'
    ],
	grid: null, // set at construction time.
	columnsConf: null, // This will be populated by any saved column specifications the user may have provided.
	gridColMetaData: null,
	me: null,
	defineColumns: function(grid, metadata) {
		// TODO: Use this to populate the editors sort/group combobox's, detail title fields, etc.
		this.gridColMetaData = metadata;
		this.me = this;
		var cols = [];

		if (Ext.isArray(metadata.columns)) {
			if(this.columnsConf && this.columnsConf.length>0)
			{
				for(key in this.columnsConf)
				{
					var col = this.columnsConf[key];
					var found = false;
					for(var n = 0; n<metadata.columns.length && !found; n++)
					{
						var mcol = metadata.columns[n];
						if(mcol.text == col.text)
						{
							cols.push(mcol);
							mcol.width = col.width;
							mcol.hidden = col.hidden;
							found = true;
							metadata.columns.splice(n, 1);
						}
					}
				}
			}
			else
			{
				for(var n = 0; n<metadata.columns.length; n++)
				{
					var mcol = metadata.columns[n];
					if(mcol.deferred) {
						// Need special renderer for this column. Build config here.
						Ext.apply(mcol, {
							renderer: this.deferredColumnLoader,
							stopSelection: true
						});
					} else {
//						Ext.apply(mcol, {
//							renderer: this.userBoldRenderer
//						});
					}
				}
			}
			for(var n = 0; n<metadata.columns.length; n++)
			{
				if(metadata.columns[n].editOpt!=null) {
					EXT.DOMAIN.cpe.viewdef.GridEditors.applyEditOpt(grid, metadata.columns[n], metadata.columns[n].editOpt);
				}
				cols.push(metadata.columns[n]);
			}
		}

		if (Ext.isArray(grid.extraColumns)) {
			cols.push(grid.extraColumns);
		}

		/*
		 * If the grid is defined with a "maxWidth" field, this snippet will chop off any columns that exceed the col's maxWidth.
		 * This should come after columns have been intelligently sized by their data content (?)
		 * Actually this code comes long before we have data to show, right?
		 */
		var maxWidth = grid.maxWidth;
		if(maxWidth)
		{
            if(maxWidth==='auto') {maxWidth = grid.width;}
			var width = 0;
			for(var i = 0; i<cols.length; i++)
			{
				if(cols[i].width+width<=maxWidth)
				{
					width = width + cols[i].width;
				}
				else
				{
					cols.splice(i--, 1);
				}
			}
		}

		return cols;
	},

	unreadBoldRenderer: function(value, metaData, record)
	{
		var read = record.data['wasViewed'];
		if(read != null && read == false)
		{
			value = "<b>"+value+"</b>";
		}
		return value;
	},

	userBoldRenderer: function(value, metaData, record)
	{
		var usr = EXT.DOMAIN.hmp.UserContext.getUserInfo().displayName;
		for(key in record.data)
		{
			if(record.data[key]==usr)
			{
				value = "<b>"+value+"</b>";
			}
		}
		return value;
	},

	deferredColumnLoader: function(value, meta, record, rowIndex, colIndex, store, view) {
		var def = this.gridAdvisor.gridColMetaData.columns[colIndex].deferred;
		var dataIndex = this.gridAdvisor.gridColMetaData.columns[colIndex].dataIndex;
		var me = this.gridAdvisor;
		var mode = 'JSON';
		if(def.summaryType=='GSP') {def.mode=def.fieldDataIndex;}
		if(def && !value) {
			if(record.get('pid')>0) {
				var kv = record.get(def.keyCol);
				var fieldName = this.gridAdvisor.gridColMetaData.columns[colIndex].dataIndex;
				var params = {
						view: def.viewdefCode,
						mode: (def.mode || 'JSON'),
						keyVal: kv,
						pid: record.get('pid'),
						board: me.grid.curViewID,
						'row.count': 1000
					};
				if(def.appInfo && def.appInfo.code) {params['code'] = def.appInfo.code;}
				Ext.apply(params, def.viewdefFilters || {});
				Ext.apply(params, def.configProperties || {});
				if(kv && fieldName) {
					Ext.Ajax.request({
						url: '/vpr/col/render',
						def: def,
						record: record,
						field: fieldName,
						params: params,
						method: 'GET',
						success: function(response, opts) {
							me.processDeferredColumnResponse(opts.record, opts.field, opts.def, response);
							//record.set(fieldName, response.responseText);
						},
						failure: function(response, opts) {
							console.log("FAIL: "+response);
						}
					})
				}
				return "<i>Loading...</i>"
			} else {
				return "<span class=\"hmp-pt-not-loaded\" title=\"Patient not in VPR\">N/A</span>";
			}
		}
		else
		{
			if(def.summaryType=='GSP') {
				return value;
			} else if(def.summaryType=='JSON') {
				if(record.srcJson && record.srcJson[dataIndex] && record.srcJson[dataIndex].data && record.srcJson[dataIndex].data.length>0) {

						var rtrn = record.srcJson[dataIndex].data[0][def.fieldDataIndex];
						// TODO: Difference between multi-value non-edible and single-value edible data.
//						if(record.srcJson.data.length>1) {
//							rtrn = rtrn + ' (' + (record.srcJson.data.length-1) + ' more)';
//						}
						return rtrn;
				}
			}
		}
	},

	processDeferredColumnResponse: function(record, field, deferredProps, response) {
		var rslt = '';
		var srcJson = null;
		if(deferredProps.summaryType == 'LIST' || deferredProps.summaryType == 'CSV') {
			var responseJSON = Ext.decode(response.responseText);
			for(var rowIdx in responseJSON.data) {
				var row = responseJSON.data[rowIdx];
				if(row[deferredProps.fieldDataIndex]) {
					rslt = rslt + row[deferredProps.fieldDataIndex];
					if(deferredProps.summaryType=='LIST'){rslt = rslt + '<br>';} else {rslt = rslt + ', ';};
				}
			}
		} else if(deferredProps.summaryType == 'TOTAL') {
			// TODO
			rslt = 'TOTAL not yet implemented';
		} else if(deferredProps.summaryType == 'AVG') {
			// TODO
			rslt = 'AVG not yet implemented';
		} else if(deferredProps.summaryType == 'JSON') { // Assume it is GSP or HTML or simple text.
			srcJson = Ext.decode(response.responseText);
			if(srcJson && srcJson.data && srcJson.data.length>0) {
				rslt = srcJson.data[0][deferredProps.fieldDataIndex];
			}
		} else {
			rslt = response.responseText;
		}
		//if(!rslt || rslt == '') {rslt = '<font color="RED">NO DATA</font>';}
		if(srcJson) {
			if(!record.srcJson) {
				record.srcJson = {};
			}
			// Trying to stuff it in somewhere;
			record.srcJson[field] = srcJson;
		}
		record.set(field, rslt);
	},

	getToolbars: function() {
		// return a list of available toolbars,
		// user config preferences will put them on the top/bottom/left/right/etc.
		// this function would probably be mostly used by the editor.
	}
});
