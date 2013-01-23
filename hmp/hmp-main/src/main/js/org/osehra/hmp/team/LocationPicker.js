Ext.define('org.osehra.hmp.team.LocationPicker', {
    extend:'Ext.form.field.ComboBox',
//    requires:[
//        'org.osehra.hmp.team.PersonStore'
//    ],
    alias:'widget.locationpicker',
    displayField: 'name',
    valueField: 'uid',
    initComponent:function () {
//        var personStore = Ext.getStore('persons');
//        if (!personStore) {
//            personStore = Ext.create('org.osehra.hmp.team.PersonStore');
//        }
//        this.store = personStore;

        this.callParent(arguments);
    }
});
