Ext.define('EXT.DOMAIN.hmp.admin.SyncErrorStore', {
    extend:'Ext.data.Store',
    storeId:'syncErrors',
    fields:['id', 'patient', 'pids', 'dateCreated', 'item', 'message', 'stackTrace', 'json'],
    pageSize: 20,
    proxy : {
        type: 'ajax',
        url : '/sync/syncErrors',
        extraParams: {
            format: 'json'
        },
        reader: {
            type: 'json',
            root: 'data.items',
            totalProperty: 'data.totalItems'
        }
    },
    autoLoad: true
});
