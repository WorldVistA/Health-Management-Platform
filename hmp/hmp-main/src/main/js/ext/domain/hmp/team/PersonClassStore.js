Ext.define('EXT.DOMAIN.hmp.team.PersonClassStore', {
    extend: 'Ext.data.Store',
    storeId: 'personClasses',
    model: 'EXT.DOMAIN.hmp.team.PersonClass',
    groupField: 'providerType',
    proxy: {
        type: 'ajax',
        url: '/js/EXT/DOMAIN/hmp/team/person-classes.json',
        reader: {
            type: 'json',
            root: 'data.items',
            totalProperty: 'data.totalItems'
        }
    }
});
