Ext.define('org.osehra.hmp.admin.AdminCardPanel', {
    extend:'Ext.container.Container',
    requires:[
        'org.osehra.hmp.admin.VprPatientBrowser',
        'org.osehra.hmp.admin.SyncErrorsPanel',
        'org.osehra.hmp.admin.SyncAdminPanel',
        'org.osehra.hmp.admin.VistaAccountAdmin',
        'org.osehra.hmp.appbar.AppInfoPropertiesGrid',
        'org.osehra.hmp.admin.VistaRpcRunner',
        'org.osehra.hmp.admin.VistaRpcBrowser',
        'org.osehra.hmp.admin.FrameListPanel',
        'org.osehra.hmp.admin.TermBrowserPanel'
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
        this.add(Ext.create('org.osehra.hmp.admin.FrameListPanel'));
        this.add(Ext.create('org.osehra.hmp.admin.TermBrowserPanel'));
    }
});
