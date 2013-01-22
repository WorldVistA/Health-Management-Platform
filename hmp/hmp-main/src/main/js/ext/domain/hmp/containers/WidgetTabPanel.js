/*
 * This is a customizable tab panel that can be filled with widgets.
 * The state can be saved/restored.
 */
Ext.define('EXT.DOMAIN.hmp.containers.WidgetTabPanel', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.widgettabpanel',
	requires: [
        'EXT.DOMAIN.hmp.UserContext',
        'EXT.DOMAIN.hmp.containers.MultiGridPanel',
        'EXT.DOMAIN.hmp.containers.PortalPanel',
        'EXT.DOMAIN.cpe.order.MedOrderingToolbar',
        'EXT.DOMAIN.cpe.PatientWikiPanel',
        'EXT.DOMAIN.hmp.containers.PatientAwarePanel',
        'EXT.DOMAIN.hmp.containers.LabReviewTab',
        'EXT.DOMAIN.hmp.containers.MedsReviewTab',
        'EXT.DOMAIN.hmp.containers.OnePanelToRuleThemAll'
    ],
    itemId: 'WidgetTabPanelID',
	editMode: true, // true to allow the user to edit/add tabs
	stateId: 'brian',
	stateful: false,
    bodyPadding: 6,
	configName: '',
	deferredRender: true,
	stateEvents: ['tabchange'],
	plain: true,
	defaults: function(config) {
		var me = this;

		// assign a stateid based on the parent id.
		if (!config.stateId) {
			config.stateId = this.stateId + '.' + this.items.length;
		}

		// if in edit mode (and no tabConfig specified), then add a tabConfig for editing.
		var tabconf = config.tabConfig || {};
		var menu = {		
				items: [],
				listeners: {
					show: function(menu, eopts) {
						var activeTabIdx = me.items.indexOf(me.getActiveTab());
						var moveLeft = menu.down('menuitem[text="Move Left"]');
						var moveRight = menu.down('menuitem[text="Move Right"]');

						// show/hide the relevant buttons
						if (moveLeft && moveRight) {
							moveLeft.setDisabled(activeTabIdx <= 1);
							moveRight.setDisabled((activeTabIdx + 1) >= me.items.length);
						}
					}
				}
		};

		if (this.editMode) {
			// all tabs have left/right/remove items
			menu.items.push({text: 'Move Left', icon: '/images/icons/arrow_left.png', handler: function(item, e, eOpts) {
				var tab = me.getActiveTab();
				var activeTabIdx = me.items.indexOf(tab);
				if (activeTabIdx >= 1) {
					me.remove(tab, false);
					me.insert(activeTabIdx-1, tab);
					me.setActiveTab(activeTabIdx-1);
				}
			}});

			menu.items.push({text: 'Move Right', icon: '/images/icons/arrow_right.png', handler: function() {
				var tab = me.getActiveTab();
				var activeTabIdx = me.items.indexOf(tab);
				if ((activeTabIdx + 1) <= me.items.length) {
					me.remove(tab, false);
					me.insert(activeTabIdx+1, tab);
					me.setActiveTab(activeTabIdx+1);
				}
			}});

			menu.items.push({text: 'Rename Tab', icon: '/images/icons/cog_edit.png', handler: function() {
				var tab = me.getActiveTab();
				Ext.MessageBox.prompt('**HIGHLY EXPERIMENTAL**', 'What do you want to call this (will overwrite existing):', function(btn, text) {
	        		if(text != null && text.length > 0)
	        		{
	        			this.setTitle(text);
	        		}
	        	}, tab, false, tab.title);
			}});

			menu.items.push({text: 'Remove', icon: '/images/icons/cog_delete.png', handler: function() {
				var tab = me.getActiveTab();
				if(tab!=null) {me.remove(tab);}
//				me.tabBar.closeTab(this.parentMenu.floatParent);
			}});
			menu.items.push('-');

			// add the menu to the tab
			config.tabConfig = Ext.applyIf(tabconf, {xtype: 'menutab', menu: menu});
		}
	},
	insertAddButton: function(menuItems) {
		var me = this;
		this.insert(0, {
			itemId: 'TabEditBtnID',
			tabConfig: {
				icon: '/images/icons/ic_plus.png',
				arrowCls: '',
				activate: Ext.emptyFn,
				deactivate: Ext.emptyFn,
				cls: 'hmp-add-page',
				xtype: 'button',
                menu: {
					items: [
					        {xtype: 'menuitem', text: 'Save', handler: function(menuitem, evt) {

					        	var cfg = me.getState();
					        	
					        	var dfltVal = '';
					        	
					        	var rec = EXT.DOMAIN.cpe.CPEPanel.selectedTabPanelRecord;
					        	if(rec)
					        	{
					        		if(rec.data.url.indexOf('.js')<0)
					        		{
					        			dfltVal = rec.data.name;
					        		}
					        	}	

					        	Ext.MessageBox.prompt('**HIGHLY EXPERIMENTAL**', 'What do you want to call this (will overwrite existing):', function(btn, text) {
					        		Ext.Ajax.request({
					        			url: '/param/put/CPE PAGE CONFIG?instance=' + text,
					        			jsonData: Ext.JSON.encode(cfg),
					        			success: function(resp) {
					        				//Ext.log(resp);
					        			}
					        		});
					        	}, null, false, dfltVal);
					        	
					        	evt.stopPropagation(); // doesn't seem to work
					        }},
					        '-'
					        ],
					        listeners: {
					        	click: function(menu, item, e) {
					        		if (item.text !== 'Save') {
					        			// TODO: would be nice to automatically open the editor here.
					        			me.insert(1, {xtype: item.tabxtype, title: item.text});
					        		}
					        	}
					        }
				},
				listeners: {
					click: function() {
						var me = this;
						// skip ajax request if already loaded
						if (this.loadedFlag) {
							return;
						}
						this.loadedFlag = true;

						Ext.Ajax.request({
							url: '/app/list?type=EXT.DOMAIN.cpe.tabtypes',
							success: function(resp) {
								var data = Ext.JSON.decode(resp.responseText);
								var menuItems = [];
								for (var i in data.items) {
									var item = data.items[i];
									menuItems.push({tabxtype: item.code, text: item.name});
								}
								me.menu.add(menuItems);
								me.menu.show();
							}
						});

					}
				}
			}	
		});
	},

	initComponent: function() {
		var me = this;
		this.callParent(arguments);

		// add button and attach handlers
		if (this.editMode) {
			this.insertAddButton([]);
		}

		//this.applyState({tabs: this.itemsXYZ});
	},
	listeners: {
		beforeadd: function(container, comp, idx) {
			// register the components tools as menu items (if any)
			if (Ext.isArray(comp.tools) && comp.tabConfig && Ext.isObject(comp.tabConfig.menu)) {
				var menuitems = comp.tabConfig.menu.items;
				for (var i in comp.tools) {
					var found = false;
					for(var j in menuitems)
					{
						if(menuitems[j].text == comp.tools[i].text && menuitems[j].tooltip == comp.tools[i].tooltip)
						{
							found = true;
						}	
					}
					if(!found)
					{	
						var tool = comp.tools[i];
						// Tooltip=text?
						menuitems.push(tool);
					}
				}
			}
		},
		render: function() {
			// Component registers an onresize stateEvent somewhere inbetween initComponent and render, clear it here.
			this.removeListener('resize', this.onStateChange, this);
		},

		tabchange: function(tabPanel, newCard, oldCard, opt) {
			var temp = tabPanel.activeTab.tab.text || '';
			var pos = temp.indexOf('*');
			if (pos > -1) {
				var str = temp.substring(0, pos);
				tabPanel.activeTab.tab.setText(str);
				tabPanel.activeTab.tab.removeCls('new-data');
				tabPanel.activeTab.tab.addClass('new-data-click');
			}

		}
	},

	getState: function() {
		var me = this, state = {tabs: []};

		// TODO: Custom State handling here
		for (var i in me.items.items) {
			var tab = me.items.items[i];
			if (tab.itemId !== 'TabEditBtnID') {
//				if(!tab.rendered)
//				{
//					alert('Non-rendered tab found; Forcing render.');
//					tab.doLayout();
//					tab.forceComponentLayout();
//				}	
				state.tabs.push(tab.getState());
			}
		}
		state.activeTabIdx= this.items.indexOf(this.getActiveTab());

		var jsonstr = Ext.JSON.encode(state);
		//Ext.log('saving state', jsonstr.length, state);
		return state;
	},

	applyState: function(state){
		var me = this;
		//console.log('loading state', state);

		// TODO: Custom state apply here.
		if (Ext.isArray(state.tabs)) {
//			for(var key in state.tabs)
//			{
//				/*
//				 * Need to add one at a time, unfortunately, so the attributes are properly initialized so getState() will return good values later.
//				 */
//				this.add(state.tabs[key]);
//			}	
			/*
			 * The code below resulted in tabs that, if they were never shown, would not persist properly when saved.
			 */
//			this.add(state.tabs);
//			this.removeAll(true);
			for (var i=state.tabs.length-1; i >=0; i--) {
				// only insert the tabs if the user has the required role (or no required role is declared)
				var tab = state.tabs[i];

				if (!tab.requireKey || EXT.DOMAIN.hmp.UserContext.currentUserHasVistaKey(tab.requireKey)) {
					var cmp = this.insert((this.editMode ? 1 : 0), tab);
				}
			}
		}

		// activate a tab
		var activeTab = state.activeTabIdx || 0;
		this.setActiveTab((this.editMode ? activeTab+1 : activeTab));
	}
});

