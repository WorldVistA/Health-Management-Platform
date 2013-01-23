Ext.define('org.osehra.cpe.multi.BoardBuilderApp', {
    extend:'org.osehra.hmp.Application',
    requires:[
        'org.osehra.hmp.Viewport',
        'org.osehra.cpe.multi.MultiPatientPanelEditor'
    ],
    launch:function () {
        Ext.create('org.osehra.hmp.Viewport', {
            items:[
                {
                    xtype:'panelEditor',
                    region:'center'
                }
            ]
        });


//        var wnd = Ext.create("org.osehra.cpe.PingWindow", {
//        	width: 400,
//        	height: 400
//        });
//        wnd.show();
//        wnd.center();
    }
});
