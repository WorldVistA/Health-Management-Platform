Ext.define('EXT.DOMAIN.hmp.team.Team', {
    extend:'Ext.data.Model',
    requires: [
        'EXT.DOMAIN.hmp.team.TeamAssignment'
    ],
    idProperty: 'uid',
    fields:[
        {
            name:'uid',
            type:'string'
        },
        {
            name:'displayName',
            type:'string',
            convert:function (value, record) {
                return value.replace(/ /g, '\xA0');
            }
        },
        {
            name:'ownerUid',
            type:'string'
//            convert:function (value, record) {
//                return value.replace(/ /g, '\xA0');
//            }
        },
        {
            name:'ownerName',
            type:'string'
//            convert:function (value, record) {
//                return value.replace(/ /g, '\xA0');
//            }
        },
        {
            name:'rosterId',
            type:'number'
        }
    ],
    hasMany: {
        associationKey: 'staff',
        model: 'EXT.DOMAIN.hmp.team.TeamAssignment',
        name: 'staff'
    }
});
