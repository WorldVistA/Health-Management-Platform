Ext.define('EXT.DOMAIN.hmp.admin.SyncErrorsPanel', {
    extend:'Ext.container.Container',
    requires:[
        'EXT.DOMAIN.hmp.admin.SyncErrorStore'
    ],
    alias: 'widget.syncerrorspanel',
    itemId:'sync-errors',
    layout:{
        type:'border'
//        align:'stretch'
    },
    items:[
        {
            region:'center',
            xtype:'grid',
            itemId:'syncErrorGrid',
            title:'Sync Errors',
            frame:true,
//            minHeight:120,
            height:'40%',
            loadMask:true,
            features:[
                {ftype:'grouping'}
            ],
            sortableColumns:false,
            viewConfig:{
                emptyText:'No Sync Errors',
                deferEmptText:false
            },
            store:'syncErrors',
            columns:[
                {header:'ID', dataIndex:'id', width:36},
                {header:'PIDs', dataIndex:'pids'},
                {header:'Patient', dataIndex:'patient', flex:1},
                {header:'Date&nbsp;Created', dataIndex:'dateCreated'},
                {header:'Item', dataIndex:'item', groupable:true, flex:2},
                {header:'Message', dataIndex:'message', groupable:true, flex:2}
            ],
            dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'top',
                    items:[
                        '->',
                        {
                            xtype:'button',
                            text:'Clear All Sync Errors',
                            listeners:{
                                click:function (button, event) {
                                    var detail = button.up('#syncErrorGrid').nextSibling('#syncErrorDetail');
                                    detail.getForm().reset();
                                    var store = Ext.getStore('syncErrors');
                                    store.removeAll();
                                    Ext.Ajax.request({
                                        url:'/sync/syncErrors/clear',
                                        method:'POST',
                                        params:{
                                            format:'json'
                                        },
                                        success:function (response) {
                                            Ext.getStore('syncErrors').load();
                                        }
                                    });
                                }
                            }
                        }
                    ]
                },
                {
                    xtype:'pagingtoolbar',
                    dock:'bottom',
                    store:'syncErrors',
                    displayInfo:true
                }
            ],
            listeners:{
                select:function (grid, record, index) {
                    var me = this;
                    var syncErrorId = record.get('id');
                    me.nextSibling('#syncErrorDetail').loadRecord(record);
                }
            }
        },
        {
            region:'south',
            xtype:'form',
            itemId:'syncErrorDetail',
            title:'Sync Error Detail',
            split:true,
            height:'60%',
            frame:true,
            bodyPadding:5,
            autoScroll:true,
            fieldDefaults:{
                labelAlign:'right',
                labelSeparator:''
            },
            layout:'anchor',
            defaults:{
                anchor:'100%'
            },
            defaultType:'displayfield',
            items:[
                {
                    fieldLabel:'ID',
                    name:'id'
                },
                {
                    fieldLabel:'PIDs',
                    name:'pids'
                },
                {
                    fieldLabel:'Patient',
                    name:'patient'
                },
                {
                    fieldLabel:'Date Created',
                    name:'dateCreated'
                },
                {
                    fieldLabel:'Message',
                    name:'message'
                },
                {
                    fieldLabel:'Item',
                    name:'item'
                },
                {
                    xtype:'textarea',
                    readOnly:true,
                    grow:true,
                    growMax:400,
                    fieldLabel:'JSON',
                    name:'json'
                },
                {
                    xtype:'textarea',
                    readOnly:true,
                    grow:true,
                    growMax:400,
                    fieldLabel:'Stack Trace',
                    name:'stackTrace'
                }
            ]
        }
    ],
    initComponent:function () {
        var store = Ext.getStore('syncErrors');
        if (!store) {
            Ext.create("EXT.DOMAIN.hmp.admin.SyncErrorStore");
        }
        this.callParent(arguments);
    }
});
