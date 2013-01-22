Ext.define('EXT.DOMAIN.hmp.team.TeamStore', {
    extend: 'Ext.data.Store',
    requires: [
        'EXT.DOMAIN.hmp.team.Team'
    ],
    storeId: 'teams',
    model: 'EXT.DOMAIN.hmp.team.Team',
    proxy: {
        type: 'ajax',
//        url: '/js/EXT/DOMAIN/hmp/team/teams.json',
        url: '/teamMgmt/v1/team/list',
        reader: {
            type: 'json',
            root: 'data.items'
        }
    }
});
