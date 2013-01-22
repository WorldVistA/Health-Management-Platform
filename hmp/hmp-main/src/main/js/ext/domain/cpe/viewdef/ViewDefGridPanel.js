/*
 * TODO: Use the message and success property to of the main json render?
 * TODO: Implement the save/restore state, but don't actually let it happen automagically.
 * -- looking to capture columns displayed, column widths, etc.
 * TODO: Tooltip configuration: 
 * -- hovering over value displays details (ie Date/Time column might say 7 days ago, hovering will show exact time/date)
 * -- detail tooltip
 * -- infobutton?!?
 * -- tooltip for more detailed column descriptions when hovering over columns?
 * TODO: New column types ( relative DTM display: 2h 34m ago)
 * TODO: no records mask window/message?
 * TODO: auto-refresh interval?
 */

/*
 * This is version 2.0 of the viewenggrid.
 * 
 * Major goals for version 2.0:
 * - Rewrite now that I know EXTjs much better
 * - Embedded detail windows (with several new detail modes: tooltip/window)
 * - Grouping/Sorting/Filtering
 * - Tighter integration with editors/preferences
 * - Pluggable toolbars
 * - Try to take some of the metadata burden off of viewdefs
 */
Ext.define('EXT.DOMAIN.cpe.viewdef.ViewDefGridPanel', {
    extend: 'Ext.grid.Panel',
	requires: [
        'EXT.DOMAIN.cpe.viewdef.GridDetailPanel',
        'EXT.DOMAIN.cpe.viewdef.HL7DTMColumn',
        'EXT.DOMAIN.cpe.viewdef.InfobuttonColumn',
        'EXT.DOMAIN.cpe.viewdef.RowActionColumn',
        'EXT.DOMAIN.cpe.viewdef.GridAdvisor',
        'EXT.DOMAIN.cpe.viewdef.AutoFilterToolbar',
        'EXT.DOMAIN.cpe.viewdef.ViewDefCellEditing',
        'EXT.DOMAIN.cpe.viewdef.ViewDefFilterTool'
    ],
	alias: 'widget.viewdefgridpanel',
	mixins: {
		patientaware: 'EXT.DOMAIN.hmp.PatientAware',
		stateful: 'Ext.state.Stateful'
	},
	gridAdvisor: null,
	scroll: false,
	  viewConfig: {
	    style: { overflow: 'auto', overflowX: 'hidden' }
	  },
	plugins: ['viewdefcellediting'],
	// grid configuration
	title: '', // the default title (may be overwritten by titleTpl after data is loaded)
	titleTpl: null, // if specified, reapplies this template as the title after data is loaded.
	//titleTpl: 'Labs <span style="font-weight: normal; font-style: italic;">({fromRecord}-{toRecord} of {total})</span>',
	grouping: true, // if true, the will auto-inject the grouping feature
	groupHeaderTpl: 'Group: {name} ({rows.length})', // if specified, the grouping template
	autoScroll: true,
	hideHeaders: false,
	selType: 'rowmodel',
	columns: [{header: '', flex: 1}], // columns will be configured dynamically, placeholder column is necessary in 4.1
	emptyText: 'No data found',

	// stateful configuration and editors
	stateful: false, // while this component can save/load state, we do not want to auto load/save state
	editorCmp: 'EXT.DOMAIN.cpe.designer.DataGridDetail', // the editor component (name or instance) use getEditor() to create/get it

	// ViewDef configuration
	viewID: null, // the viewdef to load
	viewParams: null, // the current viewdef params (or the viewdef params to autoLoad)
	curViewID: null, // the currently loaded view (null if no view loaded yet).  Do not modify directly.
	curViewParams: null, // once the view has been loaded, this represents all the parameters passed to the server

	// collapse configuration
	collapseGridIfEmpty: true, // if collapsible is true, then automatically collapse the grid if there are no rows
	collapseFirst: false,
	collapsible: false,
	collapsed: false,
	animCollapse: false,
	titleCollapse: true,

	// detail panel configuration
	detailTitle: 'Details', // default title of detail panel (when a record is selected)
	detailTitleField: null, // (optional; overrides detailTitle) specifies which field of the currently selected record to display as the title.
	detailTitleTpl: null, // (optional; overrides detailTitleField) an XTemplate to be rendered of the selected record
	detailType: 'none', // right, bottom, window, tip (click), tooltip (hover), rowbody, none
	detail: {}, // user detail config
	
	rowBodyTpl: null, // activates the row body feature, essentially becomes an additional preview row

	// loader used to load details.  Loader is usally on the detail panel, but it seems to work better
	// on the grid since the details can vary so much (window, panel, tooltip, etc.)
	loader: {
		grid: null, // reference to the grid that loaded these details
		rec: null, // reference to the record(s) that this detail is for
		//col: null, // reference to the column clicked on to load the details
		loadMask: true,
		
		ajaxOptions: {
			method: 'GET'
		},
		params: {
			format: 'html'
		},
		listeners:{
			beforeload: function(loader, op, eopts) {
				op.skipErrors = true;
				loader.getTarget().body.unmask();
			}
		},
//        success: function(loader, response) {
//        	loader.getTarget().body.unmask();
//        },

		failure: function(loader, response) {
//			var me = this;
			var detailPanel = loader.getTarget();
			detailPanel.update("<p><table class='hmp-labeled-values'><tr><td width='200'></td></tr></table></p>");
//			this.mask = detailPanel.body.mask("Component Received an Error. Try Reloading.");
//				this.mask.on('click', function(){
//					me.reload();
//				});
			Ext.log(response);
			detailPanel.body.mask("Component Received an Error. Try Reloading.");
		},
		rendererXX: function(loader, response, active) {
			var text = response.responseText;
			var detail = loader.getTarget();
			loader.getTarget().update(text);
		},
		reload: function() {
			this.loadDetails(this.grid, this.rec);
		},
		loadDetails: function(grid, rec) {
			var detailPanel = this.getTarget();
			if(detailPanel.body)
				detailPanel.body.unmask();

			// update the member variables
			this.grid = grid;
			this.rec = rec;
//			this.col = col;
			
			// expand the compnent (if collapse).
			detailPanel.show();
			if (detailPanel.collapsible && detailPanel.collapsed) {
				detailPanel.expand();
			}
			detailPanel.setTitle(grid.getDetailTitle(rec));
			
			// you can configure which field contains the URL to load details and how its loaded
			var cfg = {};
			var uid = rec.get('uid');
			if(!uid){
				uid = rec.get('UID');
				if(!uid){
					uid = rec.get('Uid');
				}
			}	
			var strs = (grid.detailField || 'selfLink').split('|');
			cfg.params = Ext.apply({}, detailPanel.extraParams);
			cfg.renderer = (strs.length > 1) ? strs[1] : 'html';
			cfg.url = rec.get(strs[0]) || '/vpr/detail/' + encodeURIComponent(uid);
			if (cfg.url) {
				detailPanel.update();
				this.load(cfg);
			} else {
				detailPanel.update('Unable to fetch detail for this item.  No details LINK or UID specified.');
			}
			detailPanel.body.scrollTo('top', 0);
			if(rec.data['wasViewed'] == false)
			{
				rec.data['wasViewed'] = true;
				grid.reconfigure(); // Want to let this grid know that state has changed on this row.
			}	
		}
	},

	initFeatures: function() {
		var me = this;
		
		// inject the grouping feature (if specified)
		var features = [], viewConfig = this.viewConfig || {};
		if (this.grouping === true) {
			features = features.concat([{ftype:'grouping', groupHeaderTpl: this.groupHeaderTpl}]);
		}
		
		// inject rowbody feature (if detailtype is rowbody)
		if (this.detailType === 'rowbody') {
			features = features.concat([Ext.create('Ext.grid.feature.RowBody', {
				getAdditionalData: function(data, rowIdx, rec, orig) {
					var headerCt = this.view.headerCt, colspan = headerCt.getColumnCount();
					return {
						//rowBody: "more info for row: " + rowIdx,
						//rowBodyCls: (rowIdx % 2 == 0) ? this.rowBodyHiddenCls : Ext.baseCSSPrefix + 'grid-row-over',
						rowBodyCls: this.rowBodyHiddenCls,
						rowBodyColspan: colspan-1
					}
				}
			})]);
		} 
		
		// if a rowbody template is specified, inject the proper features to support it
		// TODO: This is currently not compatible with setting detailType='rowbody'
		if (this.rowBodyTpl) {
			// initalize the template
			if (Ext.isString(this.rowBodyTpl)) {
				this.rowBodyTpl = new Ext.XTemplate(this.rowBodyTpl);
			}
			
			// TODO: The rowwrap feataure is necessary to make this look better, but its causing some weird issues
			// with other components (PatientBanner).  Not sure how/why, but maybe revisit this soon when we get to EXT4.1
			//features.push({ftype: 'rowwrap'});
			features.push({
                ftype: 'rowbody',
                getAdditionalData: function(data, idx, record, orig, view) {
                    var headerCt = this.view.headerCt, colspan = headerCt.getColumnCount();
                    return {
                        rowBody: me.rowBodyTpl.applyTemplate(data),
                        rowBodyCls: this.rowBodyCls,
                    	rowBodyColspan: colspan
                    }
                }
            });
		}

		// if drag-and-drop is enabled, configure it and add the plugin
		if (this.ddConfig) {
			if (!viewConfig.plugins) viewConfig.plugins = [];
			viewConfig.plugins.push(Ext.applyIf(this.ddConfig, {ptype: 'gridviewdragdrop'}));
			this.viewConfig = viewConfig;
		}

		// add/append features
		if (this.features) {
			this.features = features.concat(this.features);
		} else {
			this.features = features;
		}
	},

	initToolbar: function(obj) {
		if (!obj) {
			return null;
		}

		if (Ext.isString(obj) && obj.indexOf('.')) {
			// if there is a . lets assume its a full component name
			obj = Ext.create(obj);
		} else if (Ext.isString(obj)) {
			// otherwise, lets assume its a xtype/widget name
			obj = Ext.widget(obj);
		} else if (Ext.isObject(obj) && obj.xtype) {
			// its not an ext component yet, just a object
			obj = Ext.widget(obj.xtype, obj);
		} else {
			return obj;
		}

		// if there is a bindStore method, then bind the store
		if (Ext.isFunction(obj.bindStore)) {
			obj.bindStore(this.store);
		}
		if (Ext.isFunction(obj.bindGrid)) {
			obj.bindGrid(this);
		}

		return obj;
	},

	initComponent: function() {
		var me = this;
		
		// create/configure the store for this grid
		me.store = Ext.create('Ext.data.Store', {
            autoLoad: false,
            buffered: true,
            remoteSort: true,
            remoteGroup: true,
            // leadingBufferZone and pageSize is set dynamically by the viewdef.
            fields: [], // dynamically created/updated from JSON
            proxy: {
                //simpleSortMode: true,
                type: 'ajax',
                reader: {type: 'json', root: 'data'},
                limitParam: 'row.count',
                startParam: 'row.start',
                groupParam: 'group',
                sortParam: 'sort',
                directionParam: 'sort.dir',
                // will be updated by setViewDef()
                extraParams: {
                    mode: 'json'
                }
            }
        });

		// tools may also be rendered as menu items in some cases, so use the icon/text elements as well.
		// cant use the addTools() method because we need this data intact prior to render.
		// TODO: Something really weird happens here when we try to use this.tools.push() so I'm using Array.merge instead
		me.tools = Ext.Array.merge(me.tools || [], [{
			type: 'refresh',
			icon: '/images/icons/arrow_refresh.png',
			tooltip: 'Refresh',
			text: 'Refresh',
			handler: function() {
				if (me.store) {
					me.store.loadPage(me.store.currentPage);
				}
			}
		},{
			type: 'gear',
			icon: '/images/icons/cog_edit.png',
			tooltip: 'Edit Page',
			text: 'Edit Page',
			handler: function() {
				var win = Ext.create('EXT.DOMAIN.hmp.containers.WidgetTabPanelEditWin');
				win.configure(me);
				win.show();
			}
        }]);
        

        // initalize/create the toolbars and features
    	this.tbar = this.initToolbar(this.tbarConfig);
    	this.bbar = this.initToolbar(this.bbarConfig);
    	this.initFeatures();
    	
    	// initalize component (and detail view)
    	this.callParent();
    	this.setDetailPanel(this.detailType);
    	
    	// compile the title template (if specified) and apply it (if no default title is specified)
    	if (Ext.isString(this.titleTpl) && this.titleTpl != '') {
    		this.titleTpl = new Ext.Template(this.titleTpl, {compiled: true});
    	}
    	this.titleOrig = this.title; // needed later on for saving state.
    	
    	// initalize gridAdvisor (with a default one) if not set.
		if (!Ext.isObject(this.gridAdvisor)) {
			this.gridAdvisor = Ext.create('EXT.DOMAIN.cpe.viewdef.GridAdvisor', this.gridAdvisor || {});
			this.gridAdvisor.grid = this;
		}
		// If columnsConf is found, add it to gridAdvisor.
		if(this.columnsConf)
		{
			this.gridAdvisor.columnsConf = this.columnsConf;
		}	
		
		// attach listeners 
		this.store.on('metachange', this.onMetaChange, this);
		this.store.on('load', this.onLoad, this);
		this.on('patientchange', this.patientchange, this);
		// handlers for exceptions
        this.store.getProxy().on('exception', function(proxy, response, operation, eOpts) {
			Ext.log(response);
            me.body.mask('Component Received an Error. Try Reloading.')
            me.store.removeAll();
        });

		this.store.on('beforeload', function(store, op, eopts) {
		   op.skipErrors = true;
		});
				
	},
	
	// Listener functions  --------------------------------------------------------------/
	onMetaChange: function(store, meta) {
        var me = this;

//        Ext.log({level:'info', stack: false}, Ext.getClassName(this) + "[" + this.viewID + "]" + ".metachanged()");

        // set/update the gridAdvisor if the metadata specifies one.
        if (meta.defaults && meta.defaults['extjs.gridAdvisor']) {
            var gridClass = meta.defaults['extjs.gridAdvisor'];
            if (!me.gridAdvisor || me.gridAdvisor['$className'] != gridClass) {
//                Ext.log({level:'info'}, "creating gridAdvisor " + gridClass);
                me.gridAdvisor = Ext.create(gridClass);
            }
        }
        // If no columns are defined (first load or different viewdef) then reconfigure the grid with columns
        // me.columns did not work. It is buried under me.headerCt.gridDataColumns, at least as far as extJS4.0.7.
        if (!me.headerCt.gridDataColumns || me.headerCt.gridDataColumns.length <= 1 || me.reconfigureColumnsAlways === true) {
            /*
             * reconfigure() is an Ext method to rebuild columns.
             * gridAdvisor should be constructed with any custom user column preferences that have been persisted.
             */
            me.reconfigure(store, me.gridAdvisor.defineColumns(me, meta));
        }

        // sortable or not?
        if (meta.sortable) {
            me.sortableColumns = meta.sortable;
            me.headerCt.sortable = meta.sortable;
        }
    },

	// this is the target of the load event on the store
	onLoad: function(store, records, successful, eOpts) {
//        Ext.log({level:'info', stack: false}, Ext.getClassName(this) + "[" + this.viewID + "]" + ".onLoad()");
		var me = this;

        if (me.body) me.body.unmask();

		// colapseGridIfEmpty
		if (me.collapsible === true && me.collapseGridIfEmpty === true && (me.collapsed != (store.getCount() === 0))) {
			me.toggleCollapse();
		}

		// update the grid title (if any)
		if (Ext.isObject(me.titleTpl) && Ext.isFunction(me.titleTpl.apply)) {
			// Recalculate the template variables and reapply the template
			// TODO: refactor this into a function?
			var data = {
				total : store.getTotalCount(),
				currentPage : store.currentPage,
				pageCount: Math.ceil(store.getTotalCount() / store.pageSize),
				fromRecord: Math.min(store.getTotalCount(), ((store.currentPage - 1) * store.pageSize) + 1),
				toRecord: Math.min(store.currentPage * store.pageSize, store.getTotalCount())
			};
			me.setTitle(me.titleTpl.apply(data));
		}
		
		// Make the smarty-pants column size reactive to the actual data in the store (within reason)
		// I get a bad feeling about this feature. I think we really don't want to do this. -JC
//		if (me.headerCt.gridDataColumns && me.headerCt.gridDataColumns.length > 1) {
//			this.doSmartyPantsResizing(me.headerCt.gridDataColumns, this.store);
//		}
	},	
	
	/*
	 * This was in response to MSTHRE-767, but after experimenting with it, I think it is a bad idea.
	 * I'll leave the code as it is, since it sort-of works, but I don't want to leave it enabled since it is too slow.
	 * If we really want to push forward with it, this logic was correct, but we will need a more efficient way
	 * to manipulate the column sizes. Maybe if we go straight to the rendered DOM table(s).
	 */
//	doSmartyPantsResizing: function(cols, store) {
//		var totalWidth = 0;
//		var maxHeadroom = [];
//		for(var i=0; i < cols.length; i++) {
//			totalWidth += cols[i].getFullWidth();
//			maxHeadroom[i] = 0;
//			store.each(function(rec){
//				var fld = rec.get(this.col.dataIndex);
//				if(fld)
//				{
//					var len = rec.get(this.col.dataIndex).length;
//					if(this.col.dataIndex==="infoBtnUrl")
//					{
//						len = 25;
//					}
//					if(len > this.hd[this.index]) { this.hd[this.index] = len; }
//				}
//			}, {hd: maxHeadroom, col: cols[i], index: i});
//		}
//
//		var mHW = 0;
//		for(key in maxHeadroom)
//		{
//			mHW += maxHeadroom[key];
//		}
//		if(mHW > 0) {
//			for(var i=0; i < cols.length; i++) {
//				cols[i].setWidth(totalWidth * maxHeadroom[i] / mHW);
//				this.headerCt.remove(cols[i]);
//				this.headerCt.insert(i, cols[i]);
//			}
//		}
//		this.getView().refresh();
//	},
	beforeRender: function() {
//        Ext.log(Ext.getClassName(this) + "[" + this.viewID + "]" + ".beforeRender()");
        this.callParent(arguments);

        // apply a pointer cursor to the header
        if (this.collapsible && this.header) {
            this.header.body.setStyle('cursor', 'pointer');
        }
    },
	onBoxReady: function() {
//        Ext.log(Ext.getClassName(this) + "[" + this.viewID + "]" + ".onBoxReady()");
		this.callParent(arguments);

		// tooltip needs its target set after all the elements are created
		if (this.detailCmp && this.detailCmp.setTarget) {
			this.detailCmp.setTarget(this.el);
		}

        if (this.patientAware) {
            this.initPatientContext(); // trigger patientchange event (which in turns calls setViewDef() if pt context is already set
        } else {
        	if(this.viewID){
        		//Do not set the viewID if it's null
        		this.setViewDef(this.viewID);
        	}
        }
	},

	// This is the target of the patientchange listener
	patientchange: function(pid) {
//        Ext.log(Ext.getClassName(this) + "[" + this.viewID + "]" + ".patientchange(" + pid + ")");
		this.pid = pid;
		
		//do not enable pages if invalid patient
		if (this.pid == 0) {
			return true;
		} 

		// update the view IIF its already been rendered/queried
		if (this.curViewID) {
			this.setViewDef(this.curViewID, this.curViewParams);
		} else if (this.viewID && this.rendered) {
			// or load the view for the first time (if we are patient aware.
			this.setViewDef(this.viewID);
		}

		// any current details are now no longer valid.  
		this.clearDetail();
	},

	/*
	 * In response to #MSTHRE-591, we need a way to clear out the old viewdef when setting to a different one.
	 */
	clearViewDef: function()
	{
		me.viewParams = {};
		
	},
	
	/*
	 * Primary mechanism responsible for setting the ViewDef that is rendered into the grid.
	 * Causes the viewdef controller to be queried for data to render into the grid.
	 * 
	 * extraParams are applied in addition to the viewParams specified in the config.
	 * They currently do not over-ride
	 */
	setViewDef: function(view, extraParams, forceReload, graphStoreHandler, chartPanel) {
		var me = this;
		var store = me.getStore();
		

		// calculate/update the current view/params and apply them to the proxy
		var sameView = (me.curViewID===view);
		me.curViewID = view;
		me.curViewParams = Ext.apply({}, extraParams, me.viewParams);
		if (me.pid) {
			// if a patient context is set, ensure its passed through (overriding all other values)
			Ext.apply(me.curViewParams, {'pid': this.pid});
		}

        store.getProxy().url = '/vpr/view/' + me.curViewID;
		store.getProxy().extraParams = Ext.apply(store.getProxy().extraParams, me.curViewParams);
		store.leadingBufferZone = store.pageSize = me.curViewParams['row.count'] || 1000; // TODO: Change back to a reasonable figure when 4.1 and infinite scrolling is confirmed as working. 

		// if we are switching to a new viewdef (or just initalizing the first one)
		if (forceReload || !sameView || (!me.headerCt.gridDataColumns) || me.headerCt.gridDataColumns.length===0) {
			// initalize the current page/limit
			store.currentPage = 1;
			// leading buffer is the essentially 1 page (TODO: probably should be configurable somehow? row.buffer?)
			
			// intitalize grouping (if specified)
			var group = me.curViewParams[store.proxy.groupParam];
			if (group && group!='') {
				this.store.group(group);
			}


			// clear out the store model and grid columns
			this.columns = [];
			store.model.prototype.fields.clear();
//			this.reconfigure(store);
		}

		if(graphStoreHandler) {
			// Ensure the store calls this routine on load.
			store.chartPanel = chartPanel;
			store.on('load', graphStoreHandler);
		}
		// trigger (re)load of the store
		store.load();
	},

	/**
	 * This method creates (or recreates) the detail panel/window/tooltip/etc.
	 * 
	 * It should be safe to call this method multiple times (ie to change the dock type/position dynamically)
	 */
	setDetailPanel: function(type) {
		var me = this;

		// if a detailCmp already exists, destroy it and create a new one (unless its shared/managed externally)
		if (this.detailCmp && this.detailType != 'external') {
			this.removeDocked(this.detailCmp);
			this.detailCmp.destroy();
			this.loader.setTarget(null);
			delete this.detailCmp;
		} 

		// update the detail config (in case its state is saved/queried)
		this.detailType = type;

		// create the detail component
		if (type == 'bottom' || type == 'right') {
			Ext.applyIf(me.detail, {xtype: 'griddetailpanel', cls: 'hmp-panel-detail-' + type, dock: type});
			me.detailCmp = me.addDocked(me.detail)[0];
		} else if (type == 'window') {
			Ext.applyIf(this.detail, {xtype: 'window', modal: true, collapsible: false, closeAction: 'hide'});
			this.detailCmp = Ext.widget(this.detail.xtype, this.detail);
		} else if (type == 'rowbody') {
			var togglefxn = function(model, rec) {
				var node = Ext.get(me.getView().getNode(rec));
				var rowbody = node.next('.x-grid-rowbody-tr');

				var cls = 'x-grid-row-body-hidden';
				if (rowbody.hasCls(cls)) {
					// unhide and move the detailCmp element into the rowbody
					rowbody.removeCls(cls);
					rowbody.insertFirst(this.detailCmp.getEl());

					// resize detailCmp to the rowbody size
					var box = rowbody.getBox();
					this.detailCmp.updateBox(rowbody.getBox());
				} else {
					rowbody.addCls(cls);
				}

				// If Grid is auto-heighting itself, then perform a component layhout to accommodate the new height
				if (!this.isFixedHeight()) {
					this.doComponentLayout();
				}
			}

			// create a detail panel that will be moved inside of the rowbody when shown.
			Ext.applyIf(this.detail, {xtype: 'griddetailpanel', closable: false, autoRender: true, collapsible: false, resizeHandles: 's'});
			this.detailCmp = Ext.widget(this.detail.xtype, this.detail);
			this.detailCmp.show();

			this.on({select: togglefxn, deselect: togglefxn, scope: this});
		} else if (type == 'tooltip' || type == 'tip') {
			// configure and create the detailCmp with the extra tooltip stuff
			// most of the values in detailDefaultCfg don't really apply (except for the loader)
			var detailCfg = {
					collapsible: false,
					collapsed: false,
					target: this.el, // not defined yet, 
					delegate: '.x-grid-cell',
					autoScroll: true,
					autoHide: false,
					anchor: 'bottom',
					closable: true,
					anchorToTarget: true,
					constrainPosition: false,
					hideDelay: 1000,
					showDelay: 750,
					mouseOffset: [0,15],
					loader:  {loadMask: false}
			};
			Ext.apply(this.detail, detailCfg);
			this.detailCmp = Ext.create((type == 'tooltip') ? 'Ext.tip.ToolTip' : 'Ext.tip.Tip', this.detail);

			if (type == 'tooltip') { 
				this.detailCmp.on('show', function updateTipBody(tip) {
					var cellEl = Ext.get(tip.triggerElement);
					var rowEl = cellEl.findParentNode('.x-grid-row');
					var rec = me.view.getRecord(rowEl);
					var col = me.getSelectedColumn(cellEl, rowEl);

					tip.setTitle('Loading...');

					// call the update method to actually load the details
					return me.updateDetailPanel(rec);
				});
			}

		} else if (Ext.isObject(type) && type.isXType && type.isXType('panel')) {
			// an actual panel instance was passed in
			this.detailCmp = type;
			this.detailType = 'external';
		} else if (type == 'shared') {
			/*
			 * Ignore this, it will be set by the container.
			 * TODO: alternately, we could have the vdgp (this) actively find the first detail panel sibling.
			 */
			this.detailCmp = 'shared-XXX'; // TODO: Perhaps we should have a name that it is associated by.
			this.detailType = 'shared';
		} else if (Ext.isString(type) && type !== 'none') {
			// treat type as a string that references a component ID.
			// Since that component may not exist just yet, let the updateDetailPanel attach to it.
			this.detailCmp = type;
			this.detailType = 'external';
		}
			

		// click/select handler that will be registered (if a detail component was created)
		if (this.detailCmp) {
			this.on('select', function(rowModel, rec, idx, e) {
				var orec = me.getActualStoreIndexInsteadOfWhatTheViewFalselyThinksIsCorrectDueToExtJsGroupingBug(idx);
				me.updateDetailPanel(orec);
			}, this);
		}
	},

	/*
	 * Based on the currently selected row, gets the Grid Column assocated with
	 * the specified HTML element.  This element must be the <TD> element that implemented the .x-grid-cell style.
	 */
	getSelectedColumn: function(el, rowEl) {
		rowEl = rowEl || this.view.getSelectedNodes()[0];
		var cols = Ext.DomQuery.select('.x-grid-cell', rowEl);

		// somewhat hacky way to get the column/row index we are hovered over.
		for(var i=0; i < cols.length; i++) {
			if(cols[i] === el) {
				return this.view.headerCt.getHeaderAtIndex(i);
			}
		}
	},
	
	
	// Detail Handling Functions ---------------------------------------------------------------/
	
	/* Resolves the detail component (if its a itemId reference) and returns it.
	 * If there is no detail component specified (detailType=none) then this will return null.
	 */
	getDetailPanel: function() {
		var me = this;
		
		// if the detail is still a string, attempt to resolve it into a component
		if (me.detailCmp && Ext.isString(me.detailCmp)) {
			if(me.detailType == 'shared') {
				var parent = this.ownerCt;
				if(parent)
				{
					for(var i = 0; i<parent.items.length; i++)
					{
						var itm = parent.items.items[i];
						if(itm.isXType && itm.isXType('griddetailpanel') /* && itm.id == this.detailCmp */)
						{
							this.detailCmp = itm;
						}	
					}	
				}	
			}
			else {
				var items = Ext.ComponentQuery.query(me.detailCmp);
				me.detailCmp = (items.length > 0) ? items[0] : null;
			}
		}
		
		return me.detailCmp;
	},
	
	clearDetail: function() {
		var me = this, detailCmp = this.getDetailPanel();
		if (!detailCmp) {
			return;
		}
		
		detailCmp.setTitle(detailCmp.emptyTitle || 'Detail:');
		detailCmp.update(detailCmp.emptyHTML || 'No record selected');

		// if autoCollapse == true, then collase the details
		if (detailCmp.collapsible === true && detailCmp.autoCollapse === true && detailCmp.rendered === true) {
			detailCmp.collapse();
		}
	},
	
	/*
	 * When a row is selected (or unselected) this method is called to update the detail panel (if any)
	 * if comp and/or rec is null, then it means a record was unselected (also occurs when a next page is selected)
	 */
	updateDetailPanel: function(rec, extraParams) {
		var me = this, detailPanel = this.getDetailPanel();
		
		// ensure the loader is pointing at the correct target and calculate the params
		var loader = me.getLoader();
		loader.setTarget(detailPanel);
		detailPanel.loader = loader;
		loader.loadDetails(this, rec);
		
		// if the target is a GridDetailPanel, bind the current grid + selected records to it
		if (detailPanel.isXType('griddetailpanel')) {
			detailPanel.bindGrid(me, rec);
		}
		
		return true;
	},

	getDetailTitle: function(rec) {
		var me = this;

		// if a detail title template exists and is uncompiled, compile it
		if (me.detailTitleTpl && !Ext.isObject(me.detailTitleTpl)) {
			me.detailTitleTpl = new Ext.XTemplate(me.detailTitleTpl);
		}

		// determine the title and set it.
		var title = me.detailTitle || 'Detail';
		if (me.detailTitleTpl && rec) {
			title = me.detailTitleTpl.apply(rec.data);
		} else if (me.detailTitleField) {
			title = rec.get(me.detailTitleField);
		}
		return title;
	},

	// TODO: Somehow the GridAdvisor will eventually play into this.
	getEditor: function() {
		if (Ext.isObject(this.editorCmp)) {
			return this.editorCmp;
		} else if (Ext.isString(this.editorCmp)) {
			this.editorCmp = Ext.create(this.editorCmp);
			this.editorCmp.setEditorValues(this.getState());
			return this.editorCmp;
		}
		return null;
	},    

	getState: function() {
		// default values, only include these in the results if the current value != the default
		// TODO: is there a better way? Can the defaults be stored seperately and referenced instead of re-declared here?
		var defaults = {
				collapseGridIfEmpty: true,
				collapsible: false,
				grouping: true,
				hideHeaders: false,
		}

		var ret = {
				xtype: this.getXType(),
				// TODO: should this be the defined/declared view+params or the current/effective view+params?
				viewID: this.viewID,
				viewParams: this.viewParams,
				title: this.titleOrig,
				titleTpl: (Ext.isObject(this.titleTpl) ? this.titleTpl.html : this.titleTpl),
				groupHeaderTpl: this.groupHeaderTpl,
				detailType: this.detailType,
				tabConfig: {
					tooltip: (this.tabConfig && this.tabConfig.tooltip) ? this.tabConfig.tooltip : null
				},
				detail: this.detail
				/**
				 * 4-19-2012 JC: When tooltip is chosen, somehow the detail gets set to type "none" and no width/height or anything else.
				 */
		};

		this.setFieldValueIfExists(ret, this, 'flex');
		this.setFieldValueIfExists(ret, this, 'region');
		
		/*
		 * Ideally I would like to delegate these properties to the layout, but I can't think of a clean way to do this.
		 */
		this.setFieldValueIfExists(ret, this, 'height');
		this.setFieldValueIfExists(ret, this, 'width');
		this.setFieldValueIfExists(ret, this, 'gridX');
		this.setFieldValueIfExists(ret, this, 'gridY');
		this.setFieldValueIfExists(ret, this, 'weightX');
		this.setFieldValueIfExists(ret, this, 'weightY');
		this.setFieldValueIfExists(ret, this, 'widthX');
		this.setFieldValueIfExists(ret, this, 'widthY');
		
		// apply any non-default values
		for (var key in defaults) {
			if (this[key] !== defaults[key]) {
				ret[key] = this[key]
			}
		}

		/*
		 * Storing in a special field tbarConfig will be analysed by initComponent when the component is being constructed from stored state fields.
		 */
		this.setFieldValueIfExists(ret, this, 'tbarConfig');
		this.setFieldValueIfExists(ret, this, 'bbarConfig');

		/*
		 * Likewise, we want to pack grouped field(s) into a custom field that will be analysed during initFeatures when the component is constructed.
		 */
		if(this.features.length>0)
		{
			for(var key in this.features)
			{
				var feature = this.features[key];
				if(feature.ftype=='viewdefgrouping' && feature.lastGroupField!='' && !Ext.isEmpty(feature.lastGroupField))
				{
					if(!ret.viewParams)
					{
						ret.viewParams = {'group':feature.lastGroupField};
					}
					else
					{	
						ret.viewParams.group = feature.lastGroupField;
					}
//					ret.grouping = true;
				}
			}	
		}

		/*
		 * Column sequence, size, hide/show
		 */
		var columnsConf = [];
		for(key in this.headerCt.gridDataColumns)
		{
			var col = this.headerCt.gridDataColumns[key];
			columnsConf.push({text: col.initialConfig.text || col.text, width: col.width, hidden: col.hidden});
		}
		if(columnsConf.length>0)
		{
			ret.columnsConf = columnsConf;
		}

		/*
		 * Sorting; This is stored in the datastore used by the view, the columns have nothing to do with it.
		 */
		if(this.store.sorters && this.store.sorters.items && this.store.sorters.items.length>0)
		{
			var sorters = [];
			for(var i = 0; i<this.store.sorters.items.length; i++)
			{
				var sort = this.store.sorters.items[i];
				sorters.push({property: sort.property, direction: sort.direction});
			}
			ret.store = {'sorters': sorters};
		}	


		// record current detail panels height/width (if any/applicable)
		if (this.detailCmp && this.detailCmp.rendered) {
			if (ret.detailType != 'right') ret.detail.height = this.detailCmp.height;
			if (ret.detailType != 'bottom') ret.detail.width = this.detailCmp.width;
		}

		return ret;
	},

	/**
	 * Gently set values only if the field actually exists.
	 * This is a hack to avoid setting values that EXT thinks were set explicitly.
	 */
	setFieldValueIfExists: function(dest, src, fldName)
	{
		if(!Ext.isEmpty(src[fldName]))
		{
			dest[fldName] = src[fldName];
		}	
	},

	applyState: function(state) {
		// TODO: Implement me
	},
	
	/**
	 * Here's a workaround that was needed for detail selection, and also for tooltipping on infobu'uhns.
	 * Full explanation of bug and workaround here: http://www.sencha.com/forum/archive/index.php/t-119520.html
	 */
	getActualStoreIndexInsteadOfWhatTheViewFalselyThinksIsCorrectDueToExtJsGroupingBug: function(dex) {
		var allItems = [];
		var groupedItems = this.getStore().getGroups();
		for (var i=0; i<groupedItems.length; i++) {
			allItems = allItems.concat(groupedItems[i].children);
		}
		return allItems[dex];
	}
});

