Ext.define('EXT.DOMAIN.hmp.team.PersonField', {
    extend:'Ext.form.field.ComboBox',
    requires:[
        'EXT.DOMAIN.hmp.team.PersonStore'
    ],
    alias:'widget.personfield',
    queryMode: 'local',
    displayField: 'name',
    valueField: 'uid',
    initComponent:function () {
        this.store = Ext.data.StoreManager.containsKey('persons') ? Ext.getStore('persons') : Ext.create('EXT.DOMAIN.hmp.team.PersonStore');

        this.callParent(arguments);
    },
    onBoxReady:function() {
        this.callParent(arguments);

        this.getStore().load();
    }
});
