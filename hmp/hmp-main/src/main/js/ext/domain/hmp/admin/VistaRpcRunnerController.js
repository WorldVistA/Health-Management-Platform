/**
 * Controls behavior of {@link EXT.DOMAIN.hmp.admin.VistaRpcRunner}
 */
Ext.define('EXT.DOMAIN.hmp.admin.VistaRpcRunnerController', {
    extend:'EXT.DOMAIN.hmp.Controller',
    refs:[
        {
            ref:'rpcParams',
            selector:'#rpcParametersField'
        },
        {
            ref:'rpcResultPanel',
            selector:'#rpcResultPanel'
        }
    ],
    init:function () {
//        console.log(Ext.getClassName(this) + ".init()");
        var me = this;

        me.control({
            '#executeButton':{
                click:me.executeRpc
            }
        });
    },
    removeRpcParam:function () {
        var me = this;
        var params = me.getRpcParams();
        params.remove(params.items.last());
        if (params.items.getCount() == 0) {
            params.add({ xtype:'button', iconCls:'createIcon', handler:me.addRpcParam});
        }
    },
    addRpcParam:function () {
        var me = this;

        var params = this.getRpcParams();
        if (Ext.getClassName(params.items.getAt(0)) == 'Ext.button.Button') {
            params.removeAll();
        }
        var size = params.items.getCount();
        params.add({
            xtype:'fieldcontainer',
            combineErrors:true,
            msgTarget:'side',
            layout:'hbox',
            defaults:{
                margin:'0 0 0 2'
            },
            fieldDefaults:{
                labelAlign:'right'
            },
            items:[
                { xtype:'textfield', name:'params', fieldLabel:size + 1, flex:1, tabIndex:6 + size},
                { xtype:'button', iconCls:'deleteIcon', handler:me.removeRpcParam},
                { xtype:'button', iconCls:'createIcon', handler:me.addRpcParam}
            ]});
    },
    executeRpc:function (btn) {
        var rpcResultPanel = this.getRpcResultPanel();
        var formComponent = btn.up('form');
        var form = formComponent.getForm();
//                    if (form.isValid()) {
        var params = formComponent.getValues();
        rpcResultPanel.setLoading(Ext.String.format("Executing vrpcb:///{0}/{1}...", params.context, params.name), true);
        formComponent.down('button').disable();
        Ext.Ajax.request({
            url:"/rpc/execute",
            method:'POST',
            params:params,
            success:function (response) {
                rpcResultPanel.setLoading(false);
                rpcResultPanel.update(response);
            },
            failure:function (response) {
                rpcResultPanel.setLoading(false);
                rpcResultPanel.update(response);
            }
        });
//                    }
    }
});
