Ext.define('EXT.DOMAIN.hmp.appbar.ChangePasswordWindow', {
    xtype: 'cpwin',
    extend: 'Ext.window.Window',
    layout: 'fit',
    width: 100,
    height: 200,
    items: [
        {
            xtype: 'form',
            frame: false,
            border: 0,
            bodyPadding: 5,
            width: 300,
            height: 100,
            layout: 'anchor',
            defaults: {
                anchor: '100%',
                labelSeparator: ''
            },
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 120
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: 'Access Code',
                    itemId: 'accessCodeField',
                    name: 'j_access',
                    value: Ext.util.Cookies.get('access_code'),
                    inputType:'password',
                    allowBlank: false,
                    listeners: {
                        specialkey: function(field, e) {
                            if (e.getKey() == e.ENTER) {
                                field.up('cpwin').submitAuth();
                            }
                            else if(e.getKey() == e.TAB) {
                                field.nextSibling().focus();
                            }
                        }
                    }
                },
                {
                    fieldLabel: 'Verify Code',
                    itemId: 'verifyCodeField',
                    name: 'j_verify',
                    value: Ext.util.Cookies.get('verify_code'),
                    inputType:'password',
                    allowBlank: false,
                    listeners: {
                        specialkey: function(field, e) {
                            if (e.getKey() == e.ENTER) {
                                field.up('cpwin').submitAuth();
                            }
                            else if(e.getKey() == e.TAB) {
                                field.nextSibling().focus();
                            }
                        }
                    }
                },
                {
                    fieldLabel: 'New Verify Code',
                    itemId: 'newVerifyCodeField',
                    name: 'j_newVerify',
                    value: Ext.util.Cookies.get('verify_code'),
                    inputType:'password',
                    allowBlank: false,
                    listeners: {
                        specialkey: function(field, e) {
                            if (e.getKey() == e.ENTER) {
                                field.up('cpwin').submitAuth();
                            }
                            else if(e.getKey() == e.TAB) {
                                field.nextSibling().focus();
                            }
                        }
                    }
                },
                {
                    fieldLabel: 'Confirm Verify Code',
                    itemId: 'confirmVerifyCodeField',
                    name: 'j_confirmVerify',
                    value: Ext.util.Cookies.get('verify_code'),
                    inputType:'password',
                    allowBlank: false,
                    listeners: {
                        specialkey: function(field, e) {
                            if (e.getKey() == e.ENTER) {
                                field.up('cpwin').submitAuth();
                            }
                        }
                    }
                }
            ],
            buttons: [
                {
                    xtype: 'button',
                    text: 'Cancel',
                    handler: function(bn, e) {
                        bn.up('cpwin').hide();
                    }
                },
                {
                    xtype: 'button',
                    ui: 'theme-colored',
                    text: 'Change Verify Code',
                    handler: function(bn, e) {
                        bn.up('cpwin').doSubmit();
                    }
                }
            ]
        }
    ],
    doSubmit: function()
    {
        var frm = this.down('form');
        if(frm)
        {
            var vals = frm.getValues();
            Ext.apply(vals, {j_vistaId: Ext.state.Manager.get("vistaId"), j_division: Ext.state.Manager.get("vistaDiv")});
            Ext.Ajax.request({
                url: '/j_spring_security_check',
                method: 'POST',
                params: vals,
                success: function(resp) {
                    // Refresh window?
                    Ext.MessageBox.alert('Change Credentials', 'Credentials successfully changed.');
                },
                failure: function(resp) {
                    // Show specific error?
                    Ext.MessageBox.alert('Change Credentials', 'Credentials were not changed: '+resp.statusText);
                }
            });
        }
    }
});
