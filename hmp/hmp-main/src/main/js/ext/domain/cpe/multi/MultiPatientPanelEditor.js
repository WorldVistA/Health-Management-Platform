Ext.define('org.osehra.cpe.multi.MultiPatientPanelEditor', {
    extend:'Ext.panel.Panel',
    itemId:'mpePanel',
    requires:[
        'org.osehra.hmp.containers.GridBagLayout',
        'org.osehra.cpe.multi.MultiPatientPanelEditorModel',
        'org.osehra.cpe.multi.MultiPatientPanelColumnEditor',
        'org.osehra.cpe.viewdef.ViewDefGridPanel',
        'org.osehra.cpe.roster.RosterStore'
    ],
    alias:'widget.panelEditor',
    layout: 'hbox',
//    layout:{
//        type:'gridbag',
//        tableAttrs:{
//            style:{
//                width:'100%',
//                height:'100%'
//            }
//        }
//    },
    width: '100%',
    height: '100%',
    padding:'5 5 5 5',
    items:[
        {
//            gridX:0, gridY:1, widthX:1, widthY:1, weightX:1, weightY:8,
        	flex: 1,
            xtype:'grid',
            padding:'5 5 5 5',
            id:'mpeGrid',
            region:'west',
            title:'Multi-Patient Panels',
            store:{
                model:'org.osehra.cpe.multi.MultiPatientPanelEditorModel',
                proxy:{
                    type:'ajax',
                    url:'/config/panels',
                    reader:{
                        type:'json',
                        root:'panels'
                    }
                },
                autoLoad:true
            },
            columns:[
                {text:'Panel Name', dataIndex:'name'}
            ],
            tbar:{
                items:[
                    {
                        xtype:'button',
                        ui:'link',
                        padding:'5 5 5 5',
                        text:'Create Board',
                        handler:function (bn, e) {
                            Ext.Msg.prompt('Name', 'Please enter a name for the new Panel:', function (btn, text) {
                                if (btn == 'ok') {
                                    Ext.Ajax.request({
                                        url:'/config/addPanel',
                                        method:'POST',
                                        params:{name:text, primaryViewDefClassName:'org.osehra.cpe.vpr.queryeng.dynamic.PatientPanelViewDef'},
                                        success:function (response, opts) {
                                            this.store.load();
                                        },
                                        failure:function (response, opts) {
                                            console.log(response);
                                        },
                                        scope:this
                                    });
                                }
                            }, bn.up('#mpeGrid'));
                        }
                    },
                    {
                        xtype:'button',
                        ui:'link',
                        padding:'5 5 5 5',
                        text:'Preview',
                        handler:function (bn, e) {
                        	var window = new Ext.window.Window({
                        		width: 400,
                        		height: 100,
                        		title: 'Panel Preview',
                        		layout: {type: 'fit', align: 'stretch'},
                        		items: [
									{
									  padding:'5 5 5 5',
									  xtype:'combobox',
									  itemId:'mpeRosterPicker',
									  queryMode:'local',
									  queryParam:'filter',
									  grow:true,
									  fieldLabel:'Select Roster for Preview',
									  emptyText:'<Select Patient List>',
									  typeAhead:true,
									  allowBlank:false,
									  forceSelection:true,
									  displayField:'name',
									  valueField:'id',
									  store: Ext.getStore('rosters') ? Ext.getStore('rosters') : Ext.create('org.osehra.cpe.roster.RosterStore')
									}
                        		],
                        		bbar: {
                        			items: [
                        			    {xtype: 'tbfill'},
                        			    {
                        			    	text: 'OK',
                        			    	xtype: 'button',
                        			    	handler: function(bn) {
                                                var gpanel = Ext.ComponentQuery.query('#mpePanel')[0].down('#mpeGrid')
                                                var gsel = gpanel.getSelectionModel().getSelection();
                                                if (gsel && gsel.length > 0) {
                                                    var selViewID = gsel[0].get('name');
                                                    var picker = bn.up('window').down('combobox');
                                                    var rosterId = picker.getValue();
                                                    if (rosterId && rosterId > 0) {
                                                    	Ext.util.Cookies.set('BoardBuilderRosterPreviewID', rosterId);
                                			    		bn.up('window').close();
                                                        var window = new Ext.window.Window({
                                                            width:800,
                                                            height:400,
                                                            title:'Panel Preview',
                                                            layout:{type:'fit', align:'stretch'},
                                                            items:[
                                                                {
                                                                    xtype:'viewdefgridpanel',
                                                                    id:'mpeViewDefPreviewPanel',
                                                                    viewParams:{
                                                                        'roster.ien':rosterId
                                                                    },
                                                                    viewID:selViewID,
                                                                    addFilterTool:true,
                                                                    title:selViewID
                                                                }
                                                            ]
                                                        });
                                                        window.down('#mpeViewDefPreviewPanel').setViewDef(selViewID, {'roster.ien':rosterId});
                                                        window.show();
                                                    }
                                                    else {
                                                        Ext.MessageBox.alert('No Roster Selected', 'You must first select a Roster to show in the preview.');
                                                    }
                                                } else {
                                                    Ext.MessageBox.alert('Error Loading Preview', 'You must first select the desired Panel to preview.');
                                                }
                        			    	}
                        			    },
                        			    {
                        			    	text: 'Cancel',
                        			    	xtype: 'button',
                        			    	handler: function(bn) {
                        			    		bn.up('window').close();
                        			    	}
                        			    }
                        			]
                        		}
                        	});

                        	window.show();
                        	
                        	var rid = Ext.util.Cookies.get('BoardBuilderRosterPreviewID');
                        	if(rid) {
                        		var box = window.down('combobox');
                        		box.store.load();
                        		box.setValue(rid);
                        	}
                        }
                    },
                    {
                        xtype:'button',
                        ui:'link',
                        padding:'5 5 5 5',
                        text:'Delete',
                        handler:function (bn, e) {
                            var gpanel = bn.up('#mpePanel').down('#mpeGrid')
                            var gsel = gpanel.getSelectionModel().getSelection();
                            if (gsel && gsel.length > 0) {
                                var panelName = gsel[0].get('name');
        				    	Ext.Ajax.request({
        							url: '/config/dropPanel',
        							method: 'POST',
        							params: {'panelName': panelName},
        							success: function(response, opts) {
        								gpanel.getStore().load();
        							},
        							failure: function(response, opts) {
        								console.log(response);
        							},
        							scope: this
        						});
                            } else {
                                Ext.MessageBox.alert('Error Deleting', 'You must first select a Panel to delete.');
                            }
                        }
                    }
                ]
            },
            listeners:{
                selectionchange:{
                    fn:function (selMdl, selData, eOpts) {
                        var mpepnl = Ext.ComponentQuery.query('#mpePanel')[0];
                        var pnl = mpepnl.down('#mpeColPanel');
                        if (selData.length > 0) {
                            var pnlId = selData[0].get('id');
                            var pnlName = selData[0].get('name');
                            var colEditor;
                            if (pnl.down('#mpeColPanelEditor') == null) {
                                colEditor = Ext.create('widget.panelColumnEditor');
                                pnl.removeAll();
                                pnl.add(colEditor);
                            } else {
                                colEditor = pnl.down('#mpeColPanelEditor');
                                colEditor.down('mpecoloptions').removeAll();
                            }
                            if (colEditor && colEditor.panelName != pnlName) {
                                colEditor.setPanelName(pnlName);
                                colEditor.setTitle('"' + pnlName + '" Column List');
                            }
                        } else {
                            pnl.removeAll();
                        }
                    }
                }
            }
        },
        {
//            gridX:1, gridY:0, widthX:1, widthY:2, weightX:4, weightY:1,
            flex: 3,
            xtype:'panel',
            padding:'5 5 5 5',
            id:'mpeColPanel',
            layout:{type:'fit', align:'stretch'}
        }
    ]
});
