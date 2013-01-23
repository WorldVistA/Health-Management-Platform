Ext.define('org.osehra.hmp.team.TeamSelector', {
    extend:'Ext.grid.Panel',
    requires:[
        'org.osehra.hmp.EventBus'
    ],
    ui:'plain',
//    title:'Teams',
    hideHeaders:true,
    dockedItems:[
        {
            xtype:'toolbar',
            docked:'top',
            ui:'plain',
            items:[
                '->',
                {
                    xtype:'button',
                    ui:'link',
                    text:'Add New Team',
                    listeners:{
                        click:function () {
                            Ext.getStore('teams').insert(0, {displayName:'New Team', owner:VPR.appbar.userName});
                        }
                    }
                }
            ]
        }
    ],
    store:Ext.create('Ext.data.Store', {
        storeId:'teams',
        fields:['displayName', 'owner', 'positions'],
        associations:[
            { type:'hasMany', model:'org.osehra.hmp.team.TeamPosition', associationKey:'positions' }
        ],
        proxy:{
            type:'ajax',
            url:'/js/org.osehra/hmp/team/teams.json',
            reader:{
                type:'json',
                root:'data.items'
            }
        },
        autoLoad:true
    }),
    columns:[
        {text:'Team', dataIndex:'displayName', flex:1}
    ],
    listeners:{
        select:function (rowModel, record) {
            org.osehra.hmp.EventBus.fireEvent('teamselect', record);
        }
    },
    initComponent:function () {
        var me = this;
        me.callParent(arguments);
    }
});
