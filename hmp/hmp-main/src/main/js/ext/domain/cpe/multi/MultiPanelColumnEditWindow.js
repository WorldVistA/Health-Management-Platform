Ext.define('ViewDefColSpec', {
	extend: 'Ext.data.Model',
	fields: [
         {name: 'name', type: 'string'},
         {name: 'code', type: 'string'}
	]
});


Ext.define('org.osehra.cpe.multi.MultiPanelColumnEditWindow', {
	extend: 'Ext.window.Window',
	title: 'Add / Edit Column',
	id: 'mpeColEditWnd',
	alias: 'widget.panelColumnEditWindow',
	requires: ['org.osehra.cpe.designer.PanelEditor'],
	width: 500,
	height: 400,
	title: 'Add Column',
	layout: {type: 'hbox', align: 'stretch'},
	items: [{
		xtype: 'grid',
		padding: '20 20 20 20',
		id: 'mpeColEditWndViewDefGrid',
		store: {
			fields: ['code','name'],
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/app/list',
				reader: {
					type: 'json',
					root: 'items'
				},
				extraParams: {
					type: 'org.osehra.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef'
				}
			}
		},
		flex: 1,
//		listeners: {
//			selectionchange: {
//				fn: function(selMdl, selData, opts) {
//					var wnd = Ext.ComponentQuery.query('#mpeColEditWnd')[0];
//					var fldGrid = wnd.down('#mpeColEditWndFldGrid');
//					fldGrid.store.proxy.extraParams = {view: selData[0].get('code')};
//					fldGrid.store.load();
//				}
//			}
//		},
		columns: [
		          {text: 'Name', dataIndex: 'name'},
		          {text: 'Code', dataIndex: 'code'}
		]
	}],
//	,{
//		xtype: 'grid',
//		padding: '20 20 20 20',
//		id: 'mpeColEditWndFldGrid',
//		flex: 1,
//		store: {
//			model: 'ViewDefColSpec',
//			proxy: {
//				type: 'ajax',
//				url: '/view/rollupList',
//				reader: {
//					type: 'json'
//				}
//			}
//		},
//		columns: [
//	          {text: 'Name', dataIndex: 'fieldName'},
//	          {text: 'Data Field', dataIndex: 'fieldDataIndex'}
//		],
//		multiSelect: true
//	}],
	bbar: {
		items: [{
			xtype: 'button',
			text: 'Save',
			handler: function(button, evt) {
				var destGrid = Ext.ComponentQuery.query('#mpColGrid')[0];
				var panelGrid = Ext.ComponentQuery.query('#mpeGrid')[0];
				var selDefGrid = Ext.ComponentQuery.query('#mpeColEditWndViewDefGrid')[0];
				var selDef = selDefGrid.getSelectionModel().getSelection();
				var destPanelName = panelGrid.getSelectionModel().getSelection()[0].get('name');
				if(selDef && selDef.length>0) {
					var payload = [];
					for(var i = 0; i<selDef.length; i++) {
						payload.push(Ext.encode(selDef[i].data));
					}
					Ext.Ajax.request({
						panelName: destPanelName,
						url: 'config/addColumn',
						params: {
							'panelName': destPanelName,
							'columns': payload
						},
						success: function(response, opts) {
							var colGridPanel = Ext.ComponentQuery.query('#mpeColPanelEditor')[0];
							colGridPanel.setPanelName(opts.panelName);
							Ext.ComponentQuery.query('#mpeColEditWnd')[0].close();
						},
						failure: function(response, opts) {
							Ext.MessageBox.alert('Error Adding Column(s)',response);
						}
					});
				}
			}
		}]
	}
});
