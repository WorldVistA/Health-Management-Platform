Ext.define('org.osehra.hmp.team.TeamPositionStore', {
    extend: 'Ext.data.Store',
    requires: [
        'org.osehra.hmp.team.TeamPosition'
    ],
    storeId: 'teamPositions',
    model:'org.osehra.hmp.team.TeamPosition',
    proxy: {
        type: 'ajax',
        url: '/teamMgmt/v1/position/list',
        reader: {
            type: 'json',
            root: 'data.items',
            totalProperty: 'data.totalItems'
        }
    },
    autoLoad: true
});
