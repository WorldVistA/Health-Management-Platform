Ext.define('EXT.DOMAIN.hmp.appbar.UserButton', {
    extend:'Ext.button.Button',
    requires:[
        'EXT.DOMAIN.hmp.UserContext',
        'EXT.DOMAIN.hmp.appbar.PrefWin'
    ],
    uses:[
        'EXT.DOMAIN.hmp.appbar.ChangePhotoWindow',
        'EXT.DOMAIN.hmp.appbar.ChangePasswordWindow'
    ],
    alias:'widget.userbutton',
    cls:'hmp-user-button',
    text:'[User Name]',
    menu:{
        plain:true,
        items:[
            {
                xtype:'panel',
                height:160,
                width:400,
                padding:6,
                layout:'hbox',
                items:[
                    {
                        xtype:'image',
                        itemId:'userPicture',
                        minWidth:74
                    },
                    {
                        xtype:'container',
                        defaults:{
                            xtype:'displayField',
                            cls:'hmp-label',
                            labelAlign:'right',
                            labelSeparator:''
                        },
                        items:[
                            {
                                xtype:'displayfield',
                                itemId:'userName'
                            },
                            {
                                xtype:'displayfield',
                                fieldLabel:'Title',
                                itemId:'userTitle'
                            },
                            {
                                xtype:'displayfield',
                                fieldLabel:'Facility',
                                itemId:'userDivision'
                            },
                            {
                                xtype:'displayfield',
                                fieldLabel:'Service/Section',
                                itemId:'userService'
                            }
                        ]
                    }
                ],
                tbar:[
                    {
                        xtype:'button',
                        itemId:'changePhotoButton',
                        ui:'link',
                        text:'Change Photo'
                    }
                ],
                fbar:[
                    {
                        xtype:'button',
                        itemId:'cvcButton',
                        text:'Change Verify Code',
                        handler:function () {
                            var cpwin = Ext.create('EXT.DOMAIN.hmp.appbar.ChangePasswordWindow', {
                                itemId:'ChangePasswordWindowID',
                                modal:true,
                                minHeight:200, minWidth:300,
                                title:'Change Credentials'
                            });
                            cpwin.show();
                        }
                    },
                    '->',
                    {
                        xtype:'button',
                        itemId:'UserPrefID',
                        ui:'theme-colored',
                        text:'Edit User Preferences'
                    }
                ]
            }
        ]
    },
    initComponent:function () {
        var me = this;

        me.callParent(arguments);

        if (EXT.DOMAIN.hmp.UserContext.isAuthenticated()) {
            me.setText(EXT.DOMAIN.hmp.UserContext.getUserInfo().displayName);
            me.refreshPhoto();
            me.menu.down('#userTitle').setValue(EXT.DOMAIN.hmp.UserContext.getUserInfo().title);
            me.menu.down('#userDivision').setValue(EXT.DOMAIN.hmp.UserContext.getUserInfo().divisionName);
            me.menu.down('#userService').setValue(EXT.DOMAIN.hmp.UserContext.getUserInfo().serviceSection);
        }

        me.menu.down('#changePhotoButton').on('click', function () {
            var win = Ext.create('EXT.DOMAIN.hmp.appbar.ChangePhotoWindow', {
                title:'Edit User Photo',
                modal:true,
                listeners:{
                    load:function () {
                        me.refreshPhoto();
                    }
                }
            });
            win.show();
        });

        me.menu.down('#UserPrefID').on('click', function () {
            if (!me.prefwin) {
                me.prefwin = Ext.create('EXT.DOMAIN.hmp.appbar.PrefWin');
            }
            me.prefwin.show();
        });
    },
    refreshPhoto:function () {
        var me = this;
        var pnl = me.menu.down('panel');
        var photoUrl = '/person/v1/' + EXT.DOMAIN.hmp.UserContext.getUserInfo().uid + '/photo?_dc=' + (new Date().getTime());
//        console.log("refreshPhoto(" + photoUrl + ")");

        pnl.remove(pnl.getComponent(0), true);
        pnl.insert(0, {
            xtype:'image',
            src:photoUrl,
            minWidth:76
        });
        pnl.doLayout();

        me.setIcon(null);
        me.setIcon(photoUrl);
    }
});
