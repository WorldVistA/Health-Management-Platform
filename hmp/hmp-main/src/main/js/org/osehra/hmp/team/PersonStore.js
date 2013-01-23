Ext.define('org.osehra.hmp.team.PersonStore', {
    extend: 'Ext.data.Store',
    requires:[
        'org.osehra.hmp.team.Person'
    ],
    storeId: 'persons',
    model: 'org.osehra.hmp.team.Person',
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
