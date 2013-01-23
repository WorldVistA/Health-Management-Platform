/**
 * Attempting to consolodate detail panels from ViewDefGridPanel and MultiGridPanel.js into 1 reusable panel.
 * 
 * I'm trying to figure out if/how to migrate some ViewDefGridPanel functionality here.
 * I need to support the following:
 * 1) Detail panel needs toolbar, menu, tools, etc to toggle different detail flavors.
 * -- having difficulting figuring out how/if/where to have the detail panel reload to a different detail flavor?
 * 2) Detail panel may need to take cue from multiple grids (condition review scenario)
 * 3) Going back-and-forth trying to figure out if the loader should belong to ViewDefGridPanel and/or GridDetailPanel?
 * -- Seems like redundant configuration to have it be part of the detail tip/window/etc.
 * 4) Is there a use case for multi-select selection model?  How does that effect the details?
 * -- Array of recs?
 * 
 * 5/4/2012 ideas:
 * - how can GridAdvisor contribute detail panel configuration?
 * - turn into a card layout with predefined "cards" chart/table/html?
 * - cards get a toolbar/buttons generated automatically to swap/switch between them?
 * - chart card gets rewritten if/when needed (for multi-series)
 * - since chart/table may be driven by an alternate ViewDef, use its GridAdvisor to build a toolbar for details?!?
 * - create an API function for handling its update (which card to activate, etc
 * 
 * TODO: Stacked chart mode?
 */
