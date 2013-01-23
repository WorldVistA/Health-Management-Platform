Ext.define('org.osehra.cpe.roster.RosterModel', {
    extend: 'Ext.data.Model',
    idProperty:'id',
    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'favorite'},
        {name: 'panel'},
        {name: 'viewdef', defaultValue: 'org.osehra.cpe.vpr.queryeng.RosterViewDef'},
        {name: 'sources'}
    ]
});
