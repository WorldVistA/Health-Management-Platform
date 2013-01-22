{
    tabs: [
        {
            xtype:'dataview',
            title:'Activity',
            autoScroll:true,
            scroll:'vertical',
//            margin: '12',
            padding:'12',
//            style: 'border-left:1px solid whiteSmoke;',
            store:Ext.create('Ext.data.Store', {
                fields:['uid',
                    'summary',
                    'name',
                    'providerName',
                    {
                        name:'entered',
                        convert:function (value, record) {
                            return CPE.view.util.HL7DTMFormatter.format(value);
                        }
                    },
                    'facilityName',
                    'locationName'
                ],
                proxy:{
                    type:'ajax',
                    url:'/js/config/activities.json',
                    reader:{
                        type:'json',
                        root:'data.items'
                    }
                },
                autoLoad:true
            }),
            itemSelector:'tr.activity-wrap',
            tpl:'<tpl for=".">' +
                '<table style="width:100%;margin-bottom: 12px;border-bottom: 1px solid #dddddd">' +
                '<tr class="activity-wrap">' +
                    '<td>' +
                        '<span class="hmp-label" style="text-align: right;vertical-align: top">{entered}</span>' +
                    '</td>' +
                    '<td>' +
                        '<div>{providerName} ordered a <span>{name}</span></div>' +
                        '<div>{summary}</div>' +
                        '<div class="hmp-label">{facilityName}&nbsp;-&nbsp;{locationName}</div>' +
                    '</td>' +
                '</tr>' +
                '</table>' +
                '</tpl>'
        }
    ]
}