Ext.define('Ext.ux.tab.Tab', {
	extend: 'Ext.tab.Tab',
	alias: 'widget.menutab',
	requires: [
	           'Ext.button.Split'
	   ],
	
	   // FROM: http://stackoverflow.com/questions/6751071/how-can-i-add-tools-in-a-tab-of-an-extjs-tabpanel
	
	   /**
	    * Menu align, if you need to hack the menu alignment
	    */
	   menuAlign: 'tl-bl?',
	
	   constructor: function() {
		   this.callParent(arguments);
	
		   //Kind of harsh, we need the click action from
		   //split button instead.
		   //Or if you preferred, you can always override this
		   //function and write your own handler.
		   this.onClick = Ext.button.Split.prototype.onClick;
	   },
	
	   /**
	    * Hack the default css class and add
	    * some reasonable padding so to make it looks
	    * great :)
	    */
	   onBoxReady: function() {
		   //We change the button wrap class here! (HACK!)
		   this.btnWrap.replaceCls('x-tab-arrow x-tab-arrow-right',
		   'x-btn-split x-btn-split-right')
		   .setStyle('padding-right', '20px !important');
	   }
});

Ext.define('EXT.DOMAIN.hmp.containers.WidgetTabPanelEditWin',{
	extend: 'Ext.window.Window',
	requires: ['EXT.DOMAIN.cpe.designer.PanelEditor'],
	height: 400,
	width: 600,
	stateful: false,
	title: 'Page Settings',
	layout: 'fit',
	modal: true,
	autoDestroy: true,
	configure: function(comp) {
		var editor = null;
		if (comp && Ext.isFunction(comp.getEditor)) {
			editor = comp.getEditor();
		}

		// if no editor was derived, then use the default
		if (!editor) {
			editor = Ext.create('EXT.DOMAIN.cpe.designer.PanelEditor', {title: comp.title});
		}

		// remove any existing editor, unless its the current one
		if (this.editor && this.editor != editor) {
			this.remove(this.editor);
		}

		this.editor = this.insert(0, editor);
		this.comp = comp;
		
		if(comp.ownerCt && comp.ownerCt.layout && comp.ownerCt.layout.type=='gridbag') {
			if(this.down('#collapsechkbox')){this.down('#collapsechkbox').disabled = true;}
			if(this.down('#collapsewhenemptychkbox')){this.down('#collapsewhenemptychkbox').disabled = true;}
		}
			
		this.show();
	},
	close: function() {
		// remove the editor first to prevent the window from destroying it
		this.remove(this.editor);
		this.callParent();
	},
	save: function() {
		// Instead of trampling any prefs that aren't covered by the editor, we will merge any changes from the editor into the natural state of the component.
		var cfg = this.comp.getState();
		Ext.apply(cfg, this.editor.getEditorValues(), {});

		// remove the tab, then recreate/add new one
		var pid = this.comp.pid || EXT.DOMAIN.hmp.PatientContext.getPatientInfo().pid;
		var clazz = this.comp.$className;
		var tabpanel = this.comp.ownerCt;
//		var ocmp = tabpanel.getComponent(this.comp);
//		var rmvAt = null;
//		if(!ocmp || ! (ocmp === this.comp)) {
//			alert('Component error. Cannot replace existing component because it couldn\'t be "found."');
//			ocmp = tabpanel.getComponent(this.comp.id);
//			if(!ocmp || ! (ocmp === this.comp)) {
//				alert('Nope, by ID didn\'t work either.');
//				for(var i = 0; i<tabpanel.items.length; i++)
//				{
//					var icmp = tabpanel.items.items[i];
//					if(icmp === this.comp)
//					{
//						this.comp = icmp;
//						alert('Weeee gotcha!!');
//						// But STILL doesn't remove. Maybe RemoveAt.
//						rmvAt = i;
//					}
//				}	
//			}
//			else
//			{
//				this.comp = ocmp;
//			}	
//		}
//		
//		// Impossible to remove the panel. Ridiculous. Looks like a big bug.
//		// It always seems to be the LAST panel I remove.
//		// Maybe the array is failing when it gets to the last original item, or some such.
//		// Maybe if I play around with recalculations of components it might "reset" the array, or something.
//		// Definitely always the LAST one I modify. Hmm.
		var curidx = tabpanel.items.indexOf(this.comp);
//		if(rmvAt)
//		{
//			tabpanel.items.items.splice(i, 1);
//		}
		tabpanel.remove(this.comp, true);
		
		/*
		 * The below code was copied and pasted from Container's remove() function 
		 * because apparently in some cases it can't find its own item by the item's ID value.
		 * 
		 * It is a private function, so this *might* break in 4.1, so we'll want to check and 
		 * see if the bug is fixed with the normal remove function in 4.1 when we upgrade.
		 */
//		if (this.comp && tabpanel.fireEvent('beforeremove', tabpanel, this.comp) !== false) {
//			tabpanel.doRemove(this.comp, true);
//			tabpanel.fireEvent('remove', tabpanel, this.comp);
//        }

		this.comp = tabpanel.insert(curidx, Ext.create(clazz, cfg));
		this.comp.tbar = cfg.tbar;
		this.comp.bbar = cfg.bbar;

		// activate the tab if this is a tabpanel
		if (tabpanel.setActiveTab) {
			tabpanel.setActiveTab(this.comp);
		}

		// refire the patientchange event to sync up this widget (if a pid exists);
		if (pid) {
			this.comp.fireEvent('patientchange', pid);
		}

		this.close();
	},
	bbar: [
	       '->',
	       {xtype: 'button', text: 'Save', handler: function() {this.up('window').save();}},
	       {xtype: 'button', text: 'Cancel', handler: function() {this.up('window').close();}}
	       ]
});
