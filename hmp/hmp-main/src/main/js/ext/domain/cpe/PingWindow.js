Ext.define('org.osehra.cpe.PingWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.pingwindow',
	title: 'PING',
	layout: {
		type: 'border',
		align: 'stretch'
	},
	items: [{
		xtype: 'panel',
		region: 'center',
		id: 'pingOutputTestPanel',
		html: ''
	},{
		xtype: 'button',
		region: 'south',
		text: 'Click Me',
		handler: function(bn) {

			Ext.Msg.prompt('Message', 'What\'s to be said?', function(btn, text){
			    if (btn == 'ok'){
			    	this.sendMessage(text);
			    }
			}, bn.up('pingwindow'));
//			bn.up('pingwindow').sendMessage();
		}
	},{
		xtype: 'button',
		region: 'north',
		text: 'Connect',
		handler: function(bn) {
			bn.up('pingwindow').initEventConnection();
		}
	}],
	initEventConnection: function() {
		Ext.Ajax.request({
			url: '/event/comet2',
			method: 'GET',
			success: function(response, opts) {
				// TODO: Process stuff.
				this.down('panel').update("LocalMessage: "+response.responseText);
				this.initEventConnection();
			},
			failure: function(response, opts) {
				// TODO: Process stuff.
				this.down('panel').update("LocalMessage: "+response.responseText);
				this.initEventConnection();
			},
			timeout: 0,
			scope: this
		});
	},
	sendMessage: function(text) {
		Ext.Ajax.request({
			url: '/event/comet2',
			method: 'POST',
			jsonData: {
				"message": text
			}
		});
	}
})
