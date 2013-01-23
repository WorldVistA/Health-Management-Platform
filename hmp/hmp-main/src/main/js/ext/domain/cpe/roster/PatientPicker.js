/**
 * This component is the primary mechanism for patient selection.  It essentially consists of two 
 * main components, a tree view that lists all the rosters, and a grid view that lists the patients
 * on the selected roster.
 * 
 * It can be docked top,bottom,east,west (or as a window) and adjusts its render/collapsing appropriately.
 * 
 * TODO: Need to figure out what to do with the patient search results
 */
Ext.define('org.osehra.cpe.roster.PatientPicker', {
	//id: 'patientPicker',
    extend: 'Ext.panel.Panel',
    alias: 'widget.patientpicker',
    requires: [
        'org.osehra.cpe.multi.MultiPatientBanner',
        'org.osehra.cpe.viewdef.ViewDefGridPanel',
        'org.osehra.cpe.roster.RosterPanel',
        'org.osehra.hmp.EventBus'
    ],
    mixins: {
        patientaware: 'org.osehra.hmp.PatientAware'
    },
	layout: 'border',

	frame: false,
	collapsed: true,
	collapsible: true,
	animCollapse: false,
	titleCollapse: true,
	collapseMode: 'header',
	collapseFirst: false,
	header: false,
	pinned: "false", // should the picker auto-collapse after a patient is selected?
	
	resizable: true,
	height: 150,
	minHeight: 185,
	minWidth: 300,
    cls: 'hmp-section-foo',
	
	rosterID: null, // the currently selected roster record (if set becomes the default)
	rosterPanel: null, // the panel target (component) for the currently selected roster.
	rosterViewDef: null, // the current viewdef for the patient list (if set becomes the default)

    items: [
        {
            xtype: 'multiptbanner',
            region: 'north'
        },
        {
            xtype: 'viewdefgridpanel',
            region: 'center',
            header: false,
            forceFit: true,
            patientAware: false,
            selType: 'rowmodel',
            reconfigureColumnsAlways: true, // Added for MSTHRE-591; Switching patient list types fails unless we always reconfig columns.
            scroll: false,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'PatientGroup',
                    enableDrop: false
                },
                style: { overflow: 'auto', overflowX: 'hidden' }
            }
        }
    ],
    dockedItems: [
        {
            xtype: 'rosterpanel',
            hidden:true
        }
    ],
    listeners: {
        beforepatientchange: function(pid) {
            // TODO: find out why this doesn't work in Ext4.1
            //this.ptview.disable();
        },
        patientchange: function(pid) {
            this.pid = pid;
            if (this.pid == 0) {
                return true;
            }

            // collapse the picker unless pinned open
            if (!this.pinned) {
                this.collapse();
//                this.doLayout();
            } else {
                this.expand();
            }

            this.refreshSelection();

            return true;
        }
    },
	initComponent: function() {
		var me = this;
		if(me.maxWidth) {me.items[1].maxWidth = me.maxWidth;}
		me.addEvents('selectpatient');

		this.callParent();
		
		me.patientgrid = me.down('viewdefgridpanel');

		if(me.rosterViewDef)
		{
			me.patientgrid.setViewDef(me.rosterViewDef);
		}
		
		// handle the key press events to keep navigation keys from actually selecting a patient until enter is pressed.
		me.patientgrid.getView().on('itemkeydown', function(view, rec, item, idx, e, opts) {
			if (e.getKey() == e.ENTER) {
				me.onSelectPatient(view, rec, item, idx, e, opts)
			} else if (e.isNavKeyPress()) {
				return false;
			} else {
				// TODO: how to have J/K be equivalent to UP/DOWN?
				// TODO: typing anything else automatically does patient search?
			}
		});
		
		// setup patient list column listeners
		me.patientgrid.getView().on('itemclick', me.onSelectPatient, me);
		me.patientgrid.getView().on('refresh', me.refreshSelection, me);

        // handler for patient search text box
//        me.down('#patientsearchfield').on('change', me.doSearch, me, {buffer: 500});

        org.osehra.hmp.EventBus.on('nextPatient', me.navNext, me);
        org.osehra.hmp.EventBus.on('prevPatient', me.navPrevious, me);

        if (me.rosterID) {
            var rosterpicker = me.down('favrosterpicker');
            rosterpicker.on('load', function() {
                rosterpicker.setValue(me.rosterID);
                rosterpicker.doRosterSelection(rosterpicker, rosterpicker.getStore().getById(me.rosterID));
            });
        }
    },
    onBoxReady:function() {
        this.callParent(arguments);
        this.initPatientContext();
//        if(this.compactOrientation || (this.pinned === 'true' && (this.region === 'east' ||  this.region === 'west')))
//		{
//        	this.reconfigurePickerHeaderForCompactOrientation();
//		}
    },
    beforeRender:function () {
//        Ext.log(Ext.getClassName(this) + ".beforeRender()");

        this.callParent(arguments);

        // apply a pointer cursor to the header
        if (this.header && this.header.body) {
            this.header.body.setStyle('cursor', 'pointer');
        }
    },
	onSelectPatient: function(view, rec, item, rowidx, evt, opts) {
		if(evt && evt.target && evt.target.tagName=="A") {
			return; // If this came from an anchor link, we don't want to change patient context.
		}
		// the rec is really the most important thing.  Get it from the store if it wasn't passed in.
		rec = rec || this.patientgrid.getStore().getAt(rowidx);
		var tabtarget, me = this;
		var pid = rec.data.pid;
		if(!pid) {
			pid = rec.data.vprid;
		}	
		
		// TODO:there is a weird bug here where switching views causes the previous row/column to trigger this.
		// not all rows have a pid (search results for example)
		if (pid) {
			// TODO: tabtarget doesn't really work yet.
			//if (this.columns[colidx]) {
			//	tabtarget = this.columns[colidx].dataIndex;
			//}
			// fire a patient selected event that the parent component must listen for.	
			this.fireEvent('selectpatient', pid, rec, me.rosterID, null, me.rosterPanel, tabtarget);
		}
		if(this.isVisible()) {
	        this.firePatientNavEvent();
		}
	},
	doSearch: function(field, searchStr) {
		var grid = this.patientgrid;
		
		// update the window title (if we are in a windoww)
		if (searchStr != '' && this.ownerCt.isXType('window')) {
			this.ownerCt.setTitle('Patient Search Results: ' + searchStr);
		}
		
		// if the seach string is empty, go back to current roster 
		if (searchStr == '' && this.rosterViewDef && this.rosterID) {
			grid.setViewDef(this.rosterViewDef, {'roster.ien': this.rosterID});
		} else if (searchStr && searchStr.length >= 3) {
			grid.setViewDef('org.osehra.cpe.vpr.queryeng.RosterViewDef', {search: searchStr})
		}
	},
	/*
	 * Sometimes the PID is programmatically set and loaded elsewhere.
	 * Let's make sure the correct row is shown as selected in the table.
	 * In most cases this will be correct without a change.
	 *
	 * @private
	 */
    refreshSelection:function () {
//        Ext.log(Ext.getClassName(this) + ".refreshSelection()" + "pid = " + this.pid);
        var grid = this.down('viewdefgridpanel');
        if (grid) {
            var sel = grid.getSelectionModel().getSelection();
            if (sel && sel.length > 0) {
                if (this.pid == 0) {
                    grid.getSelectionModel().deselectAll(true);
                    return;
                } else if (sel[0].get('pid') === this.pid) {
                    return;
                }
            }
            var store = grid.getStore();
            for (var i = 0; i < store.getCount(); i++) {
                if (store.getAt(i).get('pid') === this.pid) {
                    grid.getSelectionModel().select(store.getAt(i));
                }
            }
        }
    },
	hasNextItem: function() {
		var grid = this.down('viewdefgridpanel');
		if(grid)
		{
			var sel = grid.getSelectionModel().getSelection();
			if(sel && sel.length>0)
			{
				var sdex = grid.getStore().indexOf(sel[0]);
				var count = grid.getStore().getCount();
				for(++sdex;sdex<count;sdex++)
				{
					var nrec = grid.getStore().getAt(sdex);
					if(nrec.get('pid'))
					{
						return true;
					}	
				}
			}	
		}	
		return false;
	},
	hasPrevItem: function() {
		var grid = this.down('viewdefgridpanel');
		if(grid)
		{
			var sel = grid.getSelectionModel().getSelection();
			if(sel && sel.length>0)
			{
				var sdex = grid.getStore().indexOf(sel[0]);
				for(--sdex;sdex>=0;sdex--)
				{
					var nrec = grid.getStore().getAt(sdex);
					if(nrec.get('pid'))
					{
						return true;
					}	
				}	
			}
		}
		return false;
	},
	navNext: function() {
		if(this.isVisible()) {
			var grid = this.down('viewdefgridpanel');
			var mdl = grid.getSelectionModel();
			var sel = mdl.getSelection()[0];
			var seldex = grid.getStore().indexOf(sel);
			for(++seldex;seldex<grid.getStore().getCount();seldex++)
			{
				var nrec = grid.getStore().getAt(seldex);
				if(nrec.get('pid'))
				{
					mdl.select(seldex);
					grid.getView().focusRow(seldex);
					this.onSelectPatient(null, null, null, seldex, null, null);
					return;
				}	
			}
		}
	},
	navPrevious: function() {
		if(this.isVisible()) {
	        var me = this;
			var grid = this.down('viewdefgridpanel');
			var mdl = grid.getSelectionModel();
			var sel = mdl.getSelection()[0];
			var seldex = grid.getStore().indexOf(sel);
			for(--seldex;seldex>=0;seldex--)
			{
				var nrec = grid.getStore().getAt(seldex);
				if(nrec.get('pid'))
				{
					mdl.select(seldex);
					grid.getView().focusRow(seldex);
					this.onSelectPatient(null, null, null, seldex, null, null);
	                return;
				}	
			}
		}
    },
    firePatientNavEvent:function () {
        var me = this;
        org.osehra.hmp.EventBus.fireEvent('patientNav', me, {
            hasNext:me.hasNextItem(),
            hasPrev:me.hasPrevItem()
        });
    }
});
