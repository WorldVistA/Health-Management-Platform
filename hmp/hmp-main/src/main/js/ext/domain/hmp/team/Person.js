Ext.define('EXT.DOMAIN.hmp.team.Person', {
    extend:'Ext.data.Model',
    idProperty:'uid',
    fields:[
        {
            name:'uid',
            type:'string'
        },
        {
            name:'name',
            type:'string'
        },
        {
            name:'photoHref',
            type:'string',
            convert:function (value, record) {
                var uid = record.data.uid;
                if (!uid)
                    return null;
                else
                    return '/person/v1/' + uid + '/photo';
            }
        }
    ]
});
