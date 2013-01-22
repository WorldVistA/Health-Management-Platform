Ext.define('EXT.DOMAIN.hmp.team.TeamManagementPanel', {
    extend:'Ext.container.Container',
    requires:[
        'EXT.DOMAIN.hmp.team.StaffAssignmentPanel',
        'EXT.DOMAIN.hmp.team.TeamPositionField',
        'EXT.DOMAIN.hmp.PopUpButton',
        'EXT.DOMAIN.hmp.team.TeamStore',
        'EXT.DOMAIN.cpe.roster.RosterStore',
        'EXT.DOMAIN.cpe.roster.RosterPicker',
        'EXT.DOMAIN.hmp.team.PersonField',
        'EXT.DOMAIN.hmp.team.PersonPicker',
        'EXT.DOMAIN.hmp.team.PatientPicker',
        'EXT.DOMAIN.hmp.team.PatientField',
        'EXT.DOMAIN.cpe.viewdef.ViewDefGridPanel',
        'EXT.DOMAIN.hmp.team.SearchableList',
        'EXT.DOMAIN.cpe.multi.MultiPatientPanelEditorModel'
    ],
    padding:10,
    layout:'border',
    items:[
        {
            xtype:'grid',
            componentCls: 'hmp-bubble',
            itemId:'teamList',
            title:'Teams',
            region:'west',
            minWidth:300,
            width:300,
            emptyText: 'No Teams Found',
            columns:[
                { text:'Name', dataIndex:'displayName', flex:2 },
                { text:'Owner', dataIndex:'ownerName', flex:1 },
                {
                    xtype:'actioncolumn',
                    width:20,
                    items:[
                        {
                            cls:'hmp-delete-icon',
                            tooltip:'Remove Team',
//                            handler: function(grid, rowIndex, colIndex) {
//                                var rec = grid.getStore().getAt(rowIndex);
//                                alert("Terminate " + rec.get('firstname'));
//                            }
                        }
                    ]
                }
            ],
            tools:[
                {
                    type:'plus',
                    itemId:'createTeamButton',
                    tooltip:'New Team'
                }
            ]
        },
        {
            xtype:'form',
            componentCls: 'hmp-bubble',
            itemId:'teamEdit',
            region:'center',
//            width: '100%',
            hidden:true,
            margin:10,
            autoScroll:true,
            defaults:{
                anchor:'100%'
            },
            layout:'anchor',
            items:[
                {
                    xtype:'component',
                    itemId:'teamNameField',
                    autoEl:'h1',
                    html:'Team Name'
                },
                {
                    xtype:'displayfield',
                    itemId:'ownerNameField',
                    cls: 'hmp-label',
                    fieldLabel:'Owner',
                    name:'ownerName'
                },
                {
                    xtype:'component',
                    html:'<h2>Patients</h2>'
                },
                {
//                    title:'Patients',
                    itemId:'patientList',
                    xtype:'viewdefgridpanel',
                    region:'center',
                    header:false,
                    forceFit:true,
                    patientAware:false,
                    collapsible: true,
                    collapseGridIfEmpty:false,
                    disableSelection:true,
                    scroll:false,
                    minHeight:100,
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.dynamic.PatientPanelViewDef',
                    emptyText:'There are no patients associated with this team.',
                    columns:[
                        {
                            xtype:'templatecolumn',
                            text:'Name',
                            dataIndex:'name',
                            flex:1,
                            tpl:'<img src="{photoHref}" width="24" height="24" style="vertical-align: middle"/><span style="margin-left: 4px;line-height: 24px">{name}</span>'
                        },
                        {
                            text:'Last 4',
                            dataIndex:'last4',
                            width:48
                        },
                        {
                            xtype:'hl7dtmcolumn',
                            text:'DOB',
                            dataIndex:'dob',
                            width:75
                        },
                        {
                            text:'Age',
                            dataIndex:'age',
                            width:30
                        },
                        {
                            text:'F/M',
                            dataIndex:'gender',
                            width:30
                        }
                    ],
//                    viewConfig: {
//                        plugins: {
//                            ptype: 'gridviewdragdrop',
//                            ddGroup: 'PatientGroup',
//                            enableDrop: false
//                        },
//                        style: { overflow: 'auto', overflowX: 'hidden' }
//                    },
                    tools:[],
                    bbarConfig:[
                        {
                            xtype:'patientfield',
                            emptyText:'Add Patient'
                        },
//                        {
//                            icon:'/images/icons/ic_plus.png',
//                            tooltip:'Add Patient'
//                        },
                        '->',
                        {
                            xtype:'combobox',
                            itemId: 'setTeamPatientsButton',
                            emptyText:'Choose another Patient List',
                            editable:false,
                            hideTrigger: true,
                            displayField: 'name',
                            valueField: 'id',
                            store:'rosters'

//                            ui:'link',
//                            popUp:{
//                                height:300,
//                                width:300,
//                                layout:'fit',
//                                items:[
//                                    {
//                                        xtype:'boundlist',
//                                        store:Ext.create('EXT.DOMAIN.cpe.roster.RosterStore'),
//
////                                        columns:[
////                                            {text:'Patient List', flex:1}
////                                        ]
//                                    }
////                                    {
////                                        xtype:'rosterpicker',
////                                        name:'roster',
////                                        fieldLabel:'Patient List'
////                                    }
//                                ]
//                            },
//                            popUpButtons:[]
                        }
                    ]
                },
                {
                    xtype:'component',
                    html:'<h2>Staff</h2>'
                },
                {
                    xtype: 'staffeditor',
                    itemId: 'staffList'
                }
            ],
            fbar:[
                {
                    text:'Save',
                    itemId:'saveTeamButton'
                }
            ]
        },
        {
            xtype:'container',
            region:'east',
            split: true,
            collapsible: true,
            width:200,
            minWidth:200,
            layout:{
                type:'vbox',
                align:'stretch',
                defaultMargins: '3px 0px'
            },
            items:[
                {
                    xtype:'patientpickerfoo',
                    componentCls: 'hmp-bubble',
                    flex:1
                },
                {
                    xtype: 'searchablelist',
                    componentCls: 'hmp-bubble',
                    flex: 1,
                    emptyText: "Search Boards",
                    displayField: 'name',
                    store:{
                        model:'EXT.DOMAIN.cpe.multi.MultiPatientPanelEditorModel',
                        proxy:{
                            type:'ajax',
                            url:'/config/panels',
                            reader:{
                                type:'json'
//                                root:'panels'
                            }
                        },
                        autoLoad:true
                    }
                },
                {
                    xtype:'personpicker',
                    componentCls: 'hmp-bubble',
                    flex:1
                }
            ]
        }
    ],
    initComponent:function () {
        this.items[0].store = Ext.data.StoreManager.containsKey('teams') ? Ext.getStore('teams') : Ext.create('EXT.DOMAIN.hmp.team.TeamStore');
        this.items[1].items[3].bbarConfig[2].store = Ext.data.StoreManager.containsKey('rosters') ? Ext.getStore('rosters') : Ext.create('EXT.DOMAIN.cpe.roster.RosterStore');

        this.callParent(arguments);
    },
    onBoxReady:function () {
        this.callParent(arguments);
        this.down('#teamList').getStore().load();
//        Ext.getStore('teams').load();
    }
});
