Ext.define('EXT.DOMAIN.hmp.team.TeamApp2', {
    extend:'EXT.DOMAIN.hmp.Application',
    requires:[
        'EXT.DOMAIN.hmp.Viewport',
        'EXT.DOMAIN.hmp.team.TeamSelector',
        'EXT.DOMAIN.hmp.team.TeamManagementPanel'
    ],
    launch:function () {
        Ext.create('EXT.DOMAIN.hmp.Viewport', {
            items:[
                Ext.create('EXT.DOMAIN.hmp.team.TeamSelector', {
                    region:'west',
                    split:true
                }),
                Ext.create('EXT.DOMAIN.hmp.team.TeamPanel', {
                    region:'center'
                })
            ]
        });
    }
});
