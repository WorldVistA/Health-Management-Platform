Ext.define('org.osehra.hmp.team.PatientPicker', {
    extend:'Ext.container.Container',
    requires:[
        'org.osehra.hmp.SegmentedButton',
        'org.osehra.hmp.team.SearchableList',
        'org.osehra.hmp.team.PersonStore'
    ],
    alias:'widget.patientpickerfoo',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    items:[
        {
            xtype:'container',
            layout:{
                type:'hbox',
                align:'stretch'
            },
            items:[
                {
                    xtype:'button',
                    itemId:'searchByButton',
                    width:22,
                    icon:'/images/icons/magnifying_glass_16x16.png',
                    menu:{
                        plain:true,
                        bodyPadding:4,
                        layout:{
                            type:'vbox',
                            align:'stretch',
                            margin:'1px 0px 1px 0px'
                        },
                        items:[
                            {
                                xtype:'segmentedbutton',
                                allowDepress:true,
                                defaults: {
                                    flex: 1
                                },
                                items:[
                                    {
                                        text:'Clinic',
                                        pressed:true
                                    },
                                    {
                                        text:'Provider'
                                    },
                                    {
                                        text:'Specialty'
                                    },
                                    {
                                        text:'Ward'
                                    }
                                ]
                            },
                            {
                                xtype:'searchablelist',
                                emptyText:'Select a Clinic',
                                displayField:'name',
                                store:{
                                    fields:['id', 'name', 'division', 'service'],
                                    proxy:{
                                        type:'ajax',
                                        url:'/roster/source',
                                        extraParams:{
                                            id:'Clinic'
                                        },
                                        reader:{
                                            type:'json',
                                            root:'data'
                                        }
                                    },
                                    autoLoad:true
                                }
                            }
                        ]
                    }
                },
                {
                    xtype:'textfield',
                    itemId: 'patientSearchField',
                    emptyText:'Search All Patients',
                    flex:1
                }
            ]
        },
        {
            xtype: 'dataview',
            store: {
                fields:['id', 'name', 'photoHref'],
                proxy:{
                    type:'ajax',
                    url:'/roster/source',
                    extraParams:{
                        id:'Patient'
                    },
                    reader:{
                        type:'json',
                        root:'data'
                    }
                }
            },
            tpl:new Ext.XTemplate(
                '<ul class="hmp-person-list-ct">' +
                    '<tpl for=".">' +
                    '<li class="hmp-person-list-item">' +
                    '<img src="{photoHref}"/>' +
                    '<span>{name}</span>' +
                    '</li>' +
                    '</tpl>' +
                    '</ul>'
            ),
            itemSelector:'li.hmp-person-list-item',
            emptyText:'No matching patients',
            flex:1,
            overflowY:'auto',
            overflowX:'hidden',
            trackOver:true,
            overItemCls:'x-boundlist-item-over', // borrow some styling from combobox boundlist
            selectedItemCls: 'x-boundlist-selected'
        }
//        {
//            xtype:'combobox',
//            header:false,
//            store:Ext.create('Ext.data.Store', {
//                fields:['name', 'id'],
//                proxy:{
//                    type:'ajax',
//                    url:'/roster/source',
//                    extraParams:{
//                        id:'Patient'
//                    },
//                    reader:{
//                        root:'data',
//                        type:'json'
//                    }
//                }
//            }),
//            displayField:'name',
//            emptyText:'No matching patients',
//            forceSelection:true,
//            queryParam:'filter',
//            minChars:4,
//            queryMode:'remote'
//        }
    ],
    initComponent:function () {
        this.callParent(arguments);
    },
    initEvents:function () {
        this.callParent(arguments);

        this.mon(this.down('segmentedbutton'), 'toggle', this.onSegmentedButtonToggle, this);
        this.mon(this.down('searchablelist'), 'select', this.onSelect, this);
    },
    onSegmentedButtonToggle:function (container, button, pressed) {
        if (!pressed) return;
        var searchablelist = this.down('searchablelist');
        var searchBy = button.getText();
        searchablelist.setEmptyText('Select a ' + searchBy);
        searchablelist.getStore().removeAll();
        searchablelist.getStore().getProxy().extraParams.id = searchBy;
        searchablelist.getStore().load();
    },
    onSelect:function (list, record) {
        var thingy = record.get('name');
        var searchByButton = this.down('#searchByButton');
        var patientSearchField = this.down('#patientSearchField');
        searchByButton.hideMenu();
        patientSearchField.emptyText = "Search " + thingy;
        patientSearchField.reset();
    }
});
