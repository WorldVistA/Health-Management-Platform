Ext.define('org.osehra.hmp.team.TeamPositionField', {
    extend:'Ext.form.field.ComboBox',
    requires:[
        'org.osehra.hmp.team.TeamPositionStore'
    ],
    alias:'widget.teampositionfield',
    emptyText: 'Select Position',
    displayField: 'name',
    valueField: 'uid',
//    typeAhead: true,
    editable: true,
    queryMode: 'local',
    triggerAction: 'all',
    selectOnTab: true,
    lazyRender:true,
    initComponent:function () {
        this.store = Ext.data.StoreManager.containsKey('teamPositions') ? Ext.getStore('teamPositions') : Ext.create('org.osehra.hmp.team.TeamPositionStore');

        this.callParent(arguments);
    }
});
