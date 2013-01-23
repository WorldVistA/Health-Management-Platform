Ext.define('org.osehra.cpe.multi.MultiPatientPanelColumnOptionsEditor', {
	alias: 'widget.mpecoloptions',
	extend: 'Ext.form.Panel',
	defaults: {
		width: 500,
		listeners: {
			blur: function(field, evt, eopts) {
				var form = field.up('form');
				form.saveColData();
			},
			change: function(field, evt, eopts) {
				var form = field.up('form');
				form.saveColData();
			}
		}
	},
	
	dereferenceDotNotation: function(data) {
		for(key in data) {
			var d = key.indexOf('.');
			if(d>0) {
				var firstPart = key.substring(0,d);
				var secondPart = key.substring(d+1);
				data[firstPart] = [];
				data[firstPart][secondPart] = data[key];
			}
		}
		return data;
	},
	
	saveColData: function() {
		if(!this.progLoad) {
			var frm = this.getForm();//bn.up('form').getForm();
			var vals = this.dereferenceDotNotation(frm.getValues());
			var parms = Ext.apply(vals, {'panelName': frm.destPanelName, 'colName': frm.destColName, 'sequence':frm.sequence});
			var grid = Ext.ComponentQuery.query('#mpeColPanelEditor')[0].down("#mpColGrid");
			var sel = grid.getSelectionModel().getSelection();
			var seldex = -1; 
			if(sel && sel.length>0) {
				seldex = grid.getStore().indexOf(sel[0]);
			}
			Ext.Ajax.request({
				url: 'config/setViewDefColumnProperties',
				params: parms,
				success: function(response, opts) {
					var grid = Ext.ComponentQuery.query('#mpeColPanelEditor')[0].down("#mpColGrid")
					var rec = grid.getSelectionModel().getSelection()[0];
					Ext.apply(rec.data, vals);
//					rec.set('fieldName', vals.fieldName); // Lame; It would be nice if we could have a general refresh for the grid.
//					var str = grid.store;
//		            rec.modsave = true;
//		            str.fireEvent('datachanged', null); // cuz this doesn't cut it - does not cause the grid to refresh.
				},
				failure: function(form, action) {
					Ext.MessageBox.alert('Failure','Houston, we have a problem.');
				},
				seldex: seldex
			});
		}
	},
	
	/**
	 * Set options for column-specific fields and also viewdef filters.
	 */
	setConfigOptions: function(configOptions) {
		this.removeAll();
		this.add(org.osehra.cpe.multi.MultiPatientPanelColumnOptionsEditor.buildConfigOptions(configOptions));
		this.doLayout();
	},
	
	/**
	 * Set the selected model record on this form to fill field values.
	 */
	setConfigData: function(configData, panelName) {
		
		// Temp hack.
		for(key in configData.data) {
			if(key=="configProperties" || key=="viewdefFilters") {
				for(k2 in configData.data[key]) {
					configData.data[key+"."+k2] = configData.data[key][k2];
				}
			}
		}
		this.progLoad = true;
		var frm = this.getForm();
		
		frm.loadRecord(configData);
		this.progLoad = false;
		frm.destPanelName = panelName;
		frm.destColName = configData.data.name;
		frm.sequence = configData.data.sequence;
		console.log('vals: '+frm.destPanelName+", "+this.getForm().destColName);
		var boxen = Ext.ComponentQuery.query('checkbox');
		for(key in boxen) {
			// <hack=to load checkboxen because checkbox groups do not load their data correctly on form.load() reliably>
			var box = boxen[key];
			var nm = box.name;
			if(configData.data[nm]!=null) {
				if(configData.data[nm]==box.inputValue || configData.data[nm].length>0 && configData.data[nm].indexOf(box.inputValue)>-1) {
					box.setValue(true);
				}
			}
			// </hack>
			boxen[key].on('check', function(field) {
				var form = field.up('form');
				form.saveColData();
			})
		}
	},
	
	statics: {
		buildConfigOptions: function(configOptions) {
			var fitems = [];
			fitems.push({
				xtype: 'panel',
				margin: '10 10 10 0',
				border: 1,
				html: '<b>Description:</b><br>'+configOptions.description+"<br>"
			})
			fitems.push({
				xtype: 'textfield',
				name: 'fieldName',
				fieldLabel: 'Column Title'
			});
			if(configOptions.viewdefFilterOptions) {
//				var fitems = [];
				for(key in configOptions.viewdefFilterOptions) {
					var opt = configOptions.viewdefFilterOptions[key];
					// TODO: Switch based on data types / etc.
					var fld = {};
					var options = opt.choiceList;
					
					fld['fieldLabel'] = opt.label;
					
					if(options) {
						if(opt.dataType=='LIST') {
							var boxen = [];
							for(o in options) {
								boxen.push({boxLabel: options[o], name: 'viewdefFilters.'+opt.name, inputValue: options[o]});
							}
							fld['xtype'] = 'checkboxgroup';
							fld['columns'] = 2;
							fld['vertical'] = true;
							fld['items'] = boxen;
						}
						else {
							fld['name'] = 'viewdefFilters.'+opt.name;
							var strDat = [];
							for(o in options) {
								strDat.push({choice: options[o]});
							}
							fld['xtype'] = 'combobox';
						    fld['store'] = {
						    	fields: ['choice'],
						        data : strDat
						    };
						    fld['queryMode'] = 'local';
						    fld['displayField'] = 'choice';
						    fld['valueField'] = 'choice';
						    cls = 'Ext.form.ComboBox';
						}
					} else {
						fld['name'] = 'viewdefFilters.'+opt.name;
						fld['xtype'] = 'textfield';
					}
					
					if(opt.dataType=="BOOLEAN") {
						fld['xtype'] = 'checkbox';
						fld['boxLabel'] = fld['fieldLabel'];
						fld['fieldLabel'] = '';
						fld['inputValue'] = 'true';
					} else if(opt.dataType=="MAP") {
						// TODO
					} else if(opt.dataType=="LIST") {
						// TODO
					} else if(opt.dataType=="RANGE") {
						// TODO
					} else {
						// TODO
					}
					fitems.push(fld);
				}
			}
			
			if(configOptions.configOptions) {
				for(key in configOptions.configOptions) {
					var opt = configOptions.configOptions[key];
					// TODO: Switch based on data types / etc.
					var fld = {};
					var options = opt.choiceList;
					if(options) {
						if(opt.dataType=='LIST') {
							var boxen = [];
							for(o in options) {
								boxen.push({boxLabel: options[o], name: 'viewdefFilters.'+opt.name, inputValue: options[o]});
							}
							fld['xtype'] = 'checkboxgroup';
							fld['columns'] = 2;
							fld['vertical'] = true;
							fld['items'] = boxen;
						}
						else {
							fld['name'] = 'viewdefFilters.'+opt.name;
							var strDat = [];
							for(o in options) {
								strDat.push({choice: options[o]});
							}
							fld['xtype'] = 'combobox';
						    fld['store'] = {
						    	fields: ['choice'],
						        data : strDat
						    };
						    fld['queryMode'] = 'local';
						    fld['displayField'] = 'choice';
						    fld['valueField'] = 'choice';
						    cls = 'Ext.form.ComboBox';
						}
					} else {
						fld['xtype'] = 'textfield';
					}
					fld['fieldLabel'] = opt.label;
					fld['name'] = 'configProperties.'+opt.name;
					
					if(opt.dataType=="BOOLEAN") {
						fld['xtype'] = 'checkbox';
						fld['boxLabel'] = fld['fieldLabel'];
						fld['fieldLabel'] = '';
						fld['inputValue'] = 'true';
					} else if(opt.dataType=="MAP") {
						// TODO
					} else if(opt.dataType=="LIST") {
						// TODO
					} else if(opt.dataType=="RANGE") {
						// TODO
					} else {
						// TODO
					}
					fitems.push(fld);
				}
			}
			return fitems;
		}
	}
});
