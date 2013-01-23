Ext.define('org.osehra.hmp.admin.VprPatientBrowser', {
    extend:'org.osehra.cpe.viewdef.ViewDefGridPanel',
    alias:'widget.vprpatientbrowser',
    itemId:'vpr-patients',
    title:'Patients',
    viewID:'org.osehra.cpe.vpr.queryeng.VprPatientsViewDef',
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
