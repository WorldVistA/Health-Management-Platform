{
//    activeTabIdx: 3,
    tabs: [
        {
            xtype:'wunderpanel',
            title:'Summary',
            width: '100%',
            height: '100%',
//	        items: [{
            items:[
                {
                    xtype:'viewdefgridpanel',
                    title:"Labs",
                    titleTpl:'Recent Labs ({total})',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.LabViewDef',
                    viewParams: {'row.count': 500},
                    tools: [{xtype: 'viewdeffiltertool', paramKeys: ['range']}],
//                    tbarConfig:"EXT.DOMAIN.cpe.viewdef.ViewDefFilterTool",
                    detailType:'tip',
                    gridX:0,
                    gridY:0,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                },
                {
                    xtype:'viewdefgridpanel',
                    title:"Most Recent Vitals",
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.VitalsViewDef',
                    gridX:1,
                    gridY:0,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                },
                {
                    xtype:'viewdefgridpanel',
                    title:"Problems",
                    tbarConfig:"EXT.DOMAIN.cpe.viewdef.AutoFilterToolbar",
                    viewParams:{
                        "qfilter_status":"ACTIVE"
                    },
                    titleTpl:"Problems ({total})",
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.ProblemViewDef',
                    detailType:'tip',
                    rowBodyTpl:'<tpl for="comments"><p style="padding: 0px 0px 0px 15px;">{comment}</p></tpl>',
                    gridX:0,
                    gridY:1,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                },
//	            ]
//	        },{
//	            items: [
                {
                    xtype:'viewdefgridpanel',
                    title:"Immunizations",
                    titleTpl:'Immunizations ({total})',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.ImmunizationsViewDef',
                    bbar:null,
                    gridX:1,
                    gridY:1,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                },
                {
                    xtype:'viewdefgridpanel',
                    title:"Meds",
                    titleTpl:'Meds ({total})',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.MedsViewDef',
                    tbarConfig:"EXT.DOMAIN.cpe.viewdef.AutoFilterToolbar",
                    viewParams:{
                        "qfilter_status":"ACTIVE"
                    },
                    gridX:0,
                    gridY:2,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                },
                {
                    xtype:'viewdefgridpanel',
                    title:'Recent Activity (last 300 days)',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.RecentViewDef',
                    gridX:1,
                    gridY:2,
                    widthX:1,
                    widthY:1,
                    weightX:1,
                    weightY:1
                }
            ]
//	        }]
        },
        {
			xtype: 'patientawarepanel',
			title: 'Timeline',
			detailURL: '/vpr/view/EXT.DOMAIN.cpe.vpr.queryeng.MedsViewDef?mode=/patientDomain/medicationtimeline&pid={pid}',
			tbar: [
			   {xtype: 'button', icon: '/images/icons/arrow_refresh.png', handler: function() {this.up('panel').reload()}},
		       {xtype: 'splitbutton', text: 'Display Options', menu: {
		    	   width: 300,
		    	   items: [
			           {xtype: 'checkbox', name: 'group_meds', boxLabel: 'Group meds by drug class?'},
			           {xtype: 'checkbox', name: 'show_icons', boxLabel: 'Show icons instead of text?'}
		           ]
		       }}
		    ]
		},
	    {
            xtype:'labreviewtab',
            title:'Lab Review'
        },
        {
            xtype:'multigridpanel',
            title:'Documents',
            detail:{width:575},
            items:[
                {
                    xtype:'viewdefgridpanel',
                    flex:1,
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.StudiesViewDef',
                    addFilterTool:true,
                    title:'Studies and Surgeries',
                    detailTitleTpl:'{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.referenceDateTime)]}: {localTitle}'
                },
                {
                    xtype:'viewdefgridpanel',
                    flex:1,
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.NotesViewDef',
                    addFilterTool:true,
                    title:'Notes and Consults',
                    detailTitleTpl:'{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.DateTime)]}: {Summary}'}
            ]
        },
        {
            xtype:'medsreviewtab'
        },
        {
            xtype:'multigridpanel', title:'Observations',
            layout:{type:'hbox', align:'stretch'},
            items:[
                {
                    xtype:'viewdefgridpanel',
                    flex:1,
                    title:"Observations",
                    titleTpl:'Observations ({total})',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.ObservationsViewDef'
                },
                {
                    xtype:'viewdefgridpanel',
                    flex:1,
                    title:"Health Factors",
                    titleTpl:'Health Factors ({total})',
                    detailTitleTpl:'{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.DateTime)]}: {Summary}',
                    viewID:'EXT.DOMAIN.cpe.vpr.queryeng.FactorsViewDef'
                }
            ]
        },
        {
            xtype:'viewdefgridpanel',
            title:"Orders",
            titleTpl:'Orders ({total})',
            detailTitleTpl:'{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.start)]}: {Summary}',
            viewID:'EXT.DOMAIN.cpe.vpr.queryeng.OrdersViewDef',
            viewParams:{group:'displayGroup'},
            detailType:'right',
            tbar:'EXT.DOMAIN.cpe.viewdef.AutoFilterToolbar'
        },
        {
            xtype:'viewdefgridpanel',
            title:"Procedures",
            titleTpl:'Procedures ({total})',
            detailTitleTpl:'{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.DateTime)]}: {Summary}',
            viewID:'EXT.DOMAIN.cpe.vpr.queryeng.ProceduresViewDef',
            detailType:'right',
            // TODO: Replace this with the automatic AutoFilterToolbar
            tbar:[
                {
                    xtype:'cycle',
                    showText:true,
                    prependText:'Record Filter: ',
                    menu:{items:[
                        {text:'None', value:'', checked:true},
                        {text:'Procedures', value:'Procedure'},
                        {text:'Imaging', value:'Imaging'},
                        {text:'Consults', value:'Consult'}
                    ]},
                    changeHandler:function (btn, activeItem) {
                        var grid = this.up('viewdefgridpanel');
                        var vals = {'filter_kind':activeItem.value};
                        grid.setViewDef(grid.curViewID, Ext.apply(grid.curViewParams, vals));
                    }
                }
            ]
        },
        {
            xtype:'viewdefgridpanel',
            title:"Tasks",
            titleTpl:'Tasks ({total})',
//            detailTitleTpl: '{[EXT.DOMAIN.hmp.util.HL7DTMFormatter.format(values.start)]}: {Summary}',
            viewID:'EXT.DOMAIN.cpe.vpr.queryeng.TasksViewDef',
//            viewParams: {group: 'displayGroup'},
            detailType:'right'
//            tbar: 'EXT.DOMAIN.cpe.viewdef.AutoFilterToolbar'
        }
    ]
}
