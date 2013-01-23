/**
 * This is an action column that has an embedded tooltip that will fetch and interpret 
 * what it can to display infobuttons, alerts and actions.
 * 
 * Will also try to display the appropriate summary icon.
 * 
 * TODO: this needs some CSS cleanup
 */
Ext.define('org.osehra.cpe.viewdef.RowActionColumn', {
	extend: 'Ext.grid.column.Action',
	alias: 'widget.rowactioncolumn',
	sortable: false,
	requires: ['org.osehra.cpe.AlertDialog'],
	resizable: false,
	hideable: false,
	menuDisabled: true,
	width: 20,
	tdCls: 'hmp-action-btn-cell',
	
	requestAction: 'org.osehra.cpe.vpr.rowaction', // if true, will make an invoke request for frames to generate any actions
	constructor: function(config) {
		// the ActionColumn constructor rebuilds the items array, so we have to use contstructor instead of initComponent in 4.07
		Ext.apply(config, {width: this.width, header: '', hideable: this.hideable, renderer: this.renderer});
		this.callParent([config]);
	},
	
	renderer: function(val, metaData, rec) {
		var actions = rec.get('actions');
		if (actions && actions.length) {
			metaData.style += "background: url('/images/icons/warning_sign.png'); background-repeat: no-repeat; background-size: 15px 15px;";
		}
		return '&nbsp;';
	},
	
	createTip: function(target) {
		this.tooltip = Ext.create('Ext.tip.ToolTip', {
            target: target,
            delegate: '.hmp-action-btn-cell',
            itemId: 'RowActionTooltipID',
            autoHide: false,
            closeable: true,
            anchor: 'right',
            anchorToTarget: true,
            hideDelay: 1000,
            mouseOffset: [0,0],
            width: 300,
            title: 'Available Actions:'
		});
		
		this.tooltip.alertset = this.tooltip.add(Ext.widget('fieldset', {title: 'Alerts: ', collapsible: true}));
		this.tooltip.actionset = this.tooltip.add(Ext.widget('fieldset', {title: 'Actions: ', collapsible: true}));
		this.tooltip.linkset = this.tooltip.add(Ext.widget('fieldset', {title: 'Links: ', collapsible: true}));
		
		this.tooltip.on('beforeshow', this.updateTip, this);
	},
	
	updateTip: function() {
		var tip = this.tooltip;
		
        tip.actionset.setTitle('Actions (0):');
        tip.actionset.removeAll();
        tip.actionset.collapse();
        tip.actionset.disable();
        tip.actionset.hide();
        tip.alertset.setTitle('Alerts (0):');
        tip.alertset.removeAll();
        tip.alertset.collapse();
        tip.alertset.disable();
        tip.alertset.hide();
        tip.linkset.setTitle('Links (0):');
        tip.linkset.removeAll();
        tip.linkset.collapse();
        tip.linkset.disable();
        tip.linkset.hide();
		
		// get the record
		var row = Ext.get(tip.triggerElement).up(".x-grid-row");
        var rec = this.view.getRecord(row);

        // if there is an actions column (alerts) display them...
        var actions = rec.get('actions');
        if (actions) {
        	tip.alertset.show();
        	if (actions.length) {
	   		    tip.alertset.enable();
	    		tip.alertset.expand();
            }
        	tip.alertset.setTitle('Alerts (' + actions.length + '): ');
            for (var i=0; i < actions.length; i++) {
            	var action = actions[i];
            	tip.alertset.add({xtype: 'button', ui: 'link', frameID: actions[i].frameID, text: actions[i].title, description: actions[i].description, handler: function() {
            		org.osehra.cpe.AlertDialog.open(action);
            	}});
            }
        }
        
        // cancel any existing request (so 2 sets of results don't display)
        if (this.request1) {
        	Ext.Ajax.abort(this.request1);
        }
        if (this.request2) {
        	Ext.Ajax.abort(this.request2);
        }
        
        // fetch and render object-specific actions
        if (this.requestAction) {
        	this.requestActions(rec, tip);
        }

        // if no infobutton request is present, then return
        var urlStr = (rec) ? rec.get('infobtnurl') : null;
        if (urlStr) {
        	this.requestInfobuttons(urlStr, tip);
        }
	},
	
	requestInfobuttons: function(urlStr, tip) {
        if (this.request2) {
        	Ext.Ajax.abort(this.request2);
        }
        tip.linkset.add({xtype: 'displayfield', value: 'Loading...'});
        tip.linkset.expand();
        tip.linkset.show();
        
		// ajax request to load the infobutton results.
        this.request2 = Ext.Ajax.request({
         url: urlStr + "&transform",
         success: function(response) {
        	tip.linkset.removeAll();
        	
         	// TODO: this is a hack, needs to be reworked.
         	var feeds = response.responseXML.getElementsByTagNameNS('*','feed');
         	var count = 0;
         	for (var i=0; i < feeds.length; i++) {
         		var title = feeds[i].getElementsByTagNameNS('*','title')[0].lastChild.nodeValue;
         		var subtitle = feeds[i].getElementsByTagNameNS('*','subtitle')[0].lastChild.nodeValue;
         		var entries = feeds[i].getElementsByTagNameNS('*','entry');
         		for (var j=0; j < entries.length; j++) {
         			var etitle = entries[j].getElementsByTagNameNS('*','title')[0].lastChild.nodeValue;
         			var elink = entries[j].getElementsByTagNameNS('*','link')[0].getAttribute('href');
         			var hovertitle = 'Open in new window: ' + title + ':  ' + subtitle + ' (' + etitle + ')';
         			var linkhtml = '<a class="external-link" title="' + hovertitle + '" style="padding-right: 13px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=) center right no-repeat;" href="' + elink + '" target="_BLANK">' + etitle + '</a>';
         			tip.linkset.add({xtype: 'displayfield', fieldLabel: title, value: linkhtml});
         			count++;
         		}
         	}
         	
         	// enable the link set if anything is there
         	if (count) {
         		tip.linkset.enable();
         		tip.linkset.expand();
         		tip.linkset.setTitle('Links (' + count + '): ');
         	}
         },
         failure: function(response) {
        	 tip.linkset.add({xtype: 'displayfield', value: 'Error loading infobutton response...'});
         }
       });
	},
	
	requestActions: function(rec, tip) {
        if (this.request1) {
        	Ext.Ajax.abort(this.request1);
        }
        tip.actionset.show();
        tip.actionset.expand();
        tip.actionset.add({xtype: 'displayfield', value: 'Loading...'});
        
        // fetch and render object-specific actions
        // TODO: Send more than just the row, send the viewdef/params context as well.
        var uid = rec.get('uid');
        var url = "/frame/invoke?entryPoint=" + this.requestAction;
        if (uid) url += '&uid=' + uid;
        this.request1 = Ext.Ajax.request({
        	 url: url,
        	 jsonData: rec.getData(),
             success: function(response) {
            	 var data = Ext.JSON.decode(response.responseText);
            	 if (data.actions) {
            		 tip.actionset.removeAll();
            		 tip.actionset.enable();
            		 if (!data.actions.length) {
            			 tip.actionset.collapse();
            		 }
            		 tip.actionset.setTitle('Actions (' + data.actions.length + '): ');
            		 for (var i in data.actions) {
            			 var action = data.actions[i];
            			 var value = action.title;
            			 var cfg = {xtype: 'displayfield', anchor: '100%', value: action.title};
            			 if (action.url) {
            				 cfg.fieldLabel = action.heading;
            				 cfg.value = '<a class="external-link" title="' + action.hint + '" style="padding-right: 13px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=) center right no-repeat;" target="_BLANK" href="' + action.url + '">' + action.title + '</a>';
            			 } else if (action.orderDialogID) {
            				 cfg.xtype = 'button';
            				 cfg.textAlign = 'left';
            				 cfg.ui = 'link';
            				 cfg.icon = '/images/icons/action.png',
            				 cfg.text = action.orderMessage;
            				 cfg.handler = function() {
            					 alert('TODO: Launch order dialog: ' + action.orderDialogID + '. Data: ' + action.orderData);
            				 }
            			 }
            			 tip.actionset.add(cfg);
            		 }
            	 }
             },
             failure: function(response) {
            	 tip.actionset.add({xtype: 'displayfield', value: 'Error loading actions...'});
             }
        });
	},
	
	listeners: {
    	render: function(cmp) {
    		var me = this;
			this.view = this.up('gridpanel').getView();
			this.createTip(this.view.el);
		}
	}
});
