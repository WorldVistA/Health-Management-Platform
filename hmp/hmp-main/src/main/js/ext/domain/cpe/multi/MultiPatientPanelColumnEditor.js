Ext.define('MultiPatientPanelEditorColumnModel', {
	extend: 'Ext.data.Model',
	fields: [
	         {name: 'name', type: 'string'},
	         {name: 'fieldName', type: 'string'},
	         {name: 'code', type: 'string'},
	         {name: 'appInfo'},
	         {name: 'configOptions'},
	         {name: 'configProperties'},
	         {name: 'viewdefFilters'},
	         {name: 'sequence'}
	]
});

Ext.define('org.osehra.cpe.multi.MultiPatientPanelColumnEditor', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.panelColumnEditor',
	requires: ['MultiPatientPanelEditorColumnModel','org.osehra.cpe.multi.MultiPanelColumnEditWindow','org.osehra.cpe.multi.MultiPatientPanelColumnOptionsEditor'],
	layout: {type: 'hbox', align: 'stretch'},
	id: 'mpeColPanelEditor',
	items: [{
		xtype: 'grid',
		id: 'mpColGrid',
		flex: 1,
		width: 200,
		store: {
			model: 'MultiPatientPanelEditorColumnModel',
			proxy: {
				type: 'ajax',
				url: '/config/panelColumns',
				reader: {
					type: 'json'
				},
				extraParams: {
					panelName: ''
				}
			}
		},
		columns: [
		    {text: 'Column Title', dataIndex: 'fieldName'}
		],
		viewConfig: {
			plugins: ['gridviewdragdrop'],
			listeners: {
				drop: function(node, data, overModel, dropPosition, eOpts) {
					console.log(data);
					var grid = Ext.ComponentQuery.query('#mpColGrid')[0];
					var store = grid.getStore();
					var sequence = [];
					var seq = 1;
					var panelGrid = Ext.ComponentQuery.query('#mpeGrid')[0];
					var destPanelName = panelGrid.getSelectionModel().getSelection()[0].get('name');
					
					for(key in store.data.items) {
						var rec = store.data.items[key];
						rec.set('sequence', seq);
						sequence.push(Ext.encode({'fieldName':rec.get('fieldName'),'sequence':seq++}));
					}
					var parms = {'sequence':sequence,'panelName':destPanelName};
					Ext.Ajax.request({
						url: 'config/setViewDefColumnSequence',
						method:'POST',
						params: parms,
						success: function(response) {
							console.log(response);
						},
						failure: function(response) {
							console.log(response);
						}
					})
				}
			}
		},
		listeners: {
			selectionchange:{
				fn: function(selMdl, selData, eOpts) {
					if(selData.length>0) {
						var cd = selData[0].data.appInfo.code;
						if(cd) {
					    	Ext.Ajax.request({
								url: '/config/getColumnConfigOptions',
								method: 'GET',
								params: {code: cd},
								success: function(response, opts) {
									var optPnl = this.up('#mpeColPanelEditor').down('#mpecoloptionpanel');
									var panelGrid = Ext.ComponentQuery.query('#mpeGrid')[0];
									var destPanelName = panelGrid.getSelectionModel().getSelection()[0].get('name');
									optPnl.setConfigOptions(Ext.decode(response.responseText));
									optPnl.setConfigData(selData[0], destPanelName);
									optPnl.show();
								},
								failure: function(response, opts) {
									console.log(response);
								},
								scope: this
							});
						}
					}
				}
			}
		}
	},{
		xtype: 'mpecoloptions',
		id: 'mpecoloptionpanel',
		flex: 2,
		hidden:true
	}],
	setPanelName: function(panelName) {
		this.panelName = panelName;
		this.down('#mpColGrid').store.proxy.extraParams = {'panelName': panelName};
		this.down('#mpColGrid').store.load();
	},
	tbar: {
		items: [{
			xtype: 'button',
            ui:'link',
        	padding: '5 5 5 5',
			text: 'Add Column',
			handler: function(bn, e) {
				var colEdit = Ext.ComponentQuery.query('#mpeColEditWnd');
				if(colEdit==null || colEdit.length==0) {
					colEdit = Ext.create('widget.panelColumnEditWindow');
				}
				colEdit.show();
				colEdit.center();
			}
		},{
			xtype: 'button',
            ui:'link',
        	padding: '5 5 5 5',
			text: 'Remove Column',
			handler: function(bn, e) {
				var grid = this.up('panel').down('grid');
				var sel = grid.getSelectionModel().getSelection();
				if(sel!=null && sel.length > 0) {
					var col = sel[0];
					var colName = col.get('name');

					var panelGrid = Ext.ComponentQuery.query('#mpeGrid')[0];
					var panelName = panelGrid.getSelectionModel().getSelection()[0].get('name');
					
					if(colName!=null && panelName!=null) {
				    	Ext.Ajax.request({
							url: '/config/dropColumn',
							method: 'POST',
							params: {'colName': colName, 'panelName': panelName},
							success: function(response, opts) {
								var optPnl = this.up('panel').down('#mpecoloptionpanel');
								optPnl.removeAll();
								grid.getStore().load();
							},
							failure: function(response, opts) {
								console.log(response);
							},
							scope: this
						});
					}
				}
			}
		}]
	}
});
