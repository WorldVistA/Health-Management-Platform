Ext.define('org.osehra.cpe.roster.RosterWindow', {
    extend:'Ext.window.Window',
    requires:[
        'org.osehra.cpe.roster.RosterWindowSrcGrid',
        'org.osehra.cpe.roster.RosterStore',
        'org.osehra.cpe.PagePicker'
    ],
    alias:'widget.rosterwin',
    itemId:'RosterWinID',
    modal:true,
    height:500,
    width:750,
    closeAction:'hide',
    title:'Patient List Editor',
    saveDat:{},
    bbar:[
        '->',
        {xtype:'button', itemId:'CancelBtnID', text:'Cancel'},
        {xtype:'button', itemId:'BackBtnID', text:'&laquo; Edit', disabled:true},
        {xtype:'button', itemId:'PreviewBtnID', text:'Preview &raquo;'},
        {xtype:'button', itemId:'SaveBtnID', text:'Save'}
    ],
    layout:'card',
    items:[
        {
            // form/grid card
            xtype:'container',
            layout:'border',
            items:[
                {
                    xtype:'grid',
                    itemId:'RosterListID',
                    minWidth:150,
                    width:175,
                    split:true,
                    autoScroll:true,
                    region:'west',
                    tbar:[
                        {xtype:'button', itemId:'AddBtnID', text:'Add'},
                        {xtype:'button', itemId:'DelBtnID', text:'Delete'},
                        {xtype:'button', itemId:'CopyBtnID', text:'Copy', disabled:true, tooltip:'Copy not implemented yet.'}
                    ],
                    columns:[
                        //{header: 'X', dataIndex: '', xtype:'checkcolumn'},
                        {header:'Patient List', dataIndex:'name', flex:5},
                        {header:'F', dataIndex:'favorite', flex:1,
                            renderer:function (value) {
                                var v = (value === true || value === 'true' || value === '1');
                                var rslt = '<input type="checkbox"';
                                if (v) {
                                    rslt = rslt + ' checked=true';
                                }
                                rslt = rslt + '	onChange="Ext.ComponentManager.get(\'patientListFavoriteCheckBox\').setValue(!Ext.ComponentManager.get(\'patientListFavoriteCheckBox\').getValue());"/>'
                                return rslt;
                            }
                        }
                    ],
                    plugins:[
                        Ext.create('Ext.grid.plugin.CellEditing', {
                            clicksToEdit:1
                        })
                    ]
                },
                {
                    xtype:'form',
                    region:'center',
                    flex:1,
                    defaults:{
                        padding:'5 5 5 5',
                        margin:'5 5 5 5'
                    },
                    items:[
                        {
                            xtype:'fieldset',
                            layout:'hbox',
                            items:[
                                {xtype:'textfield', name:'name', fieldLabel:'Name', maxLength:45, enforceMaxLength:45, allowBlank:false, flex:1},
                                {xtype:'hidden', name:'id'}
                            ]
                        },
                        {
                            xtype:'fieldset',
                            title:'Source(s)',
                            collapsible:true,
                            items:[{
                                xtype: 'rosterwindowsrcgrid',
                                itemId:'SourcesGridID'
                            }]
                        },
                        {
                            xtype:'container',
                            layout:'hbox',
                            items:[
                                {
                                    xtype:'fieldset',
                                    title:'Advanced Options:',
                                    collapsible:true,
//                                    collapsed:true,
                                    items:[
                                        {
                                            xtype:'combobox',
                                            fieldLabel:'Shared',
                                            name:'shared',
                                            value:'PRIVATE',
                                            forceSelection:true,
                                            store:[
                                                ['PRIVATE', 'Private'],
                                                ['READONLY', 'Public (Read-Only)'],
                                                ['PUBLIC', 'Public (Modifiable)']
                                            ]
                                        },
                                        {
                                            xtype:'combobox',
                                            fieldLabel:'Pt. List Display',
                                            name:'viewdef',
                                            value:'org.osehra.cpe.vpr.queryeng.RosterViewDef',
                                            forceSelection:true,
                                            defaultListConfig:{minWidth:200},
                                            displayField:'name',
                                            valueField:'code',
                                            store:{
                                                fields:['code', 'name'],
                                                proxy:{
                                                    type:'ajax',
                                                    url:'/view/ptlists', //'/app/list?type=org.osehra.cpe.multipatientviewdef',
                                                    reader:{
                                                        root:'items',
                                                        type:'json'
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            xtype:'pagepicker',
                                            fieldLabel:'Page Config'
                                        },
                                        {
                                            xtype:'checkbox',
                                            name:'favorite',
                                            id:'patientListFavoriteCheckBox',
                                            boxLabel:'Include this list in "My Patient Lists"',
                                            inputValue:true,
                                            uncheckedValue:false,
                                            value:false
                                        }
                                        //{xtype: 'textfield', fieldLabel: 'Cache Timeout?'},
                                        //{xtype: 'checkbox', fieldLabel: 'My Default Roster'},
                                        //{xtype: 'displayfield', name: 'ownername', fieldLabel: 'Created/Updated', value: '?'},
                                    ]
                                }
                                /*
                                 {
                                 xtype: 'fieldset',
                                 title: 'Preferences:',
                                 collapsible: true,
                                 items: [

                                 ]
                                 }*/
                            ]
                        }
                    ]
                }
            ]
        },
        {
            // preview card
            itemId:'PreviewID',
            xtype:'grid',
            autoLoad:false,
            columns:[
                {header:'Patient', dataIndex:'name', flex:1},
                {header:'SSN', dataIndex:'ssn'},
                {header:'DOB', dataIndex:'dob'}
            ],
            store:Ext.create('Ext.data.Store', {
                fields:['dfn', 'name', 'gender', 'ssn', 'dob'],
                proxy:{
                    type:'ajax',
                    url:'/roster/preview',
                    reader:{
                        root:'data.patients',
                        type:'json'
                    }
                }
            })
        }
    ],
    initComponent:function () {
        var me = this;

        // create the roster store (can't put this inline because it gets called at load time, before the requires clauses have all be called)
        me.items[0].items[0].store = Ext.getStore('rosters') ? Ext.getStore('rosters') : Ext.create('org.osehra.cpe.roster.RosterStore');

        me.callParent(arguments);

        // define some quick references
        this.previewwin = this.down('#PreviewID');
        this.rostergrid = this.down('#RosterListID');
        this.rosterform = this.down('form');
        this.sourcesgrid = this.down('#SourcesGridID');

        // event handlers for nested objects ------------------------
        this.sourcesgrid.on('beforeedit', function (editor, e, eOpts) {
            // we only care about the ID column
            if (e.column.header !== 'Value') {
                return;
            }
            console.log(e);
            var store = editor.grid.getStore();
            var rowtype = e.record.get('type');
            var proxyid = store.getProxy().extraParams.id;

            // check if the proxy is different
            if (rowtype != proxyid) {
                store.getProxy().extraParams.id = rowtype;
                store.removeAll();
            }
        });
        this.sourcesgrid.on('edit', function (editor, e, eOpts) {
            if (e.column.text !== 'Value') {
                return;
            }

            // the ID field: store both the displayField and valueField
            var editor = e.column.getEditor();
            e.record.set('id', e.value);
            e.record.set('name', editor.lastDisplay);
        });

        this.on('show', function (comp, eopts) {
            // always return to the home card/screen
            me.getLayout().setActiveItem(0);

            // reset button state
            var previewbtn = me.down('#PreviewBtnID');
            previewbtn.disable();
            previewbtn.prev().disable();
//			previewbtn.next().disable();

            // deselect any current selected roster
            me.rostergrid.getSelectionModel().deselectAll();

            // disable the editor until a roster is selected
            me.rosterform.disable();
        });

        this.on('hide', function (comp, eopts) {
            me.saveDat = {};
            me.rostergrid.getStore().load();
        })

        this.rostergrid.on('boxready', function () {
            me.rostergrid.getStore().load();
        });

        this.rostergrid.on('beforeselect', function (rowmodel, rec, index, eopts) {
            if (!me.rosterform.isDisabled()) {
                // Validation code goes here.
                // If validation fails, show a message and return false.

                // This will put all form data into the record so it can be retrieved later when we batch-save records.
                me.updateCurrentlySelectedRecord();
            }
        });

        this.rostergrid.on('select', function (rowmodel, rec, index, eOpts) {
            // load the form with the roster to edit
            // Why don't we forget this saveDat business and just use the records themselves.
            //var dt = me.saveDat;

            // instead of reconfigure() manually remove all items and add them back in.
            // scrollbars seem to break, but we can hack that back in.
            me.sourcesgrid.getStore().removeAll();

            me.rosterform.loadRecord(rec);
            me.sourcesgrid.getStore().add(rec.get('sources'));

            // if sources are empty, add a row
            if (me.sourcesgrid.getStore().getCount() == 0) {
                me.addRow(me.sourcesgrid, 0);
            }

            // enable form and preview button
            var previewbtn = me.down('#PreviewBtnID');
            previewbtn.enable();
            me.rosterform.enable();
        });

        // event handlers for buttons -------------------------------
        this.down('#CancelBtnID').on('click', function () {
            me.close();
        });

        this.down('#BackBtnID').on('click', function () {
            // switch back to edit card
            me.getLayout().setActiveItem(0);

            // enable/disable appropriate buttons
            this.disable();
            this.next().enable();
//        	this.next().next().disable();
        });

        this.down('#AddBtnID').on('click', function () {
            var r = {id:'New' + (me.newId++), name:'New Roster', sources:[]};

            // insert a new row and select it.
            var added = me.rostergrid.getStore().add(r);
            me.rostergrid.getSelectionModel().select(added);
        });

        this.down('#CopyBtnID').on('click', function () {
            // TODO: implement me.
        });

        this.down('#DelBtnID').on('click', function () {
            var store = me.rostergrid.getStore();
            var row = me.rostergrid.getSelectionModel().selected.get(0);

            if (!row) {
                return;
            }

            Ext.MessageBox.confirm('Delete Patient List', 'Remove patient list "' + row.get('name') + '"?', function (btn) {
                if (btn === 'yes') {
                    store.remove(row);
                    var rid = row.get('id');
                    var sd = me.saveDat;
                    delete me.saveDat[rid];
                    me.rostergrid.getSelectionModel().deselectAll();
                    Ext.Ajax.request({
                        url:'/roster/delete',
                        params:{
                            id:row.get('id')
                        },
                        success:function (resp) {
                            me.refreshRosters();
                            me.rosterform.form.reset();
                            me.rosterform.disable();
                        }
                    });
                }
            }, {row:row});
        });

        // when the save button is clicked, create/update the roster
        this.down('#SaveBtnID').on('click', function () {
            if (!me.rosterform.isDisabled()) {    // Make sure current form data gets saved in the store's record.
                me.updateCurrentlySelectedRecord();
            }

            var modrecs = me.getModifiedRecordSet();
            if (modrecs.length > 0) {
                var saveDat = {};
                for (var id in modrecs) {    // Check to be sure no sources with id 0 exist in this list.
                    var srcs = modrecs[id].get('sources');
                    for (var sid in srcs) {
                        if (srcs[sid].get('id') === 0) {
                            // Sometimes this comes up if the save button is pressed immediately from the patient list grid panel.
                            // It seems to me that this event is firing before the patient edit finishes and establishes a patient ID.
                            // So it could be construed as a race condition, but .... anyway. It's a small corner case.
                            Ext.MessageBox.alert('Cannot Save Roster List', 'Roster "' + modrecs[id].get('name') + '" has an item that has not been selected or has an ID of zero.');
                            return;
                        }
                    }
                    // Convert each store record into a saveable record to send to VPR.
                    saveDat[id] = me.convertStoreRecordForRPCCall(modrecs[id]);
                }

                Ext.Ajax.request({
                    url:'/roster/uall',
                    params:{
                        set:saveDat
                    },
                    success:function (req) {
                        me.refreshRosters();
                        me.close();
                    }
                });
            }
            else {
                me.close();
            }
        });

        // preview button should cause the preview grid/store to be reloaded.
        this.down('#PreviewBtnID').on('click', function () {
            // first check if the roster is valid
            var def = me.save(true).def;
            if (!me.down('form').getForm().isValid() || def.length == 0) {
                alert('The roster form is not valid');
                return;
            }
            for (var i in def) {
                if (def[i].indexOf('^0') > 0) {
                    alert('Invalid Roster Source: ' + def[i]);
                    return;
                }
            }

            // switch to the preview card
            me.getLayout().setActiveItem(1);

            // enable edit/save buttons, disable preview button
            this.prev().enable();
            this.next().enable();
            this.disable();

            // force the preview store to reload
            me.previewwin.getStore().removeAll();
            me.previewwin.getStore().getProxy().extraParams.def = def;
            me.previewwin.getStore().load();
        });

    },

    updateCurrentlySelectedRecord:function () {
        var recs = this.rostergrid.getSelectionModel().getSelection();
        if (recs && recs.length > 0) {
            // Be sure to store the list of selected patients so this can be saved / restored.
            var rec = recs[0];
            var formvals = this.down('form').getValues();
            rec.data.name = formvals.name;
            rec.data.favorite = formvals.favorite;
            rec.data.id = formvals.id;
            rec.data.sources = [];
            rec.data.formvals = [];

            var store = this.down('form').down('gridpanel').getStore();

            // loop through each source and add it as a delimited string
            for (var i = 0; i < store.getCount(); i++) {
                var srec = store.getAt(i);
                rec.data.sources.push(srec);
            }

            // find the user prefs and send them as well
            for (var i in formvals) {
                rec.data.formvals[i] = formvals[i];
            }
            recs[0].modsave = true;
            this.rostergrid.getStore().fireEvent('datachanged', null);
        }
    },

    getModifiedRecordSet:function () {
        var recs = [];
        for (var i in this.rostergrid.getStore().data.items) {
            var rec = this.rostergrid.getStore().data.items[i];
            if (rec.modsave) {
                recs.push(rec);
            }
        }
        return recs;
    },

    newId:0, // Hacky, but anything else requires a bit of refactoring with the way the form saves / loads roster data.
    refreshRosters:function () {
        // reload the roster store.  Should cause the tree to refresh
        this.rostergrid.getStore().load();
        var ppkr = Ext.ComponentManager.get('RosterBox');
        if (ppkr) {
            ppkr.getStore().load();
        }
    },
    convertStoreRecordForRPCCall:function (rec) {
        var ret = {id:0, prefs:{}};

        for (var i in rec.get('formvals')) {
            ret[i] = rec.get('formvals')[i];
        }

        ret.id = rec.get('id');
        if (ret.id.indexOf('New') > -1) {
            // Hack in id 0
            ret.id = '0';
        }

        var rosterid = ret.id;
        if (!rosterid || rosterid == 0 || rosterid === '0') {
            // no id specified, should be blank;
            rosterid = '';
        }
        ret.def = [[rec.get('name'), rosterid, rec.get('name'), '', 1804].join('^')];

        for (var i = 0; i < rec.get('sources').length; i++) {
            var srec = rec.get('sources')[i];
            ret.def.push([srec.get('type'), srec.get('operation').toUpperCase(), srec.get('id')].join('^'));
        }

        return Ext.encode(ret);
    },

    save:function (previewMode) {
        var ret = {id:0, prefs:{}, sources:[], sourceRecs:[]};
        var formvals = this.down('form').getValues();
        var store = this.down('form').down('gridpanel').getStore();
        var rosterid = formvals.id;
        if (!rosterid || rosterid == 0 || previewMode) {
            // no id specified, should be blank;
            rosterid = '';
        }
        ret.def = [[formvals.name, rosterid, formvals.name, '', 1804].join('^')];
        ret.id = formvals.id;

        // loop through each source and add it as a delimited string
        for (var i = 0; i < store.getCount(); i++) {
            var rec = store.getAt(i);
            ret.def.push([rec.get('type'), rec.get('operation').toUpperCase(), rec.get('id')].join('^'));
        }

        // find the user prefs and send them as well
        for (var i in formvals) {
            ret[i] = formvals[i];
        }
        this.saveDat[ret.id] = Ext.encode(ret);
        return ret;
    },
    addRow:function (grid, rowIdx) {
        grid.getStore().insert(rowIdx, {id:0, type:'Patient', name:'Type to search...', operation:'Union'});
    },
    removeRow:function (grid, rowIdx) {
        if (grid.getStore().getCount() > 1) {
            // dont allow removing the last row
            grid.getStore().removeAt(rowIdx);
        }
    },
    moveRow:function (grid, rowIdx, pos) {
        var store = grid.getStore();
        var rec = store.getAt(rowIdx);

        // bounds checking
        if (!rec || (rowIdx + pos) < 0 || (rowIdx + pos) >= store.getCount()) {
            return;
        }

        // ok to move
        store.removeAt(rowIdx);
        store.insert(rowIdx + pos, rec);

        // TODO: enable/disable buttons?
    }
});
