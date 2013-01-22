Ext.define('EXT.DOMAIN.hmp.team.TeamManagementApplication', {
    extend:'EXT.DOMAIN.hmp.Application',
    requires:[
        "Ext.util.History",
        'EXT.DOMAIN.hmp.Viewport',
        'EXT.DOMAIN.hmp.team.TeamManagementPanel',
        'EXT.DOMAIN.hmp.team.TeamPositionPanel'
    ],
    controllers:[
        'EXT.DOMAIN.hmp.team.TeamManagementController',
        'EXT.DOMAIN.hmp.team.TeamPositionController'
    ],
    init:function () {
        Ext.util.History.init();
    },
    launch:function () {
        var me = this;

        var viewport = Ext.create('EXT.DOMAIN.hmp.Viewport', {
            items:[
                {
                    xtype:'treepanel',
                    itemId:'screenSelector',
                    ui: 'fubar',
                    title:'Team Config',
                    padding:'0 0 0 10',
                    minWidth:200,
                    width: 200,
                    region:'west',
                    split:true,
                    collapsible:true,
                    rootVisible:false,
                    lines:false,
                    useArrows:true,
                    store:Ext.create('Ext.data.TreeStore', {
                        storeId:'teamManagementScreens',
                        idProperty:'text',
                        fields:['text', 'view'],
                        root:{
                            expanded:true,
                            children:[
                                {
                                    text:'Stuff',
                                    expanded:true,
                                    children:[
                                        { leaf:true, text:'Configure Teams', view:'team-config' },
                                        { leaf:true, text:'Configure Team Categories', view:'category-config' },
                                        { leaf:true, text:'Configure Team Positions', view:'position-config' }
                                    ]
                                }
                            ]
                        }
                    })
                },
                {
                    xtype:'panel',
                    itemId:'screens',
                    region:'center',
                    height:'100%',
                    width:'100%',
                    layout:{
                        type:'card',
                        deferredRender:true
                    },
                    items:[
                        Ext.create('EXT.DOMAIN.hmp.team.TeamManagementPanel', {
                            itemId:'team-config'
                        }),
                        Ext.create('EXT.DOMAIN.hmp.team.TeamCategoriesPanel', {
                            itemId:'category-config'
                        }),
                        Ext.create('EXT.DOMAIN.hmp.team.TeamPositionPanel', {
                            itemId:'position-config'
                        })
                    ]
                }
            ]
        });

        var screenSelector = viewport.down('#screenSelector');

        screenSelector.on('select',
            function (tree, record, item, index, e) {
//                Ext.log("select()");
                if (!record.isLeaf()) {
                    me.setActiveScreen("");
                    return;
                }

                var view = record.get('view');
                me.setActiveScreen(view);
            });
        Ext.util.History.on('change', function (token) {
            if (!token || token.length == 0) return;
            // restore selection on tree
//            Ext.log("history.change(" + token + ")");
            var store = Ext.getStore('teamManagementScreens');
            var record = store.getRootNode().findChild('view', token, true);
            if (record) {
                screenSelector.getSelectionModel().suspendEvents();
                screenSelector.getSelectionModel().select(record);
                screenSelector.getSelectionModel().resumeEvents();
            }
        });

        // hold reference for convenience
        this.screens = viewport.down('#screens');

        // initially select page as stored in history
        var token = Ext.util.History.getToken();
        if (token && token.length > 0) {
            var store = Ext.getStore('teamManagementScreens');
            var record = store.getRootNode().findChild('view', token, true);
            if (record) {
                screenSelector.getSelectionModel().select(record);
            }
        } else {
            screenSelector.getSelectionModel().select(1);
        }
    },
    setActiveScreen:function (view) {
//        Ext.log("setActiveScreen(" + view + ")");
        if (view.length > 0) {
            var child = this.screens.down('#' + view);
            if (child) {
                this.screens.setVisible(true);
                this.screens.getLayout().setActiveItem(child);
            } else {
                this.screens.setVisible(false);
            }
        } else {
            this.screens.setVisible(false);
        }
//        Ext.log("history add(" + view + ")");
        Ext.util.History.add(view);
    }
});
