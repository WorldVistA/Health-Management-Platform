Ext.define('EXT.DOMAIN.hmp.team.LocationPicker', {
    extend:'Ext.form.field.ComboBox',
//    requires:[
//        'EXT.DOMAIN.hmp.team.PersonStore'
//    ],
    alias:'widget.locationpicker',
    displayField: 'name',
    valueField: 'uid',
    initComponent:function () {
//        var personStore = Ext.getStore('persons');
//        if (!personStore) {
//            personStore = Ext.create('EXT.DOMAIN.hmp.team.PersonStore');
//        }
//        this.store = personStore;

        this.callParent(arguments);
    }
});
