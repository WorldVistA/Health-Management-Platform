Ext.define('EXT.DOMAIN.cpe.search.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    requires: [
        'EXT.DOMAIN.cpe.search.SearchBoxDetailWindow'
    ],
    mixins: {
        patientaware: 'EXT.DOMAIN.hmp.PatientAware'
    },
    id: 'searchBox',
    itemId: 'searchBox',
    alias: 'widget.searchbox',
    emptyText: "Search this Patient's Chart",
    disabled: true,
    autoSelect: false,
    minChars: 3,
    typeAhead: false,
    hideLabel: true,
    hideTrigger:true,
    enableKeyEvents: true,
    pickerAlign: 'tr-br',
    listConfig: {
        bodyMargin: 0,
        bodyPadding: 0,
        loadingText: 'Searching...',
        emptyText: 'No matches found.',
        // Custom rendering template for each item
        getInnerTpl: function() {
            var output = '<div class="search-result">' +
                '<div class="cpe-search-result-summary">{summary}<tpl if="count &gt; 0"><span class="cpe-search-result-count">({count} more)</span></tpl></div>' +
                '<tpl if="highlight.length != \'\'"><div class="cpe-search-result-highlight">{highlight}</div></tpl>' +
                '<div class="cpe-search-result-attributes">{datetimeFormatted} - {kind} - {where}</div>' +
                '</div>'
            return output;
        }
    },
    valueField: 'summary',
    store: Ext.create('Ext.data.Store', {
        fields: ['uid', 'summary', 'type', 'kind', 'datetime', 'datetimeFormatted', 'where', 'highlight', 'count'],
        proxy: {
            type: 'ajax',
            extraParams: {
                format: 'json'
            },
            reader: {
                type: 'json',
                root: 'data.items',
                totalProperty: 'totalItems'
            }
        }
    }),
    listeners: {
        beforepatientchange: function(pid) {
            var detailWindow = Ext.getCmp('searchBoxDetailWindow');
            if(detailWindow){
            	detailWindow.close();
            }
            var cmp = this;
            if (pid != 0) {
                cmp.clearValue();
                cmp.collapse();
            }
        },
        patientchange: function(pid) {
            var cmp = this;
            this.pid = pid;
            if (pid != 0) {
                cmp.setDisabled(false);
                this.getStore().getProxy().url = '/vpr/v1/' + pid + '/search';
            } else {
                cmp.setDisabled(true);
            }
        },
        select: function(searchbox, record, options) {
//          debugger;
          var searchText = searchbox.lastQuery;
          var type = record[0].data.type;
          var kind = record[0].data.kind;
          var textDisplay = true;
          var uid = record[0].data.uid;
          console.log(uid);
          
          if ((type == 'vital_sign') || ((type == 'result') && (kind == 'Laboratory'))) textDisplay = false;
          var detailWindow = Ext.getCmp('searchBoxDetailWindow');
          if(!detailWindow){
        	  detailWindow = Ext.create('EXT.DOMAIN.cpe.search.SearchBoxDetailWindow');
//        	  detailWindow = Ext.create('Ext.window.Window', {
//        	    id: 'searchBoxDetailWindow',
//				layout: 'fit',
//				width: searchbox.width,
//				height: 300,
//				items:[				  
//	                {
//	                    xtype: 'searchdetail',
//	                    itemId: 'searchdetail',
//						width: searchbox.width,
//	                }
//	           ],
//	           listeners: {
//	        	   close: function( panel, opts ){
//	        		   searchbox.setValue(searchbox.lastQuery);
//	        		   searchbox.expand();
//	        	   }
//	           }
//			});
          }
          
          if (!detailWindow.isVisible()) {
              detailWindow.show();
              detailWindow.alignTo(searchbox, 't-b');
              detailWindow.first = true;
          }
          var detailPanel = detailWindow.down('#searchdetail');
          detailWindow.setTitle(record[0].data.summary); 
          
		  if (textDisplay) {
	          if (uid) {
	        	  detailPanel.getLoader().load({
	              url: '/vpr/detail/' + encodeURIComponent(uid)
	            })
	         }
		 }else{
			 detailPanel.getLoader().load({
                 url: '/vpr/trend/' + encodeURIComponent(uid),
                 renderer: 'data',
                 params: {
                     format: 'json'
                 },
                 success: function(loader, response) {
                 	var jsonResult = Ext.JSON.decode(loader.responseText);
                 	if(!jsonResult) return
                 	
                 	var chartData = [];
                 	chartData[0] = jsonResult.data;
                 	chartData[0].data = chartData[0].items;
                 	chartData[0].items=null;
                 	detailPanel.updateChartData(chartData);
                 }
             });
		 }
		  
        },
// TODO: Method to manually unmask      
//        focus: function(field, event){
//        	field.el.unmask();
//        }

    },
    
    initComponent: function() {
		var me = this;

		// handlers for exceptions
	    this.store.getProxy().on('exception', function() {
	    	var a = me;
	        me.el.mask('Component Received an Error. Try Reloading.')
	        me.store.removeAll();
	    });
	
		this.store.on('beforeload', function(store, op, eopts) {
		   op.skipErrors = true;
		});
		
    	// initalize component
    	this.callParent();
    },
    onBoxReady:function() {
        this.initPatientContext();
        this.callParent(arguments);
    }
});

