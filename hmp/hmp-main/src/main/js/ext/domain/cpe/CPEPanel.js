/**
 * This is the main panel of the CPE app.
 *
 * It has two major components (patient picker and patient view) and
 * coordinates the interaction between the two.
 *
 * Examples:
 * - Lack of a patient context will disable/mask everything
 * - selecting a patient on a roster or panel will change the main tab configuration (user configurable)
 * - user preferences are applied where necessary
 * - keybord shortcuts/navigation handled in here as well
 */
Ext.define('org.osehra.cpe.CPEPanel', {
    extend: 'Ext.panel.Panel',
    requires: [
        'org.osehra.cpe.roster.TriStatePanel',
        'org.osehra.hmp.AppContext',
        'org.osehra.hmp.UserContext',
	    'org.osehra.cpe.patient.PatientBanner',
        'org.osehra.cpe.roster.PatientPicker',
        'org.osehra.cpe.roster.PatientPickerHotspot',
	    'org.osehra.hmp.containers.WidgetTabPanel',
        'org.osehra.cpe.viewdef.ViewDefGridPanel',
        'org.osehra.cpe.PagePicker',
	    'org.osehra.cpe.search.SearchBox',
        'org.osehra.cpe.search.SearchPanel',
        'org.osehra.hmp.util.Animation',
        'org.osehra.cpe.roster.RosterWindow',
        'org.osehra.cpe.order.QoItemListWindow' // this one probably isn't in the correct spot.
        //'org.osehra.cpe.patient.PatientSelectorPanel'       
    ],
    alias: 'widget.cpepanel',
    mixins: {
        patientaware: 'org.osehra.hmp.PatientAware'
    },
    cls: 'hmp-cpe-panel',
    flex: 1,
    layout: 'border',
	trackTitle: true, // if true, track the window title to the currently selected patient name
    pickerPinned: false, // is the picker pinned open by default (user preference)
    pickerRegion: 'north', // which border should the patient picker be rendered at (user preference)
    enableMask: false, // enables masking mode between picker and panel
    enableHash: true, // false to disable browser forward/backward for patient navigation TODO: maybe use Ext.util.History for this?
    enableEdit: true,
    animationTrigger: 'mouseover',
    animationDelay: 500,
    listeners: {
        patientchange: function(pid) {
//            Ext.log(Ext.getClassName(this) + ".patientchange(" + pid + ")");

            this.pid = pid;

            if (this.pid == 0) {
                // show no selected patient card
                this.ptcards.getLayout().setActiveItem(0);
            	return true;
            }
            
            // show selected patient card
            this.ptcards.getLayout().setActiveItem(1);
            
			if (this.trackTitle) {
				var patinfo = org.osehra.hmp.PatientContext.getPatientInfo();
			    document.title = patinfo.fullName + " (" + patinfo.age + "yo " + patinfo.gender + ")";
		    }	

            return true;
        }
    },
    items: [
        {
        	xtype: 'tristatepanel',
        	region:'center'
        }
    ],
    statics: {
    	selectedTabPanelRecord: null
    },
    initComponent: function() {
//        Ext.log(Ext.getClassName(this) + ".initComponent()");
        var me = this;

        // translate to shorter javascript keys that the components expect
        // TODO: consider renaming the user prefs so they don't have to be translated!
        var userPrefs = org.osehra.hmp.UserContext.getUserPrefs();
        userPrefs = {
            pickerRegion: userPrefs['cpe.patientpicker.loc'],
            pickerPinned: userPrefs['cpe.patientpicker.pinned'],
            animationTrigger: userPrefs['cpe.patientpicker.animateOption'],
            animationDelay: userPrefs['cpe.patientpicker.animateDelaySeconds'],
            rosterID: this.rosterID || userPrefs['cpe.patientpicker.defaultRosterID'],
            rememberLastPID: userPrefs['cpe.patientpicker.rememberlast'],
            enableMask: userPrefs['cpe.patientpicker.mask'] === true || userPrefs['cpe.patientpicker.mask'] === 'true',
            enableHash: userPrefs['cpe.patientpicker.hash'] === true || userPrefs['cpe.patientpicker.hash'] === 'true',
            enableEdit: userPrefs['cpe.editmode'] === true || userPrefs['cpe.editmode'] === 'true'
        };
		
        Ext.apply(this, userPrefs);
        this.items[0].rosterID = this.rosterID;
		
        this.callParent(arguments);

   },

//   initPicker: function() {
//	   var picker = 
//	   {
//	         xtype: 'patientpicker',
//	         itemId: 'PatientPickerID',
//	         ui: 'gadget',
//	         title: 'Patient Selection',
//	         frame: true,
//	         header: true,
//	         margin: '0 0 6 0',
//	         rosterViewDef: 'org.osehra.cpe.vpr.queryeng.RosterViewDef'
//	   };
//       Ext.apply(picker, {
//           rosterID: this.rosterID,
//           region: this.pickerRegion,
//           pinned: this.pickerPinned
//       });
//
//       // configure the patientpicker component
//       var anime = false;
//       if(this.pickerRegion === 'west' || this.pickerRegion === 'east' || this.pickerRegion === 'south' || this.pickerRegion === 'north') {
//           if(this.pickerPinned == true || this.pickerPinned == 'true') {
//        	   var hpos = 'left';
//        	   picker.pinned = true;
//               if(this.pickerRegion === 'west' || this.pickerRegion === 'east'){
//                   if(this.pickerRegion === 'east'){
//                       hpos = 'right';
//                   }
//                   Ext.apply(picker, {height: '100%', width: '30%', headerPosition: hpos})
//               }
//               else {
//            	   picker.height = '30%';
//            	   picker.width = '100%';
//               }
//               // Don't animate; Just dock in the appropriate border.
//               picker.collapsible = true;
//               picker.collapsed = false;
//               picker.title = 'Patient List';
//               this.ptpicker = Ext.create('org.osehra.cpe.roster.PatientPicker', picker);
//               this.insert(0, this.ptpicker);
//           } else {
//               var target = {
//                   xtype: 'pphotspot',
//                   itemId: 'PatientAnime-'+this.pickerRegion,
//                   region: this.pickerRegion,
//                   hidden: false
//               };
//               Ext.apply(picker, {
//                   region: this.pickerRegion,
//                   expandHeight: 800,
//                   collapsible: false,
//                   collapsed: false,
//                   resizable: false,
//                   pinned: true, tools: []});
//               var growParms = {growHorizontal: false, growVertical: true};
//               if(this.pickerRegion === 'east' || this.pickerRegion === 'west') {
//                   target.width = 20;
//                   var growParms = {growHorizontal: true, growVertical: false};
//               } else {
//                   target.height = 20;
//               }
//               var startRelativeToX = 0;
//               var startRelativeToY = 0;
//               if(this.pickerRegion === 'east') {
//                   startRelativeToX = 1;
//               }
//               if(this.pickerRegion === 'south'){
//                   startRelativeToY = 1;
//               }
////               this.items = Ext.Array.insert(this.items, 0, target);
//               target = this.insert(0, target);
//               anime = org.osehra.hmp.util.Animation.decorateComponent(target, picker, this, growParms, startRelativeToX, startRelativeToY, this.animationTrigger, this.animationDelay);
//               this.ptpicker = anime.getAnimComponent();
//           }
//       } else if (this.pickerRegion === 'window') {
//           // pull default picker config out of items array
//           this.items = Ext.Array.erase(this.items, 0, 1);
//
//           var winsize = Ext.getBody().getViewSize();
//
//           // region='window' is special case that must alter some of the patientpicker
//           // config and be nested in a window with tools
//           this.ptpickerwin = Ext.create('Ext.window.Window', {
//               itemId: 'PatientPickerWinID',
//               modal: true,
//               closeAction: 'hide',
//               minHeight: 600, minWidth: 800,
//               height: winsize.height * 0.75,
//               width: winsize.width * 0.95,
//               layout: 'fit',
//               title: 'Patient Selection',
//               iconCls: 'groupIcon',
//               items: [
//                   Ext.apply(picker, {
//                       header: false,
//                       frame: false,
//                       collapsible: false,
//                       collapsed: false,
//                       resizable: false,
//                       tools: []
//                   })
//               ]
//           });
//       }
//
//
//       // convenient references
//       if(this.ptpicker==null) {
//    	   this.ptpicker = this.pickerRegion === 'window'? this.ptpickerwin.down('patientpicker') : this.down('patientpicker');
//       }
//       
//       this.ptcards = this.down('#PatientCardsID');
//       this.ptview = this.down('#PatientViewID');
//       this.ptpanel = this.down('#PatientPanelID');
//
//       if (anime) {
//           this.ptpicker.on('selectpatient', anime.immediateClose, anime)
//       }
//       
//       var me = this;
//
//       // event handlers
//       me.ptpicker.on('selectpatient', me.onSelectPatient, me);
//       me.down('pagepicker').on('change', me.onPageChange, me);
//
//       var tabnavfxn = function(e) {
//           if (me.ptpanel.isXType('tabpanel')) {
//               var tab = me.ptpanel.getActiveTab();
//               tab = (e.ctrlKey == true || e.keyCode == e.HOME) ? tab.previousSibling() : tab.nextSibling();
//               if (tab) {
//                   me.ptpanel.setActiveTab(tab);
//               }
//           }
//       }
//
//       // keyboard shortcuts/nav
//       this.nav = Ext.create('Ext.util.KeyNav', Ext.getDoc(), {
//           scope: this,
//           'esc': this.showPicker,
//           'tab': tabnavfxn,
//           'home': tabnavfxn,
//           'end': tabnavfxn,
//           'pageUp': function() {me.prevbtn.handler(me.prevbtn)},
//           'pageDown': function() {me.nextbtn.handler(me.nextbtn)}
//       });
//  
//   },
   
   initPickerWindow: function() {
	   
	   var winsize = Ext.getBody().getViewSize();

       // region='window' is special case that must alter some of the patientpicker
       // config and be nested in a window with tools
       this.ptpickerwin = Ext.create('Ext.window.Window', {
           itemId: 'PatientPickerWinID',
           modal: true,
           closeAction: 'hide',
           minHeight: 600, minWidth: 800,
           height: winsize.height * 0.75,
           width: winsize.width * 0.95,
           layout: 'fit',
           title: 'Patient Selection',
           iconCls: 'groupIcon',
           items: [{
        	   xtype: 'patientpicker',
        	   itemId: 'PatientPickerID',
        	   ui: 'gadget',
        	   title: 'Patient Selection',
        	   frame: true,
        	   header: true,
        	   margin: '0 0 6 0',
        	   rosterViewDef: 'org.osehra.cpe.vpr.queryeng.RosterViewDef',
        	   header: false,
	           frame: false,
	           collapsible: false,
	           collapsed: false,
	           resizable: false,
	           tools: [],
	           rosterID: this.rosterID,
	           region: this.pickerRegion,
	           pinned: this.pickerPinned
			}]
       });
       
       var winpick = this.ptpickerwin.down('patientpicker');
       
       winpick.on('selectpatient', this.onSelectPatient, this);
       
       
   },
   
   initPickerShortcutKeys: function() {
	   var me = this;
	   // keyboard shortcuts/nav
	     var tabnavfxn = function(e) {
		     if (me.ptpanel.isXType('tabpanel')) {
		         var tab = me.ptpanel.getActiveTab();
		         tab = (e.ctrlKey == true || e.keyCode == e.HOME) ? tab.previousSibling() : tab.nextSibling();
		         if (tab) {
		             me.ptpanel.setActiveTab(tab);
		         }
		     }
	     };
	     
	     this.nav = Ext.create('Ext.util.KeyNav', Ext.getDoc(), {
           scope: this,
           'esc': this.showPicker,
           'tab': tabnavfxn,
           'home': tabnavfxn,
           'end': tabnavfxn,
           'pageUp': function() {me.prevbtn.handler(me.prevbtn)},
           'pageDown': function() {me.nextbtn.handler(me.nextbtn)}
       });
   },

    onPageChange:function (combobox, newPanelId, oldPanelId) {
        var me = this;

        var record = combobox.getStore().findRecord('code', newPanelId);
        var configURL = record.get('url');
        if (configURL) {
            var panelcomp = 'widgettabpanel';
            var panelconfig = configURL;

            // TODO: switch the component in addition to the config?
            if (this.ptpanel.getXType() !== panelcomp || this.ptpanel.configName !== panelconfig) {
                // remove the old panel
                if (this.ptpanel) {
                    this.ptview.remove(this.ptpanel, true);
                    this.ptpanel = null;
                }

                // add the new component (disabled)
                this.ptpanel = this.ptview.add({
                    xtype: panelcomp,
                    region:'center',
                    editMode:this.enableEdit,
                    itemId:'PatientPanelID'
                });

                // TODO: Some panels don't have a config url, should this be moved into widgettabpanel?
                Ext.Ajax.request({
                    url:panelconfig,
//                scope: org.osehra.cpe.CPEPanel.selectPatient,
                    success:function (resp) {
                        var cfg = Ext.JSON.decode(resp.responseText);
//                    console.log(cfg);
                        me.ptpanel.applyState(cfg);
                        me.ptpanel.configName = panelconfig;

                        // update the patient id hash on the URL (if enabled)
//                    if (this.enableHash) location.hash = pid;
                    },
                    failure:function (response, opts) {
                        Ext.log('CPEPanel.selectPatient.Ext.Ajax.request.failure() :  server-side failure with status code ');
                    }
                });

                // TODO: toggle the correct tab?
                //var colmap = rec.data.
            }

            org.osehra.cpe.CPEPanel.selectedTabPanelRecord = record;

            Ext.Ajax.request({
                url: '/app/context',
                method: 'POST',
                params: {
                    panelId: newPanelId
                },
                failure: function(response) {
                    Ext.log('boo');
                }
            });
        }
    },
    showRosterEditor: function() {
        var me = this;
        if (!me.rosterwin) {
            me.rosterwin = Ext.create('org.osehra.cpe.roster.RosterWindow');
        }
        me.rosterwin.show();
    },

    showPicker: function() {
        var me = this;
//        Ext.log(Ext.getClassName(me) + ".showPicker(" + rosterID + ")");

        // show patient window or expand window.
        if (me.ptpickerwin) {
            me.ptpickerwin.show();
        } else if (me.ptpicker.collapsed) {
            me.ptpicker.expand();
        }

        // must be slightly delayed for the focus to work.
        Ext.defer(function() {me.ptpicker.patientgrid.getView().focus()}, 250, this);
    },
    onSelectPatient: function(pid) {
        var me = this;

        // try to populate the correct context by using any session/cookie defaults.
        // TODO: enhance this.  Keep server side in session.  Add tabtarget and/or current/active tab.
        // TODO: Ignote patient checks on reload if same patient?
        // TODO: Also, how to generalize this so it could be parsed from the URL too (#pid|roster|panel|config? etc.)?

        // close/hide the window if it exists
        if (this.ptpickerwin) {
            this.ptpickerwin.hide();
        }

        this.refreshPickerHotspots();

        // update next/prev buttons (if any)
        if (this.nextbtn && this.prevbtn && patRec) {
            // store is ext4.0, stores is ext4.1
            var store = (Ext.isArray(patRec.stores)) ? patRec.stores[0] : patRec.store;
            var idx =  store.indexOf(patRec);
            var prevRec = store.getAt(idx-1);
            var nextRec = store.getAt(idx+1);

            if (prevRec) {
                this.prevbtn.enable();
                this.prevbtn.idx = idx-1;
                this.prevbtn.setTooltip(prevRec.get('familyName') + ", " + prevRec.get('givenNames'));
            } else {
                this.prevbtn.disable();
            }

            if (nextRec) {
                this.nextbtn.enable();
                this.nextbtn.idx = idx+1;
                this.nextbtn.setTooltip(nextRec.get('familyName') + ", " + nextRec.get('givenNames'));
            } else {
                this.nextbtn.disable();
            }
        }

        if (pid !== this.pid) {
            // initate PatientAware context change (if its different)
            // update the patient id hash on the URL (if enabled)
//            if (this.enableHash) location.hash = pid;
            this.setPatientContext(pid);
        }
    },
    refreshPickerHotspots: function() {
//        Ext.log(Ext.getClassName(this) + ".refreshPickerHotspots()");
        var hotspots = Ext.ComponentQuery.query('pphotspot');
        if (hotspots && hotspots.length > 0) {
            for (var i = 0; i < hotspots.length; i++) {
                hotspots[i].refreshHeaderText();
            }
        }
    },
    afterRender: function() {
        var me = this;
        me.callParent(arguments);
//        Ext.log(Ext.getClassName(me) + ".afterRender()");

        // listen for focus changes between the components (experimental)
        if (this.enableMask && this.ptpicker && this.ptpicker.body) {
            // TODO: there is an issue here with excessive mask/unmasking as the cursor moves that pegs the browser
            // I tried using a delayed/buffered event but it doesn't quite work as planned.
            this.ptpicker.body.on('mouseover', function() {
            	if(me.ptview.body && me.ptview.getDockedComponent(1)){
            		me.ptview.body.mask();
            		me.ptview.getDockedComponent(1).disable();
            	}
            });
            this.ptpicker.body.on('mouseout', function() {
                if (me.pid>0 && me.ptview.body && me.ptview.getDockedComponent(1)) {
                    me.ptview.body.unmask();
                    me.ptview.getDockedComponent(1).enable();
                }
            });
            this.ptview.getEl().on('mouseover', function() {
                if (me.pid>0 && me.ptview.body) {
                    me.ptpicker.body.mask();
                }
            });
            this.ptview.getEl().on('mouseout', function() {
            	if(me.ptview.body)
            		me.ptpicker.body.unmask();
            });
        }

        this.refreshPickerHotspots();
    },
	onBoxReady: function() {
		this.callParent(arguments);
		this.down('tristatepanel').initPatientPickerListeners();
		this.ptcards = this.down('#PatientChart');
		this.ptview = this.down('#PatientViewID');
		this.ptpanel = this.down('#PatientPanelID');
		this.initPickerWindow();
		this.initPickerShortcutKeys();
	}
});
