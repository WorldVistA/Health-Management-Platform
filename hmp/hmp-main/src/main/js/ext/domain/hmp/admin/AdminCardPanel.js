Ext.define('EXT.DOMAIN.hmp.admin.AdminCardPanel', {
    extend:'Ext.container.Container',
    requires:[
        'EXT.DOMAIN.hmp.admin.VprPatientBrowser',
        'EXT.DOMAIN.hmp.admin.SyncErrorsPanel',
        'EXT.DOMAIN.hmp.admin.SyncAdminPanel',
        'EXT.DOMAIN.hmp.admin.VistaAccountAdmin',
        'EXT.DOMAIN.hmp.appbar.AppInfoPropertiesGrid',
        'EXT.DOMAIN.hmp.admin.VistaRpcRunner',
        'EXT.DOMAIN.hmp.admin.VistaRpcBrowser',
        'EXT.DOMAIN.hmp.admin.FrameListPanel',
        'EXT.DOMAIN.hmp.admin.TermBrowserPanel'
    ],
    itemId:'adminCardPanel',
    hidden: true,
    layout:{
        type:'card',
        deferredRender:true
    },
    items:[
        {
            xtype: 'vprpatientbrowser'
        },
        {
            xtype: 'syncerrorspanel'
        },
        {
            xtype: 'syncadminpanel'
        },
        {
            xtype: 'vistarpcrunner'
        },
        {
            xtype: 'vistarpcbrowser'
        },
        {
            xtype:'appinfopropertygrid',
            itemId:'hmp-properties',
            title:'HMP Properties',
            appInfo:'props'
        },
        {
            xtype:'appinfopropertygrid',
            itemId:'system-properties',
            title:'System Properties',
            appInfo:'system'
        },
        {
            xtype:'appinfopropertygrid',
            itemId:'environment-variables',
            title:'Environment Variables',
            appInfo:'env'
        }
    ],
    initComponent:function () {
        this.callParent(arguments);

        this.add(Ext.create('Ext.panel.Panel', {itemId: 'drools-edit', html: 'Work In Progress...'}));
        this.add(Ext.create('EXT.DOMAIN.hmp.admin.FrameListPanel'));
        this.add(Ext.create('EXT.DOMAIN.hmp.admin.TermBrowserPanel'));
    }
});
