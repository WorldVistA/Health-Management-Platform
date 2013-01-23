Ext.define('org.osehra.hmp.team.TeamApp2', {
    extend:'org.osehra.hmp.Application',
    requires:[
        'org.osehra.hmp.Viewport',
        'org.osehra.hmp.team.TeamSelector',
        'org.osehra.hmp.team.TeamManagementPanel'
    ],
    launch:function () {
        Ext.create('org.osehra.hmp.Viewport', {
            items:[
                Ext.create('org.osehra.hmp.team.TeamSelector', {
                    region:'west',
                    split:true
                }),
                Ext.create('org.osehra.hmp.team.TeamPanel', {
                    region:'center'
                })
            ]
        });
    }
});
