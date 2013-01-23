Ext.define('org.osehra.hmp.admin.FrameListPanel', {
    extend:'org.osehra.cpe.viewdef.ViewDefGridPanel',
    requires:[],
    itemId:'frame-list',
    title:'Frames',
    titleTpl: 'Frames ({total})',    
    viewParams: {
        'group': 'type',
        'col.display': 'id,name,runCount,type'
    },
    viewID:'org.osehra.cpe.vpr.queryeng.FrameListViewDef',
    detailType: 'bottom',
    detail: {
    	height: '300px'
    },
    scroll: 'vertical',
    patientAware:false,
    viewConfig:{
        emptyText:'Loading Frames...',
        deferEmptyText:false
    },
    autorefresh:function () {
        this.getStore().load();
    }
});
