Ext.define('org.osehra.hmp.team.TeamStore', {
    extend: 'Ext.data.Store',
    requires: [
        'org.osehra.hmp.team.Team'
    ],
    storeId: 'teams',
    model: 'org.osehra.hmp.team.Team',
    proxy: {
        type: 'ajax',
//        url: '/js/org.osehra/hmp/team/teams.json',
        url: '/teamMgmt/v1/team/list',
        reader: {
            type: 'json',
            root: 'data.items'
        }
    }
});
