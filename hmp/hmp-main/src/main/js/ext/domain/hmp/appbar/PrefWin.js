Ext.define('EXT.DOMAIN.hmp.appbar.PrefWin',{
	extend: 'Ext.window.Window',
	requires: ['EXT.DOMAIN.cpe.roster.FavoriteRosterPicker'],
    alias: 'widget.prefwin',
	id: 'PrefWinID',
 	height: 350,
 	width: 450,
 	stateful: false,
 	stateId: 'PrefWinID',
 	title: 'User Preferences',
 	layout: 'fit',
 	closeAction: 'hide',
 	modal: true,
 	listeners: {
 		show: function() {
 			this.disable();
            var comboStore = Ext.getStore('favoriteRosters');
            comboStore.load();
 			this.load();

 		}
 	},
 	initComponent: function() {
 		this.callParent(arguments);
 		this.form = Ext.create('Ext.form.Basic', this);
 		this.on('afterrender', function(){

 		});
 	},
    afterRender:function(){
        this.callParent(arguments);

        console.log(Ext.getClassName(this) + ".afterRender()");

        var pnl = Ext.ComponentManager.get('animateOptionRadioPanel');
        var box = Ext.ComponentManager.get('selectorDockedPrefCheckBox');
        if (box.getValue()) {
            pnl.hide();
        } else {
            pnl.show();
        }
    },
 	load: function() {
 		var me = this;
 		Ext.Ajax.request({
 			url: '/param/get/VPR USER PREF?default={}',
 			success: function(resp) {
 				var prefs = Ext.JSON.decode(resp.responseText);
 				me.form.setValues(prefs);
 				me.enable();
 			}
 		});
 	},
 	save: function() {
 		var vals = this.form.getValues();
 		Ext.Ajax.request({
 			url: '/param/set/VPR USER PREF',
 			params: vals,
 			success: function() {
 				window.location.reload();
 			}
 		});
 		this.close();
 	},
 	bbar: ['->',{xtype: 'button', text: 'Save + Reload', handler: function() { this.up('window').save();}}],
 	items: {
 		xtype: 'tabpanel',
 		activeTab: 0,
 		plain: true,
 		defaults: {
 			padding: 5,
            defaults: {
//                 anchor: '100%',
                 labelSeparator: '',
                labelAlign: 'right'
            }
 		},
 		items: [{
 			xtype: 'fieldcontainer',
 			title: 'Display',
			layout: 'vbox',
	        items: [
                {
		        	fieldLabel: 'Theme',
		        	name: 'ext.theme',
                	xtype: 'combobox',
                	emptyText: 'Set Theme...',
                	store: [
                        ['/css/hi2-default.css','hi2 Blue'],
                        ['/css/hi2-aqua.css','Aqua'],
                        ['/css/hi2-blue-green.css','Teal'],
                        ['/css/hi2-light-gray.css','Light Gray'],
                        ['/css/hi2-dark-gray.css','Dark Gray'],
                        ['/css/hi2-crimson.css','Crimson'],
                        ['/css/hi2-fuschia.css','Fuschia'],
                        ['/css/hi2-green.css','Green'],
                        ['/css/hi2-maroon.css','Maroon'],
                        ['/css/hi2-midnight-blue.css','Midnight Blue'],
                        ['/css/hi2-orange.css','Orange'],
                        ['/css/hi2-violet.css','Violet'],
                        ['/lib/extjs-4.1.3/resources/css/ext-all.css','Default ExtJS 4.1'],
                        ['/lib/extjs-4.1.3/resources/css/ext-all-gray.css','Gray ExtJS 4.1']
                   ]
                },
                {
		        	fieldLabel: 'Default App',
                	xtype: 'combobox',
                	name: 'aviva.default.app',
                	emptyText: 'Default App...',
                	store: ['cpe','admin']
                },
		        {
		        	fieldLabel: 'Date Format',
		        	disabled: true,
                	xtype: 'combobox',
                	emptyText: 'Date Format...',
                	store: ['10/20/2011']
		        }, 
		        {
		        	fieldLabel: 'Time Format',
		        	disabled: true,
                	xtype: 'combobox',
                	emptyText: 'Time Format...',
                	store: ['11:23 A']
		        }
	        ]
		},{
			xtype: 'fieldcontainer',
 			title: 'CPE',
			layout: 'vbox',
			defaults: {
				labelWidth: 200
			},
	        items: [
                /*
				{
					fieldLabel: 'CPE Session Timeout (min)',
					name: 'cpe.timeoutmin',
					xtype: 'numberfield',
					allowDecimals: false
				},
				*/
				{
					boxLabel: 'Enable page/tab editing (advanced users)',
					name: 'cpe.editmode',
					inputValue: true,
					uncheckedValue: false,
					xtype: 'checkbox'
				}	       
            ]
		},{
			xtype: 'fieldcontainer',
 			title: 'Patient Selection',
			layout: 'vbox',
			defaults: {
				labelWidth: 150,
				labelAlign: 'right',
                labelSeparator: '',
                width: 400
			},
	        items: [
				{
					fieldLabel: 'Patient Selector Location',
					name: 'cpe.patientpicker.loc',
					xtype: 'combobox',
					store: [['north', 'Top'],['west', 'Left'],['south','Bottom'],['east','Right'],['window','Window/Dialog']]
				},
                {
		        	fieldLabel: 'Default Patient List',
		        	name: 'cpe.patientpicker.defaultRosterID',
                	xtype: 'favrosterpicker',
                	emptyText: 'Default Patient List...'

                },	 				
				{
					boxLabel: 'Remember last patient on refresh',
					name: 'cpe.patientpicker.rememberlast',
					inputValue: true,
					uncheckedValue: false,
					xtype: 'checkbox'
				},	       
				{
					boxLabel: 'Patient Selector Docked',
					name: 'cpe.patientpicker.pinned',
					inputValue: true,
					uncheckedValue: false,
					xtype: 'checkbox',
					id: 'selectorDockedPrefCheckBox',
					handler: function(box, chkd) {
						var pnl = Ext.ComponentManager.get('animateOptionRadioPanel');
						if(pnl)
						{
							if(chkd)
							{
								pnl.hide();
							}	
							else
							{
								pnl.show();
							}	
						}	
					}
				},	      
				{
					xtype: 'panel',
					id: 'animateOptionRadioPanel',
					hidden: true,
					layout: 'auto',
					items: [{
						xtype: 'panel',
						layout: 'hbox',
						items: [{
							boxLabel: 'Animation Delay Open',
							name: 'cpe.patientpicker.animateOption',
							inputValue: 'mouseover',
							uncheckedValue: false,
							xtype: 'radio',
							flex: 6
						},
						{
							xtype: 'textfield',
		                    itemId: 'animationDelaySeconds',
		                    name: 'cpe.patientpicker.animateDelaySeconds',
		                    value: '1000',
		                    flex: 3
						},
						{
							xtype: 'panel',
							html: 'ms',
							flex: 1
						}]
					},	       
					{
						boxLabel: 'Animation Click-to-Activate',
						name: 'cpe.patientpicker.animateOption',
						inputValue: 'click',
						uncheckedValue: false,
						xtype: 'radio'
					}]
				},
				{
					boxLabel: 'Enable masking between multi/single patient context (only for non-window modes)',
					name: 'cpe.patientpicker.mask',
					inputValue: true,
					uncheckedValue: false,
					xtype: 'checkbox'
				}	
	        ]
		},{
			xtype: 'fieldcontainer',
 			title: 'Developer',
			layout: 'vbox',
	        items: [
				{
					fieldLabel: 'ExtJS Library',
					itemId: 'ExtLibID',
					name: 'ext.libver',
					xtype: 'combobox',
					store: [
			            '/lib/extjs-4.1.3/ext-all.js',
                        '/lib/extjs-4.1.3/ext-all-dev.js'
			        ] 
				}
	        ]
		}]
 	}
});

