Ext.define('EXT.DOMAIN.hmp.admin.VprPatientBrowser', {
    extend:'EXT.DOMAIN.cpe.viewdef.ViewDefGridPanel',
    alias:'widget.vprpatientbrowser',
    itemId:'vpr-patients',
    title:'Patients',
    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.VprPatientsViewDef',
    patientAware:false,
    viewConfig:{
        emptyText:'No patients in the VPR',
        deferEmptText:false
    },
    autorefresh:function () {
        if (this.rendered) {
            this.getStore().load();
        }
    }
//    dockedItems:[
//        {
//            xtype:'pagingtoolbar',
//            dock:'bottom',
//            store:'syncErrors',
//            displayInfo:true
//        }
//    ]
});
