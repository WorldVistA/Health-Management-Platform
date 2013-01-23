Ext.define('org.osehra.hmp.auth.Login', {
    extend: 'org.osehra.hmp.Application',
    requires: [
        'org.osehra.hmp.Viewport',
        'org.osehra.hmp.AppContext'
    ],
    launch:function() {
        var vistaAccountsStore = Ext.create('Ext.data.Store', {
            autoLoad:true,
            fields:['name', 'vistaId', 'division', 'host', 'port'],
            proxy:{
                type:'ajax',
                url:"accounts",
                extraParams:{
                    format:'json'
                },
                reader:{
                    type:'json',
                    root:'data.items'
                }
            },
            listeners:{
                'load':function () {
                    // save selected division
                    var vistaId = Ext.state.Manager.get("vistaId");
                    var formCmp = Ext.ComponentQuery.query("form")[0];
                    var form = formCmp.getForm();
                    var vistaIdField = form.findField('j_vistaId');
                    if (!Ext.isEmpty(vistaId)) {
                        var vistaAccount = vistaIdField.store.findRecord("vistaId", vistaId);
                        if (vistaAccount != null) {
                            vistaIdField.setValue(vistaAccount);
                            form.findField('j_access').focus();

                            var welcomeArea = Ext.ComponentQuery.query('#welcomeArea')[0];
                            welcomeArea.setValue('');
                            var welcomeAreaLoader = welcomeArea.getLoader();
                            welcomeAreaLoader.load({
                                params:{
                                    vistaId:vistaAccount.get('vistaId'),
                                    host:vistaAccount.get('host'),
                                    port:vistaAccount.get('port')
                                }
                            });
                        }
                    } else {
                        vistaIdField.focus();
                    }
                }
            }
        });

        Ext.create('org.osehra.hmp.Viewport', {
            items: [
                {
                    xtype:'container',
                    componentCls: 'hmp-bubble',
                    region: 'center',
                    padding:'12 0 0 0',
                    layout:{
                        type:'vbox',
                        align:'center'
                    },
                    items:[
                        {
                            xtype:'component',
                            html:'<h1>Health Management Platform Sign In</h1>'
                        },
                        {
                            xtype:'component',
                            html:'<p class="hmp-label">'+ org.osehra.hmp.AppContext.getVersion()+'</p>',
                            height:60
                        },
                        {
                            type:'container',
                            width:566,
                            height:480,
                            border:0,
                            layout:{
                                type:'absolute'
                            },
                            items:[
                                {
                                    xtype:'textarea',
                                    itemId:'welcomeArea',
                                    cls:'hmp-monospaced',
                                    x:0,
                                    y:0,
                                    height:200,
                                    width: 566,
                                    autoScroll:true,
                                    readOnly:true,
                                    value: '                              No Facility Selected', // 30 character padding
                                    loader:{
                                        url:'/auth/welcome',
                                        method:'post',
                                        loadMask:true,
                                        renderer:function (loader, response, active) {
                                            var text = response.responseText;
                                            loader.getTarget().setValue(text);
                                            return true;
                                        }
                                    }
                                },
                                {
                                    xtype:'container',
                                    layout:{
                                        type: 'vbox',
                                        align: 'center'
                                    },
                                    x:0,
                                    y:206,
                                    height: 20,
                                    items:[
                                        {
                                            xtype:'component',
                                            height:20,
                                            itemId:'message',
                                            cls:'hmp-error',
                                            html: location.search.indexOf('msg') != -1 ? Ext.Object.fromQueryString(location.search).msg : ''
                                        }
                                    ]
                                },
                                {
                                    xtype:'image',
                                    src:'/images/hi2_logo64.png',
                                    x:0,
                                    y:232
                                },
                                {
                                    xtype:'form',
                                    frame:false,
                                    border:0,
                                    x:166,
                                    y:232,
                                    width:400,

                                    // Fields will be arranged vertically, stretched to full width
                                    layout:'anchor',
                                    defaults:{
                                        anchor:'100%',
                                        labelSeparator:''
                                    },

                                    // The fields
                                    fieldDefaults:{
                                        labelAlign:'right'
                                    },
                                    defaultType:'textfield',
                                    items:[
                                        {
                                            xtype:'combobox',
                                            itemId:'divisionCombo',
                                            name:'j_vistaId',
                                            fieldLabel:'Facility',
                                            autoSelect:false,
                                            emptyText:'-- select one --',
                                            store:vistaAccountsStore,
                                            valueField:'vistaId',
                                            displayField:'name',
                                            editable:false,
                                            disableKeyFilter:true,
                                            queryMode:'local',
                                            allowBlank:false,
                                            forceSelection:true,
                                            msgTarget:"side",
                                            listeners:{
                                                select:function (comboBox, value) {
                                                    var vistaId = value[0].get("vistaId");
                                                    var formCmp = Ext.ComponentQuery.query("form")[0];
                                                    var form = formCmp.getForm();
                                                    var vistaIdField = form.findField('j_vistaId');
                                                    if (!Ext.isEmpty(vistaId)) {
                                                        var vistaAccount = vistaIdField.store.findRecord("vistaId", vistaId);
                                                        if (vistaAccount != null) {
                                                            form.findField('j_access').focus();

                                                            var welcomeArea = Ext.ComponentQuery.query('#welcomeArea')[0];
                                                            welcomeArea.setValue('');
                                                            var welcomeAreaLoader = welcomeArea.getLoader();
                                                            welcomeAreaLoader.load({
                                                                params:{
                                                                    vistaId:vistaAccount.get('vistaId'),
                                                                    host:vistaAccount.get('host'),
                                                                    port:vistaAccount.get('port')
                                                                }
                                                            });
                                                        }
                                                    }
                                                    vistaIdField.focus();
                                                }
                                            }
                                        },
                                        {
                                            fieldLabel:'Access Code',
                                            itemId:'accessCodeField',
                                            name:'j_access',
                                            value:Ext.util.Cookies.get('access_code'),
                                            inputType:'password',
                                            allowBlank:false,
                                            listeners:{
                                                specialkey:function (field, e) {
                                                    if (e.getKey() == e.ENTER) {
                                                        submitAuth();
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            fieldLabel:'Verify Code',
                                            itemId:'verifyCodeField',
                                            name:'j_verify',
                                            value:Ext.util.Cookies.get('verify_code'),
                                            inputType:'password',
                                            allowBlank:true,
                                            listeners:{
                                                specialkey:function (field, e) {
                                                    if (e.getKey() == e.ENTER) {
                                                        submitAuth();
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            fieldLabel:'New Verify Code',
                                            itemId:'newVerifyCodeField',
                                            name:'j_newVerify',
                                            value:Ext.util.Cookies.get('verify_code'),
                                            inputType:'password',
                                            allowBlank:true,
                                            disabled:true,
                                            hidden:true,
                                            listeners:{
                                                specialkey:function (field, e) {
                                                    if (e.getKey() == e.ENTER) {
                                                        submitAuth();
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            fieldLabel:'Confirm Verify Code',
                                            itemId:'confirmVerifyCodeField',
                                            name:'j_confirmVerify',
                                            value:Ext.util.Cookies.get('verify_code'),
                                            inputType:'password',
                                            allowBlank:true,
                                            disabled:true,
                                            hidden:true,
                                            listeners:{
                                                specialkey:function (field, e) {
                                                    if (e.getKey() == e.ENTER) {
                                                        submitAuth();
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    buttons:[
                                        {
                                            text:'Sign In',
                                            ui:'theme-colored',
                                            scale:'medium',
                                            handler:submitAuth
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        });

        function submitAuth() {
            // check for validity
            var formComponent = Ext.ComponentQuery.query('form')[0];
            var form = formComponent.getForm();
            var msgComponent = Ext.ComponentQuery.query('#message')[0];
            msgComponent.update('');
            var vistaIdField = form.findField('j_vistaId');
            var vistaId = vistaIdField.getValue();
            if (form.isValid()) {
                formComponent.setLoading("Authenticating...", true);
                formComponent.down('button').disable();
                form.submit({
                    url:'${request.contextPath}/j_spring_security_check',
                    params:{
                        'j_division':vistaAccountsStore.findRecord('vistaId', vistaId).get('division')
                    },
                    success:function (form, action) {
                        msgComponent.removeCls('hmp-error');
                        msgComponent.addCls('hmp-success');
                        msgComponent.update('Authentication Successful');
                        formComponent.setLoading(false);
                        var json = Ext.decode(action.response.responseText);
                        window.location = json.data.targetUrl;
                        Ext.state.Manager.set("DUZ", json.DUZ);
                    },
                    failure:function (form, action) {
                        formComponent.setLoading(false);
                        formComponent.down('button').enable();
                        var json = Ext.decode(action.response.responseText);
                        msgComponent.removeCls('hmp-success');
                        msgComponent.addCls('hmp-error');
                        if (json.error.code === 401 && json.error.message === "VERIFY CODE must be changed before continued use.") {
                            formComponent.down('#newVerifyCodeField').enable();
                            formComponent.down('#confirmVerifyCodeField').enable();
                            formComponent.down('#newVerifyCodeField').show();
                            formComponent.down('#confirmVerifyCodeField').show();
                            formComponent.down('button').setText('Change Verify Code');
                        }
                        msgComponent.update(json.error.message);
                        //formComponent.doLayout();
                    }
                });
            }
            // save selected division
            if (!Ext.isEmpty(vistaIdField)) {
                Ext.state.Manager.set("vistaId", vistaId);
                Ext.state.Manager.set("vistaDiv", vistaAccountsStore.findRecord('vistaId', vistaId).get('division'));
            }
        }
    }
});
