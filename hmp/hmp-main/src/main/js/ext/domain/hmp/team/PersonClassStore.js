Ext.define('org.osehra.hmp.team.PersonClassStore', {
    extend: 'Ext.data.Store',
    storeId: 'personClasses',
    model: 'org.osehra.hmp.team.PersonClass',
    groupField: 'providerType',
    proxy: {
        type: 'ajax',
        url: '/js/org.osehra/hmp/team/person-classes.json',
        reader: {
            type: 'json',
            root: 'data.items',
            totalProperty: 'data.totalItems'
        }
    }
});
