Ext.define('org.osehra.hmp.admin.SyncAdminPanel', {
    extend:'Ext.panel.Panel',
    requires:[
        'org.osehra.hmp.AppContext',
        'org.osehra.hmp.UserContext',
        'org.osehra.cpe.roster.RosterPicker'
    ],
    alias: 'widget.syncadminpanel',
    itemId:'sync',
    defaults:{
        width:'50%',
        padding:'5',
        bodyPadding:5,
        fieldDefaults:{
            labelAlign:'right'
        },
        layout:'anchor',
        defaults:{
            anchor:'100%'
        }
    },
    items:[
        {
            xtype:'component',
            itemId:'message'
        },
        {
            xtype:'grid',
            itemId:'stats',
            title:'Stats',
            height:200,
            store:Ext.create('Ext.data.Store', {
                storeId:'statsStore',
                fields:['name', 'value'],
                proxy:{
                    type:'ajax',
                    url:'/sync/stats',
                    extraParams:{
                        format:'json'
                    },
                    reader:{
                        type:'json',
                        root:'data.items'
                    }
                },
                autoLoad:true
            }),
            columns:[
                { header:'Name', dataIndex:'name', width: 200},
                { header:'Value', dataIndex:'value'}
            ],
            dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'bottom',
                    ui:'footer',
                    items:[
                        '->',
                        {
                            xtype:'button',
                            itemId: 'autoUpdateToggle',
                            text:'Disable Automatic Updates'
                        }
                        ]
                }
                ]
        },
        {
            xtype:'form',
            title:'Synchronize a Roster of Patients',
            height:100,
            items:[
                {
                    xtype:'rosterpicker',
                    itemId:'rosterField',
                    width:'100%'
                }
            ],
            buttons:[
                {
                    itemId: 'syncRosterButton',
                    ui:'theme-colored',
                    text:'Synchronize Roster'
                }
            ]
        },
        {
            xtype:'form',
            itemId: 'syncForm',
            title:'Synchronize a Patient',
            defaults:{
                labelSeparator:''
            },
            items:[
                { xtype:'textfield', fieldLabel:'DFN', name:'dfn' },
                { xtype:'textfield', fieldLabel:'ICN', name:'icn' }
            ],
            buttons:[
                {
                    itemId: 'syncPatientButton',
                    ui:'theme-colored',
                    text:'Synchronize'
                }
            ]
        },
        {
            xtype:'form',
            itemId: 'clearForm',
            title:'Clear',
            fieldDefaults:{
                labelSeparator:'',
                labelAlign:'right'
            },
            items:[
                {
                    xtype:'fieldcontainer',
                    layout:'hbox',
                    fieldDefaults:{
                        labelSeparator:'',
                        labelAlign:'right'
                    },
                    items:[
                        {xtype:'textfield', fieldLabel:'DFN', name:'dfn', flex:1},
                        {xtype:'textfield', fieldLabel:'Facility Code', name:'fcode', flex:1}
                    ]},
                { xtype:'textfield', fieldLabel:'ICN', name:'icn' },
                { xtype:'textfield', fieldLabel:'VPR PID', name:'pid' }
            ],
            dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'bottom',
                    ui:'footer',
                    items:[
                        {
                            xtype:'button',
                            itemId:'clearAllSyncErrorsButton',
                            text:'Clear All Sync Errors'
                        },
                        {
                            xtype:'button',
                            itemId: 'clearAllPatientsButton',
                            text:'Clear All Patients'
                        },
                        '->',
                        {
                            xtype:'button',
                            itemId: 'clearPatientButton',
                            ui:'theme-colored',
                            text:'Clear Patient'
                        }
                    ]
                }
            ]
        },
        {
            xtype:'form',
            itemId:'reindexForm',
            title:'Reindex',
            defaults:{
                labelSeparator:''
            },
            items:[
                { xtype:'textfield', fieldLabel:'PID', name:'pid' }
            ],
            dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'bottom',
                    ui:'footer',
                    items:[
                        {
                            xtype:'button',
                            itemId:'reindexAllButton',
                            text:'Reindex All Patients'
                        },
                        '->',
                        {
                            xtype:'button',
                            itemId:'reindexPatientButton',
                            ui:'theme-colored',
                            text:'Reindex Patient'
                        }
                    ]
                }
            ]
        }
    ],
    autorefresh:function () {
        var store = Ext.getStore('statsStore');
        store.load();
    }
});
