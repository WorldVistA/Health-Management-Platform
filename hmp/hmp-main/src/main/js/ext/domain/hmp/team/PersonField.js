Ext.define('org.osehra.hmp.team.PersonField', {
    extend:'Ext.form.field.ComboBox',
    requires:[
        'org.osehra.hmp.team.PersonStore'
    ],
    alias:'widget.personfield',
    queryMode: 'local',
    displayField: 'name',
    valueField: 'uid',
    initComponent:function () {
        this.store = Ext.data.StoreManager.containsKey('persons') ? Ext.getStore('persons') : Ext.create('org.osehra.hmp.team.PersonStore');

        this.callParent(arguments);
    },
    onBoxReady:function() {
        this.callParent(arguments);

        this.getStore().load();
    }
});
