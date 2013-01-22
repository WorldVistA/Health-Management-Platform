/**
 * this is a private inner grid for the roster window
 * @private
 */
Ext.define('EXT.DOMAIN.cpe.roster.RosterWindowSrcGrid', {
    extend:'Ext.grid.Panel',
    alias: 'widget.rosterwindowsrcgrid',
    height:225,
    enableColumnHide:false,
    enableColumnMove:false,
    enableColumnResize:false,
    selType:'cellmodel',
    plugins:[
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit:1
        })
    ],
    store:Ext.create('Ext.data.Store', {
        fields:['seq', 'type', 'name','id', 'operation'],
        autoLoad:false
    }),
    columns:[
        {
            xtype:'actioncolumn',
            itemId:'RowMoveColID',
            width:45,
            menuDisabled:true, sortable:false,
            items:[
                {iconCls:'upBtn', tooltip:'Move Up', handler:function (grid, rowIdx, colIdx) {
                    this.up('#RosterWinID').moveRow(grid, rowIdx, -1)
                }},
                {iconCls:'downBtn', tooltip:'Move Down', handler:function (grid, rowIdx, colIdx) {
                    this.up('#RosterWinID').moveRow(grid, rowIdx, 1)
                }}
            ]
        },
        {
            header:'Type',
            width:75, dataIndex:'type',
            menuDisabled:true, sortable:false,
            editor:{
                xtype:'combobox',
                defaultListConfig:{minWidth:200},
                forceSelection:true,
                queryMode:'local',
                allowBlank:false,
                displayField:'name',
                valueField:'value',
                store:Ext.create('Ext.data.Store', {
                    fields:['name', 'value'],
                    data:[
                        {name:'Clinic', value:'Clinic'},
                        {name:'Ward', value:'Ward'},
                        {name:'OE/RR', value:'OE/RR'},
                        {name:'PCMM Team', value:'PCMM Team'},
                        {name:'Provider', value:'Provider'},
                        {name:'Reminder List', value:'PXRM'},
                        {name:'Specialty', value:'Specialty'},
                        {name:'Patient', value:'Patient'},
                        {name:'VPR Roster', value:'VPR Roster'}
                    ]
                })
            }
        },
        {
            header:'Value', dataIndex:'name',
            menuDisabled:true, sortable:false, flex:1,
            editor:{
                xtype:'combobox', allowBlank:false, displayField:'name', valueField:'id',
                hideTrigger:true,
                listConfig:{
                    minHeight:50,
                    emptyText:'No matching records found...',
                    loadingText:'Searching....'
                },
                store:Ext.create('Ext.data.Store', {
                    fields:['name', 'id'],
                    proxy:{
                        type:'ajax',
                        url:'/roster/source',
                        extraParams:{
                            id:'Patient'
                        },
                        reader:{
                            root:'data',
                            type:'json'
                        }
                    }
                }),
                emptyText:'Select a value...',
                forceSelection:true,
                queryParam:'filter',
                minChars:4,
                queryMode:'remote',
                listeners:{
                    select:function (combo, recs) {
                        // both the displayField and valueField must end up in the edited record
                        // this seems like the only way I can figure to get access to the displayField later.
                        combo.lastDisplay = recs[0].data.name;
                    }
                }
            }
        },
        {
            header:'Operation', width:60,
            dataIndex:'operation', menuDisabled:true, sortable:false,
            renderer:function (val) {
                if (val == 'Union') {
                    return 'Include';
                } else if (val == 'Difference') {
                    return 'Exclude';
                }
                return val;
            },
            editor:{
                xtype:'combobox',
                defaultListConfig:{minWidth:200},
                queryMode:'local',
                allowBlank:false,
                forceSelection:true,
                displayField:'name',
                valueField:'value',
                store: Ext.create('Ext.data.Store', {
                    fields:['name', 'value'],
                    data:[
                        {name:'Include (Union)', value:'Union'},
                        //{name: 'Intersection', value: 'Intersection'}, // Too advanced for now?
                        {name:'Exclude (Difference)', value:'Difference'}
                    ]
                })
            }
        },
        {
            xtype:'actioncolumn',
            width:45,
            menuDisabled:true, sortable:false,
            items:[
                {iconCls:'insertBtn', tooltip:'Insert Row', handler:function (grid, rowIdx, colIdx) {
                    this.up('#RosterWinID').addRow(grid, rowIdx + 1)
                }},
                {iconCls:'deleteBtn', tooltip:'Delete Row', handler:function (grid, rowIdx, colIdx) {
                    this.up('#RosterWinID').removeRow(grid, rowIdx)
                }}
            ]
        }
    ]
});
