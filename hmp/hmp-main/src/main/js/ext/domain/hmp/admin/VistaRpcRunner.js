function removeParam() {
    var params = Ext.getCmp('rpcParametersField');
    params.remove(params.items.last());
    if (params.items.getCount() == 0) {
        params.add({ xtype:'button', iconCls:'createIcon', handler:addParam});
    }
}

function addParam() {
    var params = Ext.getCmp('rpcParametersField');
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
            labelAlign:'right',
            labelSeparator:''
        },
        items:[
            { xtype:'textfield', name:'params', fieldLabel:size + 1, flex:1, tabIndex:6 + size},
            { xtype:'button', iconCls:'deleteIcon', handler:removeParam},
            { xtype:'button', iconCls:'createIcon', handler:addParam}
        ]});
}

Ext.define('EXT.DOMAIN.hmp.admin.VistaRpcRunner', {
    extend:'Ext.container.Container',
    requires:[
        'EXT.DOMAIN.hmp.containers.LinkBar'
    ],
    alias: 'widget.vistarpcrunner',
    itemId: 'rpc-call',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    items:[
        {
            xtype:'form',
            title:'Execute a VistA RPC',
            autoScroll:false,
            padding:0,
            bodyPadding:5,
            margin:'5 5 0 5',
            layout:'anchor',
            defaults:{
                anchor:'50%',
                labelAlign:'right',
                labelSeparator:''
            },
            defaultType:'textfield',
            items:[
                {
                    xtype:'fieldset',
                    title:'VistA Account',
                    collapsible:true,
                    collapsed:true,
                    defaultType:'textfield',
                    defaults:{
                        anchor:'100%',
                        labelAlign:'right',
                        labelSeparator:''
                    },
                    items:[
                        {
                            xtype:'combo',
                            fieldLabel:'Division',
                            name:'division',
                            tabIndex:1,
                            forceSelection:true,
                            valueField:'division',
                            displayField:'name',
//                            value: "${rpc.division}",
                            queryMode:'local',
                            store:Ext.create('Ext.data.Store', {
                                fields:[
                                    {name:'division', type:'string'},
                                    {name:'name', type:'string'},
                                    {name:'host', type:'string'},
                                    {name:'port', type:'string'}
                                ],
                                proxy:{
                                    type:'ajax',
                                    url:'/auth/accounts',
                                    reader:{
                                        type:'json',
                                        root:'data.items'
                                    }
                                },
                                autoLoad:true
//                                data: [
//                                    <g:each in="${accounts}" var="account" status="i">
//                                {division: "${account.division}", name: "${account.name}", host:"${account.host}", port:"${account.port}"}<g:if test="${i < accounts.size() - 1}">,
//                                    </g:if>
//                                    </g:each>
//                                ]
                            })
//                        getInnerTpl: function() {
//                            return '<div>{name}</div>';
////                            return '<div data-qtip="{name} (vrpcb://{host}:{port}/{division})">{name} ({division})</div>';
//                        }
                        },
                        {
                            fieldLabel:'Access Code',
                            inputType:'password',
                            name:'accessCode',
//                            value: "${user?.accessCode}",
                            allowBlank:false,
                            tabIndex:2
                        },
                        {
                            fieldLabel:'Verify Code',
                            inputType:'password',
                            name:'verifyCode',
//                            value: "${user?.verifyCode}",
                            allowBlank:false,
                            tabIndex:3
                        }
                    ]
                },
                {
                    id:'rpcContextField',
                    xtype:'combobox',
                    fieldLabel:'RPC Context',
                    valueField:'name',
                    displayField:'name',
                    name:'context',
                    queryMode:'local',
                    store:Ext.create('Ext.data.Store', {
                        fields:['name'],
                        // some common options (user is not required to pick one of these though)
                        data:[
                            {name:'VPR SYNCHRONIZATION CONTEXT'},
                            {name:'VPR UI CONTEXT'},
                            {name:'VPR APPLICATION PROXY'},
                            {name:'OR CPRS GUI CHART'}
                        ]
                    }),
                    allowBlank:true,
                    tabIndex:4
                },
                {
                    id:'rpcNameField',
                    fieldLabel:'RPC Name',
                    name:'name',
                    allowBlank:false,
                    tabIndex:5
                },
                {
                    id:'rpcParametersField',
                    xtype:'fieldset',
                    title:'Parameters',
                    collapsible:false,
                    fieldDefaults:{
                        labelAlign:'right',
                        labelSeparator:''
                    },
                    autoDestroy:true,
                    items:[
                        {
                            xtype:'button',
                            iconCls:'createIcon',
                            handler:addParam
                        }
                    ],
                    resetParams:function () {
                        this.removeAll();
                        this.add({
                            xtype:'button',
                            iconCls:'createIcon',
                            handler:addParam
                        });
                    }
                },
                {
                    id:'rpcFormatField',
                    xtype:'radiogroup',
                    fieldLabel:'Response Format',
                    items:[
                        {
                            itemId:'plaintext',
                            boxLabel:'Plain Text',
                            name:'format',
                            inputValue:'text',
                            checked:true
                        },
                        {
                            itemId:'json',
                            boxLabel:'JSON',
                            name:'format',
                            inputValue:'json'
                        },
                        {
                            itemId:'xml',
                            boxLabel:'XML',
                            name:'format',
                            inputValue:'xml'
                        }
                    ]
                }
            ],
            buttons:[
                {
                    ui:'theme-colored',
                    itemId:'executeButton',
                    text:'Execute',
                    handler:function () {
                        var rpcResultPanel = Ext.getCmp('rpcResultPanel');
                        var formComponent = this.up('form');
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
                }
            ],
            dockedItems:[
                {
                    xtype:'linkbar',
                    dock:'top',
                    items:[
                        {
                            id:'vprExtractionRpcButton',
                            xtype:'button',
                            text:'VPR Extraction RPC',
                            handler:function () {
                                var form = this.up('form');
                                var ctx = Ext.getCmp('rpcContextField');
                                var name = Ext.getCmp('rpcNameField');
                                var params = Ext.getCmp('rpcParametersField');
                                var format = Ext.getCmp('rpcFormatField');
                                var rpcResultPanel = Ext.getCmp('rpcResultPanel');
                                ctx.setValue('VPR SYNCHRONIZATION CONTEXT');
                                name.setValue('VPR GET PATIENT DATA JSON');
                                format.down('#json').setValue(true);
                                rpcResultPanel.update('');
                                params.removeAll();
                                params.add([
                                    {
                                        xtype:'fieldcontainer',
                                        fieldLabel:'Patient DFN',
                                        labelSeparator:'',
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
                                            { xtype:'textfield', name:'params[0]', allowBlank:false, flex:1, tabIndex:6},
                                            { xtype:'button', iconCls:'deleteIcon', handler:removeParam},
                                            { xtype:'button', iconCls:'createIcon', handler:addParam}
                                        ]
                                    },
                                    {
                                        xtype:'fieldcontainer',
                                        fieldLabel:'Type',
                                        labelSeparator:'',
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
                                            {
                                                xtype:'combobox',
                                                name:'params[1]',
                                                flex:1,
                                                store:Ext.create('Ext.data.Store', {
                                                    fields:['text'],
                                                    data:Ext.Array.map(['accession', 'allergy', 'appointment', 'consult', 'document', 'factor' , 'immunization', 'lab', 'med', 'pharmarcy', 'rx', 'order', 'panel', 'patient', 'problem', 'procedure', 'radiology', 'reaction', 'surgery', 'visit', 'vital', 'vpr'], function (x) {
                                                        return {text:x};
                                                    })
                                                }),
                                                queryMode:'local',
                                                forceSelection:false,
                                                emptyText:'Select an Extraction Type...',
                                                tabIndex:7
                                            },
                                            { xtype:'button', iconCls:'deleteIcon', handler:removeParam},
                                            { xtype:'button', iconCls:'createIcon', handler:addParam}
                                        ]
                                    }
                                ]);
                            }
                        },
                        ' ',
                        {
                            xtype:'button',
                            text:'CPRS RPC',
                            handler:function () {
                                var ctx = Ext.getCmp('rpcContextField');
                                var name = Ext.getCmp('rpcNameField');
                                var params = Ext.getCmp('rpcParametersField');
                                var format = Ext.getCmp('rpcFormatField');
                                var rpcResultPanel = Ext.getCmp('rpcResultPanel');
                                ctx.setValue('OR CPRS GUI CHART');
                                format.down('#plaintext').setValue(true);
                                name.setRawValue('');
                                params.resetParams();
                                rpcResultPanel.update('');
                            }
                        },
                        ' ',
                        {
                            xtype:'button',
                            text:'Other',
                            handler:function () {
                                var ctx = Ext.getCmp('rpcContextField');
                                var name = Ext.getCmp('rpcNameField');
                                var params = Ext.getCmp('rpcParametersField');
                                var format = Ext.getCmp('rpcFormatField');
                                var rpcResultPanel = Ext.getCmp('rpcResultPanel');
                                ctx.setRawValue('');
                                name.setRawValue('');
                                format.down('#plaintext').setValue(true);
                                params.resetParams();
                                rpcResultPanel.update('');
                            }
                        }
                    ]
                }
            ],
            listeners:{
                boxready:function () {
                    Ext.getCmp('vprExtractionRpcButton').handler();
                }
            }
        },
        {
            xtype:'panel',
            id:'rpcResultPanel',
            title:'RPC Result',
            height:600,
            split:true,
            margin:'0 5 5 5',
            bodyPadding:5,
            border:true,
            autoScroll:true,
            tpl:'<pre>{[Ext.htmlEncode(values.responseText)]}</pre>'
        }
    ]
});
