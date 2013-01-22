Ext.define('EXT.DOMAIN.cpe.roster.FavoriteRosterPicker', {
    extend:'Ext.form.field.ComboBox',
    requires:[
        'EXT.DOMAIN.cpe.roster.RosterContext',
        'EXT.DOMAIN.cpe.roster.RosterModel'
    ],
    alias:'widget.favrosterpicker',
    //Commented out because Rosters are not populated
    queryMode:'local',
    queryParam:'filter',
//	queryCaching: false,
    grow:true,
    emptyText:'<Select Patient List>',
    typeAhead:true,
    allowBlank:false,
    forceSelection:true,
    displayField:'name',
    valueField:'id',
    initComponent:function() {
        this.store = Ext.create('Ext.data.Store', {
            storeId:'favoriteRosters',
            model: 'EXT.DOMAIN.cpe.roster.RosterModel',
            proxy:{
                type:'ajax',
                url:'/roster/list',
                extraParams: {
                    id: 'fav'
                },
                reader:{
                    type:'json',
                    root:'data'
                }
            },
            autoLoad: true
        });
        this.callParent(arguments);
        this.relayEvents(this.getStore(), ['load']);
    },
    /**
     * JC 5-31-2012: Maybe this method should be higher up, but I didn't know a more reliable way to work with the existing roster selection mechanisms which were already down inside this class.
     * Let's leave a TODO: Refactor this code out of this combo box to a more general area.
     * @private
     */
    initTitleToSelectedRoster:function () {
        var pp = this.up('patientpicker');
        if (pp && pp.rendered && pp.rosterID) {
                this.store.data.each(function (rec, idx, len) {
                    if (rec.internalId == pp.rosterID) {
                        this.doRosterSelection(this, rec);
                        this.select(rec);
                    }
                }, this);
        }
    },
    doRosterSelection:function (combo, record) {
//        Ext.log(Ext.getClassName(this) + ".doRosterSelection()");
        Ext.suspendLayouts();

        EXT.DOMAIN.cpe.roster.RosterContext.setRosterInfo(record.data);
        var title = record.get('name');
        var ptpicker = combo.up('patientpicker');
        ptpicker.setTitle(title);

        //parent.down('textfield').reset();
        ptpicker.rosterID = record.get('id');
        ptpicker.rosterViewDef = record.get('viewdef');
        ptpicker.rosterPanel = record.get('panel');

//        Ext.log(Ext.getClassName(this) + ".rosterID=" + ptpicker.rosterID);
        if (ptpicker.rosterID > 0 && ptpicker.rosterViewDef) {
//            Ext.log("/roster/select?rosterID=" + ptpicker.rosterID);
            Ext.Ajax.request({
                url:'/roster/select?rosterID=' + ptpicker.rosterID
            });
        }
        ptpicker.patientgrid.setViewDef(ptpicker.rosterViewDef, {'roster.ien':ptpicker.rosterID}, true);

        var cpe = Ext.ComponentQuery.query('cpepanel');
        if (cpe && cpe.length > 0) {
            cpe[0].refreshPickerHotspots();
        }

        Ext.resumeLayouts();
        
        // Hack to synchronize all pickers
        var pickers = Ext.ComponentQuery.query('favrosterpicker');
        for(key in pickers) {
        	if(pickers[key]!=this && pickers[key].getValue()!=record.get('id')) {
        		pickers[key].setValue(record.get('id'));
        	}
        }
    }
});