Ext.define('org.osehra.cpe.viewdef.GridDetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.griddetailpanel',
    requires: 'org.osehra.cpe.ChartPanel',

    //dock: 'bottom',
	resizable: true, // is the detail window resizable (applies to all detailTypes, except none).  For right, bottom, the detailBorder will be ajusted appropriately
	//resizeHandles: 'w',
	
	collapseFirst: false,
	collapsible: false, // collapse is only applicable to right and bottom detailTypes
	collapseDirection: 'right', 
	collapsed: false,
	titleCollapse: true, 
	animCollapse: false, 
	autoCollapse: true, // if collapsible is true and there is no detail record to display, then collapse the detail automatically.
	
	autoScroll: true,
	minHeight: 150, 
	minWidth: 250,
	bodyPadding: '5 5 5 5',
	
	emptyTitle: 'Detail: ', // displayed when there when no record is selected
	emptyHTML: 'No record selected',
	
	extraParams: null, // if defined, additional params that are added to the details URL/POST.
	layout: 'card',
	enableTrendChart: false, // if true, adds the trend and chart cards.
	tools: [],
	
	// currently bound grid + records (if any)  
	boundGrid: null,
	boundRecs: null,
	deselectMode: true, // iif true, attempt to deselect any currently selected record on another grid

	chartCfg: {
		xtype: 'chartpanel',
		legend: {position: 'top'}
	},

	initComponent: function() {
		var me = this;
		
		if (this.collapsible && this.autoCollapse) {
			this.collapsed = true;
		}
		
		// silly layout issue
		if (this.dock === 'bottom') {
			this.height = this.height || this.minHeight;
			delete this.width;
		} else {
			this.width = this.width || this.minWidth;
			delete this.height;
		}
			
	    // bottom has some slight differences from right
	    if (this.dock == 'bottom' && this.resizable === true) {
	    	Ext.apply(this, {resizeHandles: 'n', collapseDirection: 'top'});
	    }
	    
	    
    	this.callParent();
    	
    	// build the specified cards
    	this.buildCards();
    	
       	// resize handler to get the layout to work
    	if (this.resizable) {
	    	this.on('resize', function(comp, width, height, eOpts) {
	    		me.ownerCt.forceComponentLayout();
	    	});
    	}

    	this.setTitle(this.emptyTitle);
    	//this.update(this.emptyHTML);
	},
	beforeRender: function() {
//        Ext.log(Ext.getClassName(this) + ".beforeRender()");
        this.callParent(arguments);

        // apply a pointer cursor to the header
        if (this.collapsible && this.header) {
            this.header.body.setStyle('cursor', 'pointer');
        }
    },
	setCard: function(idx) {
		if (this.rendered) {
			// adjust the active card
			this.getLayout().setActiveItem(idx);
			
			// also make sure the toggle button is selected
			var btn = this.down('button[cardIdx=' + idx + ']');
			if (btn && btn.enableToggle === true) {
				btn.toggle(true);
			}
			
			// expand if collapsed (and the ownerCt layout will support the expand() method)
			if(this.collapsed && Ext.isFunction(this.ownerCt.calculateChildBoxes))
			{	
				this.expand();
			}
			
//			// remove error mask, if there is one
//			var item = this.getLayout().getActiveItem(idx);
//		    var area = (item.body)?item.body:item.el
//		    if(area){area.unmask();}

		}
	},
	
	/*
	 * This overrides the default update and redirects it to card#1 which is where the actual
	 * html details should be shown.  To update one of the other cards (ie chart), use updateChart()
	 */
	update: function(htmlOrData, loadScripts, callback) {
		// redirect update to the first card (and ensure its visible)
		this.items.get(0).update(htmlOrData, true, callback);
		//this.items.get(0).body.unmask();
		this.setCard(0);
	},
	
	updateChart: function(chartCfg, viewParams, viewID, graphStoreHandler) {
		if (!this.enableTrendChart) {
			return; // no chart/grid to update
		}
		this.remove(this.items.get(2), true);
		var cfg = this.chartCfg;
		cfg.width = this.width;
		cfg.height = this.height;
		var chartPanel = this.add(cfg);
		var newParams = Ext.apply(this.grid.curViewParams || {}, viewParams);
		this.grid.setViewDef(viewID || this.grid.viewID, newParams, true, graphStoreHandler, chartPanel);
		
		this.setCard(2);
	},
	
	/**
	 * This details panel sometimes needs to know which grid/records its currently bound to.
	 */
	bindGrid: function(grid, recs) {
		
		// if deselect is active, and this is a different grid than the previously bound grid...
		if (this.deselectMode === true && this.boundGrid != grid && this.boundRecs) {
			this.boundGrid.getSelectionModel().deselect(this.boundRecs);
		}
		
		// store which grid/rec we are bound to (rec may be an array)
		this.boundGrid = grid;
		this.boundRecs = recs;
	},
	
	buildCards: function() {
		var me = this;
		
		// first card is content card
		this.add({xtype: 'container',tag:'detail'}).update(this.emptyHTML);
		
		// second is trend/grid, third is chart (if enabled)
		if (this.enableTrendChart) {
			// chart defaults to empty containers, will be recreated when needed.
			this.grid = this.add({xtype: 'viewdefgridpanel', tag:'labTrend', reconfigureColumnsAlways: true, header: true, viewID: 'org.osehra.cpe.vpr.queryeng.LabTrendViewDef'});
			this.grid.update(this.emptyHTML)
			this.add({xtype: 'container', tag:'labTrend'}).update(this.emptyHTML);
			
			// also build tools to toggle between the cards
			var toggleHandler = function(btn, e) {
				me.setCard(btn.cardIdx || 0);
				e.stopEvent();
			};
			this.tools.push({xtype: 'button', enableToggle: true, toggleGroup: 'card', cardIdx: 0, tooltip: 'Detail', handler: toggleHandler, icon: '/images/icons/text_align_justify.png', pressed: true});
			this.tools.push({xtype: 'button', enableToggle: true, toggleGroup: 'card', cardIdx: 1, tooltip: 'Table/Trend',  handler: toggleHandler, icon: '/images/icons/table.png'});
			this.tools.push({xtype: 'button', enableToggle: true, toggleGroup: 'card', cardIdx: 2, tooltip: 'Chart/Graph', handler: toggleHandler, icon: '/images/icons/chart_line.png'});
			
			/* TODO: The filter menu for the details isn't ready for primetime
			// add a filter menu (since the nested viewdefgrid does not have a header
        	this.grid.filtermenu = Ext.create('Ext.menu.Menu');
        	var cfg = {xtype: 'button', text: 'Filter(s)', ui: 'link', tooltip: 'Filter Menu', menu: this.grid.filtermenu, listeners: {}};
        	cfg.listeners.menutriggerover = function(btn, menu) {
        		this.menu = me.grid.filtermenu;
        	}

        	// create a placeholder menu
        	this.tools.push(cfg);
        	*/
		}
	},

	reload: function(extraParams) {
		var loader = this.getLoader();
		if (extraParams) {
			this.extraParams = extraParams;
		}
		if (loader) {
			loader.reload();
		}
	}
});
