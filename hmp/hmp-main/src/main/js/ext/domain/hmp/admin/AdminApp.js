Ext.define('EXT.DOMAIN.hmp.admin.AdminApp', {
    extend: 'EXT.DOMAIN.hmp.Application',
    requires:[
        "Ext.util.History",
        "EXT.DOMAIN.hmp.Viewport",
        "EXT.DOMAIN.hmp.admin.AdminScreenSelector",
        "EXT.DOMAIN.hmp.admin.AdminCardPanel"
    ],
    controllers: [
        'EXT.DOMAIN.hmp.admin.AdminScreenController',
        'EXT.DOMAIN.hmp.admin.TermBrowserController',
        'EXT.DOMAIN.hmp.admin.VistaRpcRunnerController',
        'EXT.DOMAIN.hmp.admin.SyncAdminController'
    ],
    init: function() {
        Ext.util.History.init();
    },
    launch:function() {
        Ext.create('EXT.DOMAIN.hmp.Viewport',{
            items: [
                Ext.create('EXT.DOMAIN.hmp.admin.AdminScreenSelector', {
                    region: 'west',
                    split: true,
                    padding: '6 0 6 6'

                }),
                Ext.create('EXT.DOMAIN.hmp.admin.AdminCardPanel', {
                    region: 'center',
                    padding: '6 6 6 0'

                })
            ]
        });
   }
});
