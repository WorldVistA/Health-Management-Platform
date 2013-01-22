Ext.define('EXT.DOMAIN.cpe.multi.BoardBuilderApp', {
    extend:'EXT.DOMAIN.hmp.Application',
    requires:[
        'EXT.DOMAIN.hmp.Viewport',
        'EXT.DOMAIN.cpe.multi.MultiPatientPanelEditor'
    ],
    launch:function () {
        Ext.create('EXT.DOMAIN.hmp.Viewport', {
            items:[
                {
                    xtype:'panelEditor',
                    region:'center'
                }
            ]
        });


//        var wnd = Ext.create("EXT.DOMAIN.cpe.PingWindow", {
//        	width: 400,
//        	height: 400
//        });
//        wnd.show();
//        wnd.center();
    }
});
