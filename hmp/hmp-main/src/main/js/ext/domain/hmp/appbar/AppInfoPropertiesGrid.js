Ext.define('EXT.DOMAIN.hmp.appbar.AppInfoPropertiesGrid', {
    extend:'Ext.grid.property.Grid',
    requires:[
        'EXT.DOMAIN.hmp.AppContext'
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
        this.setSource(EXT.DOMAIN.hmp.AppContext.getAppInfo()[this.appInfo]);
    }
});
