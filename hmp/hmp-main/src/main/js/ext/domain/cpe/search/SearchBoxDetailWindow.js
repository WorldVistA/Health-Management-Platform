Ext.define('org.osehra.cpe.search.SearchBoxDetailWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.searchBoxDetailWindow',
    id: 'searchBoxDetailWindow',
	layout: 'fit',
	width: Ext.getCmp('searchBox')?Ext.getCmp('searchBox').width:400,
	height: 300,
	items:[				  
        {
            xtype: 'searchdetail',
            itemId: 'searchdetail',
            width:  Ext.getCmp('searchBox')?Ext.getCmp('searchBox').width:400,
			//width: searchbox.width,
        }
   ],
   listeners: {
	   close: function( panel, opts ){
		   var searchbox = Ext.getCmp('searchBox');
		   if(searchbox){
			   searchbox.setValue(searchbox.lastQuery);
			   searchbox.expand();
		   }
	   }
   }
});
