{
	tabs: 	[
	    { // This is the mockup of the worksheet layout/view
	    	xtype: 'panel', 
	    	title: 'CHF Worksheet',
	    	layout: {
	    		type: 'hbox',
	    		align: 'stretch',
                padding: '0 6 0 0'
	    	},
	    	items: [
    	        {
    	        	flex: 1,
    	        	layout: {
    	        		type: 'vbox',
    	        		align: 'stretch',
                        padding: '0 0 6 0'
    	        	},
    	        	items: [
    	                {xtype: 'viewdefgridpanel', title: "CHF Meds", titleTpl: 'CHF Meds ({total})', flex: 2, viewID: 'EXT.DOMAIN.cpe.vpr.queryeng.MedsViewDef', height: 250, hideHeaders: true, viewParams: {'col.display': 'summary,', 'filter_class': "ACE INHIBITORS,LOOP DIURETICS,BETA BLOCKERS"}},
	        	        {xtype: 'viewdefgridpanel', title: "CHF Labs", titleTpl: 'CHF Labs ({total})', flex: 2, viewID: 'EXT.DOMAIN.cpe.vpr.queryeng.LabViewDef', height: 250, viewParams: {'col.display': 'name,value,specimen', 'filter.typeCodes': "urn:lnc:2951-2,urn:lnc:2823-3,urn:lnc:2160-0"}},
	                    {xtype: 'panel', title: 'Tasks', flex: .5, html: 'TBD'}
        	        ]
    	        },
    	        {html: "CHF WORKSHEET", align: 'center', flex: 2}
	        ]
		},{
			xtype: 'viewdefgridpanel',
			title: 'Relevant Labs',
			viewID: 'EXT.DOMAIN.cpe.vpr.queryeng.LabViewDef',
			viewParams: {
				'filter.typeCodes': "urn:lnc:2951-2,urn:lnc:2823-3,urn:lnc:2160-0"
			}
		},
		{
            xtype: 'viewdefgridpanel',
            title: 'LVEF Documents',
            titleTpl: 'LVEF Documents ({total})',
            detailType: 'right',
            viewID: 'EXT.DOMAIN.cpe.vpr.queryeng.DocSearchResultsViewDef'
		},{
			xtype: 'viewdefgridpanel',
			title: 'CHF Meds',
			titleTpl: 'CHF Meds ({total})',
			viewID: 'EXT.DOMAIN.cpe.vpr.queryeng.MedsViewDef',
			viewParams: {
				'filter_class': "ACE INHIBITORS,LOOP DIURETICS,BETA BLOCKERS"				
			}
		}
	]
}
