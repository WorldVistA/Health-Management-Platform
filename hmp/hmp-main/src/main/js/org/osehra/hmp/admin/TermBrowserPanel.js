
var termSearchStore = Ext.create('Ext.data.Store', {
	fields: ['urn', 'description'],
	proxy: {
		type: 'ajax',
		url: '/term/search',
		reader: {
			type: 'json',
			root: 'data.items'
		}
	}
});

Ext.define('org.osehra.hmp.admin.TermBrowserPanel', {
	extend : 'Ext.panel.Panel',
	requires : [ 'org.osehra.hmp.admin.TermBrowserTree' ],
	itemId : 'term-browse',
	title : 'Terminology Browser',
	layout : 'border',
	items : [
	// top items
	{
		xtype : 'container',
		region : 'north',
		layout : 'hbox',
		items : [ {
			xtype : 'fieldset',
			title : 'Search',
			layout : 'hbox',
			flex : 2,
			items : [{
				xtype : 'combobox',
				itemId : 'termSearchField',
				value : 'urn:lnc:2345-7',
				fieldLabel : 'Search Concept',
				flex : 1,
				displayField: 'description',
				valueField: 'urn',
				store: termSearchStore
			}]
		}/*, {
			xtype : 'fieldset',
			title : 'Sources',
			flex : 1,
			items :  {
				xtype : 'combobox',
				fieldLabel : 'Sources',
			}
		}*/

		]
	}, {
		xtype : 'tabpanel',
		itemId : 'termSearchTabs',
		title: 'Search/Display Results',
		region : 'center'
	} ]
});
