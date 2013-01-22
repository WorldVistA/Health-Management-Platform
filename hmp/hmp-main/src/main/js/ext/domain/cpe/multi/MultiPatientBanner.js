Ext.define('EXT.DOMAIN.cpe.multi.MultiPatientBanner', {
    extend:'Ext.panel.Panel',
    requires:[
        'EXT.DOMAIN.hmp.PopUpButton',
        'EXT.DOMAIN.hmp.EventBus',
        'EXT.DOMAIN.hmp.PhotoPicker'
    ],
    alias:'widget.multiptbanner',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    height:85,
    componentCls:'hmp-multipt-banner-ct',
//    disabled:true,
    defaults:{
        componentCls:'hmp-multipt-banner-item'
    },
    items:[
        {
            xtype:'component',
            style:{
                fontWeight:'bold',
                fontSize:16
            },
            html:'Red Medicine Team'
        },
        {
            xtype:'component',
            html:'Team Picker TBD, in the meantime, pick a favorite roster'
        },
        {
            xtype: 'favrosterpicker',
            flex: 3,
            minWidth: 200,
            margins: '5 0 5 0',
            listeners: {
                select: function(combo, records){
                    if(records && records.length>0)
                    {
                        combo.doRosterSelection(combo, records[0]);
                    }
                }
            }
        }
//        {
//            xtype: 'component',
//            html:'Choose another team'
//        }
//        {
//          xtype: 'combobox',
//          store: 'teams',
//            emptyText:'Choose another Team'
//        }
    ]
});
