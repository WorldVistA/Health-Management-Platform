Ext.define('EXT.DOMAIN.cpe.roster.RosterModel', {
    extend: 'Ext.data.Model',
    idProperty:'id',
    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'favorite'},
        {name: 'panel'},
        {name: 'viewdef', defaultValue: 'EXT.DOMAIN.cpe.vpr.queryeng.RosterViewDef'},
        {name: 'sources'}
    ]
});
