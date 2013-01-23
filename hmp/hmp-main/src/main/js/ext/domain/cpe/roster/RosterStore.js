/**
 * Store containing all rosters.
 */
Ext.define('org.osehra.cpe.roster.RosterStore', {
    extend: 'Ext.data.Store',
    requires: [
        'org.osehra.cpe.roster.RosterModel'
    ],
    storeId: 'rosters',
    model: 'org.osehra.cpe.roster.RosterModel',
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
