/**
 * Store containing all rosters.
 */
Ext.define('EXT.DOMAIN.cpe.roster.RosterStore', {
    extend: 'Ext.data.Store',
    requires: [
        'EXT.DOMAIN.cpe.roster.RosterModel'
    ],
    storeId: 'rosters',
    model: 'EXT.DOMAIN.cpe.roster.RosterModel',
    proxy: {
        type: 'ajax',
        url: '/roster/list',
        extraParams: {
            id:'all'
        },
        reader: {
            root: 'data',
            type: 'json'
        }
    },
    autoLoad: true
});
