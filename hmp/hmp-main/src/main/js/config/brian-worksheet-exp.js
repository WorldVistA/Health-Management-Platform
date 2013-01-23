{
	
	activeTabIdx: 0,
	tabs: [
	       {
		    	xtype:'wunderpanel',
		    	title: 'Condition Review2',
		    	detail: {height: 350},
		    	doAppBarExperiment: function() {
		    		var appbar = Ext.getCmp('AppBar');
		    		appbar.addAppMenuItem({
		    			xtype: 'cycle',
		    			showText: true,
		    			prependText: 'Mode: ',
		    			itemId: 'ModeCyleBtnID',
		    			menu: {
		    				items: [
	    				        {text: 'Board', iconCls: 'hmp-row-action-board-btn', icon: '/images/icons/application.png'},
	    				        {text: 'Split', iconCls: 'hmp-row-action-split-btn', icon: '/images/icons/application_tile_horizontal.png'},
	    				        {text: 'Chart', iconCls: 'hmp-row-action-chart-btn', icon: '/images/icons/layout_content.png'}
    				        ]
		    			}
		    		});
		    		
		    		appbar.addAppMenuItem({
		    			xtype: 'cycle',
		    			showText: true,
		    			prependText: 'Config: ',
		    			menu: {
		    				items: [
	    				        {text: 'Generic Chart'},
	    				        {text: 'Search', icon: '/images/icons/warning_sign.png', iconCls: 'hmp-row-action-btn'},
	    				        {text: 'Activity Stream'},
	    				        {text: 'Etc...'}
    				        ]
		    			}
		    		});
		    		
		    		appbar.addAppMenuItem('->');
		    		
		    		var sb = Ext.getCmp('searchBox');
		    		sb.setWidth(150);
		    		sb.on('focus', function(text, evt) {text.setWidth(400)});
		    		sb.on('blur', function(text, evt) {text.setWidth(200)});
		    		appbar.addAppMenuItem(sb);
		    		
		   	     
			   	    this.keymap = Ext.create('Ext.util.KeyMap', {
				   	    target: Ext.getBody(),
				   	    key: 's',
				   	    fn: function() {Ext.getCmp('searchBox').focus(false, 20);},
				   	    scope: this
			   	    });
		    	},
		    	listeners: {
		    		afterrender: function() {
		    			var me = this;
		    			this.detail1 = this.items.get(1);
		    			this.detail2 = this.items.get(2);
				   	    this.items.get(0).on('select', function(model, rec, idx) {
	    					var rec = this.getActualStoreIndexInsteadOfWhatTheViewFalselyThinksIsCorrectDueToExtJsGroupingBug(idx);
	    					var grid = me.items.get(1);
	    					var viewdef = rec.get('viewdef');
	    					var viewdeftitle = rec.get('viewdef_title');
	    					var viewparams = rec.get('viewdef_params') || {};
	    					if (viewdef) {
	    						me.detail1.setViewDef(viewdef, viewparams);
	    						if (viewdeftitle) me.detail1.setTitle(viewdeftitle);
	    					}
				    	});
		    		}
		    	},
		    	items: [
	    	        {
	    	        	xtype: 'viewdefgridpanel',
	    	        	title: 'Goals/Guidelines',
	    	        	viewID: 'org.osehra.cpe.vpr.queryeng.GoalsDueViewDef',
	                   	//rowBodyTpl: '<tpl for="comments"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>{entered}</b> ({author}):<em style="grey: b; font-weight: normal; font-style: italic; padding: 0px 0px 0px 15px;">{comment}</em></p></tpl>',
	                   	detailType: '#conditiondetailpanel2',
	                   	tools: [{xtype: 'viewdeffiltertool', paramKeys: ['conditions']}],
	                   	reconfigureColumnsAlways: true,
	    	    		gridX: 0,
	    	    		gridY: 0,
	    	    		widthX: 1,
	    	    		widthY: 2,
	    	    		weightX: 1,
	    	    		weightY: 1
	    	        },
	    	        {
	    	        	xtype: 'viewdefgridpanel',
	    	        	viewID: 'org.osehra.cpe.vpr.queryeng.MedsTabViewDef',
//	    	        	titleTpl: 'Relevant Meds ({total})',
	                    tools: [{xtype: 'viewdeffiltertool'}],
	                    detailType: '#conditiondetailpanel2',
	                	reconfigureColumnsAlways: true,
	    	    		gridX: 1,
	    	    		gridY: 0,
	    	    		widthX: 1,
	    	    		widthY: 1,
	    	    		weightX: 1,
	    	    		weightY: 1
	    	        },
	    	        {
	    				xtype: 'griddetailpanel',
	    				enableTrendChart: false,
	    				itemId: 'conditiondetailpanel2',
	    	    		gridX: 1,
	    	    		gridY: 1,
	    	    		widthX: 1,
	    	    		widthY: 1,
	    	    		weightX: 1.5,
	    	    		weightY: 1.5
	    	        }
	   	        ]
	       },	       
	       
	       
	       {
		    	xtype:'wunderpanel',
		    	title: 'Condition Review',
		    	detail: {height: 350},

		    	listeners: {
		    		afterrender: function() {
		    			var me = this;
	    				this.items.get(0).on('select', function(model, rec, idx) {
	    					var rec = this.getActualStoreIndexInsteadOfWhatTheViewFalselyThinksIsCorrectDueToExtJsGroupingBug(idx);
	    					var grid = me.items.get(1);
	    					var vals = rec.get('med_filters')
	    					grid.setViewDef(grid.curViewID, Ext.apply(grid.curViewParams, vals));
	    					
	    					var grid = me.items.get(2);
	    					var viewdef = rec.get('viewdef');
	    					grid.setViewDef(viewdef, grid.curViewParams)
	    				});
		    		}
		    	},
		    	
		    	items: [
	    	        {
	    	        	xtype: 'viewdefgridpanel',
	    	        	title: 'Conditions/Protocols',
	    	        	viewID: 'org.osehra.cpe.vpr.queryeng.ProtocolViewDef',
	    	        	viewParams: {group: 'conditionType'},
	    	        	detailType: '#conditiondetailpanel',
	    	    		gridX: 0,
	    	    		gridY: 0,
	    	    		widthX: 1,
	    	    		widthY: 1,
	    	    		weightX: 1,
	    	    		weightY: 1
	    	        }, 
	    	        {
	    	        	xtype: 'viewdefgridpanel',
	    	        	viewID: 'org.osehra.cpe.vpr.queryeng.MedsTabViewDef',
	    	        	titleTpl: 'Relevant Meds ({total})',
	                    tbarConfig : "org.osehra.cpe.viewdef.AutoFilterToolbar",
	    	        	//viewParams: {'qfilter_status': 'ACTIVE'},
	                    detailType: '#conditiondetailpanel',
	    	    		gridX: 1,
	    	    		gridY: 0,
	    	    		widthX: 1,
	    	    		widthY: 1,
	    	    		weightX: 2,
	    	    		weightY: 1
	    	        },
	    	        {
	    	        	xtype: 'viewdefgridpanel',
	    	        	title: 'Condition Goals/Observations/Guidelines',
	    	        	viewID: 'org.osehra.cpe.vpr.queryeng.DiabetesViewDef',
                    	rowBodyTpl: '<tpl for="comments"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>{entered}</b> ({author}):<em style="grey: b; font-weight: normal; font-style: italic; padding: 0px 0px 0px 15px;">{comment}</em></p></tpl>',
                    	detailType: '#conditiondetailpanel',
                    	reconfigureColumnsAlways: true,
	    	    		gridX: 2,
	    	    		gridY: 0,
	    	    		widthX: 1,
	    	    		widthY: 1,
	    	    		weightX: 3,
	    	    		weightY: 1
	    	        },
	    	        {
	    				xtype: 'griddetailpanel',
	    				enableTrendChart: false,
	    				itemId: 'conditiondetailpanel',
	    	    		gridX: 0,
	    	    		gridY: 1,
	    	    		widthX: 3,
	    	    		widthY: 1,
	    	    		weightX: 1,
	    	    		weightY: 1
	    	        }
	   	        ]
	       }
	       /*
	       {
		    	xtype: 'portalpanel', 
		    	title: 'Worksheet/DnD Demo',
		    	items: [{
		    		items: [
					 {
					     xtype: 'viewdefgridpanel',
					     title: "Labs",
					     titleTpl: 'Labs ({total})',
					     viewID: 'org.osehra.cpe.vpr.queryeng.LabViewDef',
					     detailType: '#CommonDetailPanelID',
					     height: 250,
					     detailTitleTpl: '{name} {result}{units}',
						 ddConfig: {
						    ddGroup: 'WorksheetGroup',
						    enableDrop: false
						 }
					 },	
					 {
					     xtype: 'viewdefgridpanel',
					     title: "Most Recent Vitals",
					     height: 250,
					     detailType: '#CommonDetailPanelID',
					     viewID: 'org.osehra.cpe.vpr.queryeng.VitalsViewDef',
					 },
					 {
					     xtype: 'viewdefgridpanel',
					     title: "Active Problems",
					     titleTpl: "Active Problems ({total})",
					     height: 250,
					     detailType: '#CommonDetailPanelID',
					     viewID: 'org.osehra.cpe.vpr.queryeng.ProblemViewDef',
					     viewParams: {'col.display': "infobtnurl,summary,onset,provider"}
					 }
				    ]
		    	},{
		    		items: [
	    		        {xtype: 'patientwikipanel', height: 450},
	    		        {xtype: 'griddetailpanel', itemId: 'CommonDetailPanelID', title: 'details', height: 300}
	    	        ]
		    	},{
		    		items: [
		                {xtype: 'viewdefgridpanel', title: "Immunizations", titleTpl: 'Immunizations ({total})', viewID: 'org.osehra.cpe.vpr.queryeng.ImmunizationsViewDef', height: 150, bbar: null},
		                {
		                    xtype: 'viewdefgridpanel',
		                    title: "Active Meds",
		                    titleTpl: 'Active Meds ({total})',
		                    viewID: 'org.osehra.cpe.vpr.queryeng.MedsViewDef',
		                    viewParams: {filter_status: 'ACTIVE'},
		                    height: 200,
		                    detailType: '#CommonDetailPanelID',
						    detailTitleTpl: '{Summary}',
						    ddConfig: {
						    	ddGroup: 'WorksheetGroup',
					            enableDrop: false
						    }	                    
		                },
		                {
		                	xtype: 'viewdefgridpanel',
		                	title: 'Recent Activity (last 300 days)',
		                	viewID: 'org.osehra.cpe.vpr.queryeng.RecentViewDef',
		                	detailType: '#CommonDetailPanelID',
		                	height: 250
		                }
	                ]
		    	}]
			}	 
			*/
    ]
}
