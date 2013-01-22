/**
 * RosterPicker is a combobox that lets selects from all rosters.
 */
Ext.define('EXT.DOMAIN.cpe.roster.RosterPicker', {
    extend:'Ext.form.field.ComboBox',
    requires:[
        'EXT.DOMAIN.cpe.roster.RosterStore'
    ],
    alias:'widget.rosterpicker',
    queryMode:'local',
    queryParam:'filter',
    grow:true,
    typeAhead:true,
    displayField:'name',
    valueField:'id',
    initComponent:function () {
        // create the roster store (can't put this inline because it gets called at load time, before the requires clauses have all be called)
        this.store = Ext.getStore('rosters') ? Ext.getStore('rosters') : Ext.create('EXT.DOMAIN.cpe.roster.RosterStore');

        this.callParent(arguments);
    }
});
