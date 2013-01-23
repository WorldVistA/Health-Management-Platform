Ext.define('org.osehra.hmp.admin.AdminApp', {
    extend: 'org.osehra.hmp.Application',
    requires:[
        "Ext.util.History",
        "org.osehra.hmp.Viewport",
        "org.osehra.hmp.admin.AdminScreenSelector",
        "org.osehra.hmp.admin.AdminCardPanel"
    ],
    controllers: [
        'org.osehra.hmp.admin.AdminScreenController',
        'org.osehra.hmp.admin.TermBrowserController',
        'org.osehra.hmp.admin.VistaRpcRunnerController',
        'org.osehra.hmp.admin.SyncAdminController'
    ],
    init: function() {
        Ext.util.History.init();
    },
    launch:function() {
        Ext.create('org.osehra.hmp.Viewport',{
            items: [
                Ext.create('org.osehra.hmp.admin.AdminScreenSelector', {
                    region: 'west',
                    split: true,
                    padding: '6 0 6 6'

                }),
                Ext.create('org.osehra.hmp.admin.AdminCardPanel', {
                    region: 'center',
                    padding: '6 6 6 0'

                })
            ]
        });
   }
});
