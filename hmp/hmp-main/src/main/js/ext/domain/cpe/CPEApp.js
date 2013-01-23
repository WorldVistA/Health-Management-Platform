Ext.define('org.osehra.cpe.CPEApp', {
    extend:'org.osehra.hmp.Application',
    requires:[
        'org.osehra.hmp.appbar.ErrorManager',
        'org.osehra.hmp.Viewport',
        'org.osehra.cpe.CPEPanel',
        'org.osehra.hmp.PatientContext',
        'org.osehra.cpe.roster.RosterContext'
    ],
    launch:function () {
    	
    	var cpeConf = {
            xtype:'cpepanel',
            region:'center'
        }
        
        var appInfo = org.osehra.hmp.AppContext.getAppInfo();
        if (Ext.isDefined(appInfo.contexts)) {
            // restore roster context
            if (Ext.isDefined(appInfo.contexts.rosterId) && appInfo.contexts.rosterId) {
            	cpeConf.rosterID = appInfo.contexts.rosterId;
            }

            // restore patient context
            if (Ext.isDefined(appInfo.contexts.pid) && appInfo.contexts.pid != null) {
                org.osehra.hmp.PatientContext.setPatientContext(appInfo.contexts.pid);
            }
        }

        Ext.create('org.osehra.hmp.Viewport', {
            items:[cpeConf]
        });
    }
});
