Ext.define('EXT.DOMAIN.cpe.multi.ConfigPanelMain', {
	extend: 'Ext.panel.Panel',
	title: 'Column Selection',
	padding: '20 20 20 20',
	requires: 'EXT.DOMAIN.cpe.multi.MultiPatientPanelEditor',
	id: 'cpMain',
	alias: 'widget.configpanel',
	layout: {type: 'hbox', align: 'stretch'},
	items: [{
		xtype: 'panel',
		flex: 1,
		items: [{
			xtype: 'button',
            ui:'theme-colored',
        	padding: '20 20 20 20',
			text: 'Add/Edit Multi-Patient Panels',
			handler: function(button, e){
				var workPanel = button.up('#cpMain').down('#ConfigWorkArea');
				workPanel.removeAll();
				var pe = {xtype: 'panelEditor'};
				var pepnl = workPanel.add(pe);
			}
		},{
			xtype: 'button',
            ui:'theme-colored',
        	padding: '20 20 20 20',
			text: 'Add/Edit Rooms',
			handler: function(button, e){
				// TODO
			}
		},{
			xtype: 'button',
            ui:'theme-colored',
        	padding: '20 20 20 20',
			text: 'Add/Edit Column Specs',
			handler: function(button, e){
				// TODO
			}
		}]
	},{
		xtype: 'panel',
		flex: 10,
		id: 'ConfigWorkArea',
		layout: {type: 'fit', align: 'stretch'}
	}]
});
