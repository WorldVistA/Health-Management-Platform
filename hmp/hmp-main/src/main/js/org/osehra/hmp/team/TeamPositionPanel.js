Ext.define('org.osehra.hmp.team.TeamPositionPanel', {
    extend:'Ext.container.Container',
    requires:[
        'org.osehra.hmp.team.TeamPositionStore'
    ],
    layout:'border',
    items:[
        {
            xtype:'grid',
            itemId: 'positionList',
            minWidth:200,
            width:200,
            region:'west',
            title:'Team Positions',
            columns:[
                { text:'Name', flex:1, dataIndex:'name' }
            ],
            tools:[
                {
                    type:'plus',
                    itemId:'createButton',
                    tooltip:'New Position'
                }
            ]
        },
        {
            xtype:'form',
            itemId: 'positionEdit',
            region:'center',
            bodyPadding:10,
            layout:'anchor',
            defaults:{
                anchor:'100%'
            },
            items:[
                {
                    xtype:'textfield',
                    itemId: 'nameField',
                    name:'name',
//                    fieldLabel:'Name',
                    emptyText: 'Name',
                    enableKeyEvents: true
                },
                {
                    xtype:'textarea',
                    name:'description',
                    emptyText: 'Description',
//                    fieldLabel:'Description',
                    height:200
                },
                {
                    xtype:'toolbar',
                    items:[
                        '->',
                        {
                            xtype:'button',
                            itemId:'deleteButton',
//                            ui: 'red',
                            enable:true,
                            text:'Remove'
                        },
                        {
                            xtype:'button',
                            itemId:'saveButton',
                            ui:'theme-colored',
                            enable:false,
                            text:'Save'
                        }
                    ]
                }
            ]
        }
    ],
    initComponent:function () {
        var positionStore = Ext.getStore('teamPositions');
        if (!positionStore) {
            positionStore = Ext.create('org.osehra.hmp.team.TeamPositionStore');
        }
        this.items[0].store = positionStore;

        this.callParent(arguments);
    }
});
