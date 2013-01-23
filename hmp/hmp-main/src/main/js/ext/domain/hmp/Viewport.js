/**
 * A simple viewport with AppBar in the north region by default.
 */
Ext.define('org.osehra.hmp.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'org.osehra.hmp.appbar.AppBar'
    ],
    ui:'plain',
    layout:{
        type:'border',
        align:'stretch'
    },
    defaults:{
        border:false
    },
    initComponent:function() {
        this.callParent(arguments);

        this.add({
            xtype: 'appbar',
            region: 'north'
        });
    }
});
