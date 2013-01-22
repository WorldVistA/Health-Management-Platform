Ext.define('EXT.DOMAIN.cpe.roster.PatientChart', {
	extend: 'Ext.container.Container',
//  	hidden: true,
  	id: 'PatientChart',
  	alias: 'widget.patientchart',
  	layout: {
  		type: 'card'
  	},
  	items: [
  	      {
          xtype: 'container',
          layout: {
              type:'vbox',
              align: 'center'
          },
          componentCls: 'hmp-pt-view-ct',
          items: [
              {
                  xtype: 'component',
                  html: '<h1>No Patient Selected</h1>'
              }
          ]
      },
      {
          xtype: 'panel',
          itemId: 'PatientViewID',
          layout: 'border',
          componentCls: 'hmp-pt-view-ct',
          items:[
              {
                  xtype:'ptbanner',
                  region: 'north'
              },
              {
                  xtype:'toolbar',
                  region: 'north',
                  items:[
                      {
                          xtype:'pagepicker'
                      },
                      '->',
                      {
                          xtype:'searchbox',
                          margin:'2 3 0 0',
                          width:400
                      }
                  ]
              },
              // start with an empty panel (roster/page selection will update this)
              {
                  xtype:'panel',
                  region: 'center',
                  itemId:'PatientPanelID'
              }
          ]
      }
  ]
});
