Ext.define('org.osehra.cpe.roster.TriStatePanel', {
    extend:'Ext.panel.Panel',
    requires:[
        'org.osehra.hmp.SegmentedButton',
        'org.osehra.cpe.patient.PatientBanner',
        'org.osehra.cpe.roster.PatientPicker',
        'org.osehra.cpe.PagePicker',
        'org.osehra.cpe.roster.PatientChart'
    ],
    alias:'widget.tristatepanel',
    dockedItems: [
        {
            xtype:'toolbar',
            dock:'top',
            cls:'hmp-tristate-toolbar',
            items: [
                {
                    xtype:'segmentedbutton',
                    allowDepress:true,
                    items:[
                        {
                            text:'Board',
                            itemId: 'boardButton',
                            icon:'/images/icons/application_view_columns.png',
//                    icon: '/images/icons/multi-patient.png',
                            tooltip:'Multi-Patient'
                        },
                        {
                            text:'Split',
                            itemId: 'splitButton',
                            icon:'/images/icons/application_side_list.png',
                            tooltip:'Split',
                            pressed:true
                        },
                        {
                            text:'Patient',
                            itemId: 'singlePatientButton',
                            icon:'/images/icons/application.png',
//                    icon: '/images/icons/single-patient.png',
                            tooltip:'Single-Patient'
                        }
                    ],
                    listeners:{
                        toggle:function (container, button, pressed) {
                            container.up('tristatepanel').showSelectedPanel();
                        }
                    }
                },
                {
                    xtype:'button',
                    itemId:'addPatientButton',
                    text:'Add Patient',
                    tooltip:'Add Patient to Team List',
                    margins:'0 0 0 50',
                    menu:{
                        width:300,
                        items:[
                            {
                                xtype:'combobox', allowBlank:false, displayField:'name', valueField:'id',
                                hideTrigger:true,
                                width:250,
                                listConfig:{
                                    minHeight:50,
                                    emptyText:'No matching records found...',
                                    loadingText:'Searching....'
                                },
                                store:Ext.create('Ext.data.Store', {
                                    fields:['name', 'id'],
                                    proxy:{
                                        type:'ajax',
                                        url:'/roster/source',
                                        extraParams:{
                                            id:'Patient'
                                        },
                                        reader:{
                                            root:'data',
                                            type:'json'
                                        }
                                    }
                                }),
                                emptyText:'Select a value...',
                                forceSelection:true,
                                queryParam:'filter',
                                minChars:4,
                                queryMode:'remote',
                                listeners:{
                                    select:function (combo, recs) {
                                        console.log(recs);
                                        var rpkr = Ext.ComponentQuery.query('favrosterpicker')[0];
                                        if (rpkr) {
                                            var rid = rpkr.getValue();
                                            var parms = {'dfn':recs[0].get('id'), 'id':rid};
                                            Ext.Ajax.request({
                                                url:'/roster/addPatient',
                                                params:parms,
                                                success:function (resp) {
                                                    var vds = Ext.ComponentQuery.query('viewdefgridpanel');
                                                    for (key in vds) {
                                                        if (vds[key].curViewParams && vds[key].curViewParams['roster.ien']) {
                                                            vds[key].store.load();
                                                        }
                                                    }
                                                    Ext.MessageBox.alert('Success', 'Roster successfully updated.');
                                                },
                                                failure:function (resp) {
                                                    Ext.MessageBox.alert('Could not add patient', resp);
                                                }
                                            });
                                        }
                                        // both the displayField and valueField must end up in the edited record
                                        // this seems like the only way I can figure to get access to the displayField later.
                                        //combo.lastDisplay = recs[0].data.name;
                                        //TODO: Add this guy to the currently selected rooster.
                                    }
                                }
                            }
                        ]
                    },
//            handler:function (bn) {
//                // TODO: Show patient selector.
//                // TODO: Add to existing roster and save roster.
//                var wnd = Ext.create('Ext.window.Window', {
//                    title:'Add Patient to Current Roster',
//                    width:300,
//                    items:[
//                        {
//                            xtype:'combobox', allowBlank:false, displayField:'name', valueField:'id',
//                            hideTrigger:true,
//                            width:250,
//                            listConfig:{
//                                minHeight:50,
//                                emptyText:'No matching records found...',
//                                loadingText:'Searching....'
//                            },
//                            store:Ext.create('Ext.data.Store', {
//                                fields:['name', 'id'],
//                                proxy:{
//                                    type:'ajax',
//                                    url:'/roster/source',
//                                    extraParams:{
//                                        id:'Patient'
//                                    },
//                                    reader:{
//                                        root:'data',
//                                        type:'json'
//                                    }
//                                }
//                            }),
//                            emptyText:'Select a value...',
//                            forceSelection:true,
//                            queryParam:'filter',
//                            minChars:4,
//                            queryMode:'remote',
//                            listeners:{
//                                select:function (combo, recs) {
//                                    console.log(recs);
//                                    var rpkr = Ext.ComponentQuery.query('favrosterpicker')[0];
//                                    if (rpkr) {
//                                        var rid = rpkr.getValue();
//                                        var parms = {'dfn':recs[0].get('id'), 'id':rid};
//                                        Ext.Ajax.request({
//                                            url:'/roster/addPatient',
//                                            params:parms,
//                                            success:function (resp) {
//                                                var vds = Ext.ComponentQuery.query('viewdefgridpanel');
//                                                for (key in vds) {
//                                                    if (vds[key].curViewParams && vds[key].curViewParams['roster.ien']) {
//                                                        vds[key].store.load();
//                                                    }
//                                                }
//                                                Ext.MessageBox.alert('Success', 'Roster successfully updated.');
//                                                wnd.close();
//                                            },
//                                            failure:function (resp) {
//                                                Ext.MessageBox.alert('Could not add patient', resp);
//                                            }
//                                        });
//                                    }
//                                    // both the displayField and valueField must end up in the edited record
//                                    // this seems like the only way I can figure to get access to the displayField later.
//                                    //combo.lastDisplay = recs[0].data.name;
//                                    //TODO: Add this guy to the currently selected rooster.
//                                }
//                            }
//                        }
//                    ]});
//
//                wnd.show();
//
//            }
                },
                '->',
                {
                    xtype: 'button',
                    ui: 'link',
                    itemId: 'patientlisteditbutton',
                    region: 'east',
                    margins: '5 0 5 0',
                    minWidth: 200,
                    text: 'Edit Patient List(s)',
                    flex: 2,
                    handler: function() {
                        var cpe = Ext.ComponentQuery.query('cpepanel');
                        if(cpe && cpe.length>0)
                        {
                            cpe[0].showRosterEditor();
                        }
                    }
                }
            ]
        }
    ],
    layout:{type:'hbox', align:'stretch'},
    items:[
        {
            xtype:'patientpicker',
            id:'BoardFull',
//            ui:'gadget',
            title:'Patient Selection',
//            frame:true,
            hidden:true,
//            header:true,
            collapsible:false,
            collapsed:false,
//            margin:'0 0 6 0',
            rosterViewDef:'org.osehra.cpe.vpr.queryeng.RosterViewDef',
            flex:1
        },
        {
            xtype:'container',
            id:'SplitContainer',
            layout:'border',
            flex:1,
            items:[
                {
                    xtype:'patientpicker',
//                    ui:'gadget',
                    id:'BoardShortPicker',
                    title:'Patient Selection',
                    maxWidth:300,
//                    frame:true,
//                    header:true,
                    collapsible:false,
                    collapsed:false,
                    margin:'0 0 6 0',
                    split:true,
                    rosterViewDef:'org.osehra.cpe.vpr.queryeng.RosterViewDef',
                    compactOrientation:true,
                    region:'west',
                    split:true,
                    width:300,
//                    items:[
//                        {
//                            xtype:'rosterpanel',
//                            region:'north',
//                            shortOrientation:true
//                        },
//                        {
//                            xtype:'viewdefgridpanel',
//                            region:'center',
//                            header:false,
//                            forceFit:true,
//                            patientAware:false,
//                            selType:'rowmodel',
//                            reconfigureColumnsAlways:true, // Added for MSTHRE-591; Switching patient list types fails unless we always reconfig columns.
//                            scroll:false,
//                            viewConfig:{
//                                plugins:{
//                                    ptype:'gridviewdragdrop',
//                                    ddGroup:'PatientGroup',
//                                    enableDrop:false
//                                },
//                                style:{ overflow:'auto', overflowX:'hidden' }
//                            }
//                        }
//                    ]
                },
                {
                    xtype:'patientchart',
//	      	flex: 4,
                    region:'center'
                }
            ]
        }
    ],
    initComponent:function () {
        if (this.rosterID) {
            this.items[0].rosterID = this.rosterID;
            this.items[1].items[0].rosterID = this.rosterID;
        }
        this.callParent();
    },
    showSelectedPanel:function () {
        if (this.down('#boardButton').pressed) {
            this.down('container[id=SplitContainer]').hide();
            this.down('panel[id=BoardFull]').show();
            this.down('#addPatientButton').show();
        } else if (this.down('#splitButton').pressed) {
            this.down('panel[id=BoardFull]').hide();
            this.down('container[id=SplitContainer]').show();
            this.down('patientpicker[id=BoardShortPicker]').show();
            this.down('#addPatientButton').show();
        } else {
            this.down('panel[id=BoardFull]').hide();
            this.down('container[id=SplitContainer]').show();
            this.down('patientpicker[id=BoardShortPicker]').hide();
            this.down('#addPatientButton').hide();
        }
    },
    initPatientPickerListeners:function () {
        var cpe = this.up('cpepanel');
        this.items.items[0].on('selectpatient', cpe.onSelectPatient, cpe);
        this.items.items[1].items.items[0].on('selectpatient', cpe.onSelectPatient, cpe);

        this.down('pagepicker').on('change', cpe.onPageChange, cpe);

        // TODO: This will need more looking into for peeling it out of CPEPanel.
        var tabnavfxn = function (e) {
            if (cpe.ptpanel.isXType('tabpanel')) {
                var tab = me.ptpanel.getActiveTab();
                tab = (e.ctrlKey == true || e.keyCode == e.HOME) ? tab.previousSibling() : tab.nextSibling();
                if (tab) {
                    me.ptpanel.setActiveTab(tab);
                }
            }
        }

        // TODO: This, too.
        // keyboard shortcuts/nav
        this.nav = Ext.create('Ext.util.KeyNav', Ext.getDoc(), {
            scope:this,
            'esc':this.showPicker,
            'tab':tabnavfxn,
            'home':tabnavfxn,
            'end':tabnavfxn,
            'pageUp':function () {
                me.prevbtn.handler(me.prevbtn)
            },
            'pageDown':function () {
                me.nextbtn.handler(me.nextbtn)
            }
        });
    }
});
