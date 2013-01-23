/*
 * Primary toolbar.
 * Support both authenticated and non-authenticated.
 * 
 * TODO: The MyApps menu colors should match the active theme somehow.
 * TODO: AppBar would be a great place to put a global user notification message (ie 'system going down in 5 min')
 * TODO: the currentApp setting doesn't really work well and is probably not necessary.
 * TODO: AppBar could display TEST/PRODUCTION/ETC. environment warning and debug info (VISTA account, IP, etc.)
 */
Ext.define('org.osehra.hmp.appbar.AppBar', {
    extend: 'Ext.toolbar.Toolbar',
    requires: [
        'org.osehra.hmp.AppContext',
        'org.osehra.hmp.UserContext',
        'org.osehra.hmp.appbar.ErrorManager',
        'org.osehra.hmp.appbar.UserButton',
        'org.osehra.cpe.TaskWindow',
        'org.osehra.cpe.ChatWindow'
    ],
    alias: 'widget.appbar',
    ui: 'hmp-appbar',
    id: 'AppBar',
    itemId: 'AppBar',
    centerAppMenu: false,
    autoRender: true,
    padding: '0 0 0 4',
    menus: {},
    defaults: {
        scale: 'small'
    },
    items: [
        {
            xtype: 'image',
            src: '/images/tri-16a.png',
            height: 16,
            width: 16
        },
        {
            xtype: 'button',
            text: 'My Apps',
            itemId: 'MyAppsID',
            hidden: true,
            menu: {
                minWidth: 150,
                showSeparator: false,
                items: []
            }
        },
        '-',
        {
            xtype: 'toolbar',
            ui: 'plain',
            flex: 1,
            itemId: 'AppMenuID',
            padding: 0,
            style: {
                border: 'none'
            },
            defaults: {
                scale: 'small'
            },
            items: []
        },
        '-',
        {
            xtype: 'userbutton',
            hidden: true,
            itemId: 'UserBtnID'
        },
        {
            xtype: 'button',
            text: 'Ping',
            hidden: true,
            listeners: {
                click: function(btn) {
                    var chatWindow = Ext.getCmp('chatWindow');
                    if (!chatWindow) chatWindow = Ext.create('org.osehra.cpe.ChatWindow', {});
                    range = btn.getPosition();
                    x = range[0];
                    y = range[1];
                    x = x - chatWindow.width + btn.getWidth();
                    y = y + btn.getHeight();
                    chatWindow.showAt(x,y);
                }
            }
        },
        {
            xtype: 'button',
            text: 'Task',
            hidden: true,
            listeners: {
                click: function(btn) {
                    var taskWindow = Ext.getCmp('taskWindow');
                    if (!taskWindow) taskWindow = Ext.create('org.osehra.cpe.TaskWindow', {
                    	task: {
                    		data: {
                    			'type':'General'
                    		}
                    	}
                    });
                    range = btn.getPosition();
                    x = range[0];
                    y = range[1];
                    x = x - taskWindow.width + btn.getWidth();
                    y = y + btn.getHeight();
                    taskWindow.showAt(x,y);
//                    taskWindow.showAt(btn.getPosition());
//                        taskWindow.show()
                }
            }
        },
        {
            xtype: 'button',
            text: 'Help',
            menu: {
                xtype: 'menu',
                items: [
                    /* TODO: Make this a window to direct submit JIRA issue!
                     {
                     text: 'Feedback',
                     icon: '/images/icons/email.png',
                     handler: function() {
                     org.osehra.hmp.appbar.ErrorManager.warn('Not implemented yet.  Sorry...', 5000);
                     }

                     },
                     */
                    {
                        text: 'HMP Wiki',
                        icon: '/images/icons/help.png',
                        href: 'https://localhost:8080/',
                        hrefTarget: '_BLANK'
                    },
                    {
                        text: 'New and Noteworthy!',
                        icon: '/images/icons/new.png',
                        href: 'https://localhost:8080/label/sandbox/releasenotes',
                        hrefTarget: '_BLANK'
                    },
                    {
                        xtype: 'menuseparator'
                    },
                    {
                        icon: '/images/tri-16a.png',
                        href: 'http://hi2.DOMAIN.EXT/',
                        hrefTarget: '_BLANK',
                        text: 'About the Health Informatics Initiative (HI<sup>2</sup>)'
                    },
                    {
                        text: 'Diagnostics',
                        handler: function() {
                            org.osehra.hmp.appbar.ErrorWindow.show();
                        }
                    },
                    {
                        canActivate: false,
                        disabled: true,
                        itemId: 'VersionInfoID'
                    }
                ]
            }
        }
    ],
    initComponent: function() {
        var me = this;

        if (this.centerAppMenu) {
        	this.items[3].layout = {type: 'hbox', pack: 'center'};
        }

        this.callParent(arguments);

        this.apps = {};

        me.authenticated = false;
        if (!org.osehra.hmp.UserContext.isAuthenticated())
            org.osehra.hmp.UserContext.on('userchange', this.onUserChange, this);
        else
            this.onUserChange(org.osehra.hmp.UserContext.getUserInfo());

        // place the version info into the menu
        var version = org.osehra.hmp.AppContext.getVersion();
        me.down('#VersionInfoID').text = "Version " + version;


        var appInfo = org.osehra.hmp.AppContext.getAppInfo();
        // add the menus
        var menus = appInfo.menus;
        if (Ext.isDefined(menus)) {
            for (var i = 0; i < menus.length; i++) {
                var menu = menus[i];
                me.addApp(menu.code, menu.name, menu.url, menu.menu);
            }
        }
    },
    addAppMenuItem: function(config) {
        this.down('#AppMenuID').add(config);
    },
    addApp: function(code, text, href, menu) {
        var menuitem = {
            code: code,
            text: text,
            href: href
        };

        // first append to the my apps button/menu
        if (text != '') {
            // generate the sub-menu if necessary
            if (menu && !this.menus[menu]) {
                this.menus[menu] = menu;
                var submenu = this.items.get(1).menu;
                submenu.add({text: menu, canActivate: false, disabled: true, plain: true, style: {padding: '3px', fontWeight: 'bold', backgroundColor: 'silver'}});
            }
            this.items.get(1).menu.add(menuitem);
        }

        // if this app is the current app, then update the button name
        if (href && window.location.href.indexOf(href) > 0) {
            this.down('#MyAppsID').setText(text);
        }

    },
    onUserChange: function(userInfo) {
        if (org.osehra.hmp.UserContext.isAuthenticated()) {
            this.items.each(function(it){
                it.setVisible(true);
            });
            this.add({href:'/auth/logout', hrefTarget:'_self', text:'Sign&nbsp;Out'});
        }
    }
});
