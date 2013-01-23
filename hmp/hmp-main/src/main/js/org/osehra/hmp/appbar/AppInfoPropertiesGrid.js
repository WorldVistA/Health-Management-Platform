Ext.define('org.osehra.hmp.appbar.AppInfoPropertiesGrid', {
    extend:'Ext.grid.property.Grid',
    requires:[
        'org.osehra.hmp.AppContext'
    ],
    alias:'widget.appinfopropertygrid',
    /**
     * @cfg {String} appInfo (required)
     * The name of the node in the @{link AppContext#getAppInfo}'s JSON data to use as this Property Grid's source.
     */
    nameColumnWidth:200,
    source:{},
    beforeRender:function() {
        this.refreshSource();
        return this.callParent(arguments);
    },
    refreshSource: function() {
        this.setSource(org.osehra.hmp.AppContext.getAppInfo()[this.appInfo]);
    }
});
