Ext.define('EXT.DOMAIN.hmp.team.PersonStore', {
    extend: 'Ext.data.Store',
    requires:[
        'EXT.DOMAIN.hmp.team.Person'
    ],
    storeId: 'persons',
    model: 'EXT.DOMAIN.hmp.team.Person',
    proxy: {
        type: 'ajax',
        url: '/teamMgmt/v1/person/list',
        reader: {
            type: 'json',
            root: 'data.items',
            totalProperty: 'data.totalItems'
        }
    }
});
