Ext.define('EXT.DOMAIN.hmp.team.TeamPositionStore', {
    extend: 'Ext.data.Store',
    requires: [
        'EXT.DOMAIN.hmp.team.TeamPosition'
    ],
    storeId: 'teamPositions',
    model:'EXT.DOMAIN.hmp.team.TeamPosition',
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
