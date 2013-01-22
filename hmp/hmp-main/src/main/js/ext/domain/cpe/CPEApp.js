Ext.define('EXT.DOMAIN.cpe.CPEApp', {
    extend:'EXT.DOMAIN.hmp.Application',
    requires:[
        'EXT.DOMAIN.hmp.appbar.ErrorManager',
        'EXT.DOMAIN.hmp.Viewport',
        'EXT.DOMAIN.cpe.CPEPanel',
        'EXT.DOMAIN.hmp.PatientContext',
        'EXT.DOMAIN.cpe.roster.RosterContext'
    ],
    launch:function () {
    	
    	var cpeConf = {
            xtype:'cpepanel',
            region:'center'
        }
        
        var appInfo = EXT.DOMAIN.hmp.AppContext.getAppInfo();
        if (Ext.isDefined(appInfo.contexts)) {
            // restore roster context
            if (Ext.isDefined(appInfo.contexts.rosterId) && appInfo.contexts.rosterId) {
            	cpeConf.rosterID = appInfo.contexts.rosterId;
            }

            // restore patient context
            if (Ext.isDefined(appInfo.contexts.pid) && appInfo.contexts.pid != null) {
                EXT.DOMAIN.hmp.PatientContext.setPatientContext(appInfo.contexts.pid);
            }
        }

        Ext.create('EXT.DOMAIN.hmp.Viewport', {
            items:[cpeConf]
        });
    }
});
