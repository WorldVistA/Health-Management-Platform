Ext.define('org.osehra.cpe.LoggedInUserModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'displayName', type:'string'},
        {name:'uid', type:'string'}
    ]
});

Ext.define('org.osehra.cpe.LoggedInUserStore', {
    extend: 'Ext.data.Store',
    requires: [
        'org.osehra.cpe.LoggedInUserModel'
    ],
    model: 'org.osehra.cpe.LoggedInUserModel',
    proxy: {
        type: 'ajax',
        url: '/chat/users',
        reader: {
            root: 'data',
            type: 'json'
        }
    },
    listeners: {
    	load: function(store, recs, success, opts) {
    		Ext.log('Records length: '+recs.length);
    	}
    }
});

Ext.define('org.osehra.cpe.ChatWindow', {
	extend: 'Ext.window.Window',
    title:'Chat Window',
    height:400,
    alias: 'widget.chatwindow',
    id:'chatWindow',
    width:400,
    layout:{
        type:'fit'
    },
	items: [{
			xtype: 'form',
            itemId:'taskPanel',
            height:500,
            width:300,
            layout:{
                type:'vbox',
                align:'stretch'
            },
            border:false,
            bodyPadding:10,
            fieldDefaults:{
                labelAlign:'top',
                labelWidth:100,
                labelStyle:'font-weight:bold'
            },
            defaults:{
                margins:'0 0 10 0'
            },
            closeAction: 'dispose',
            modal: true,
			items: [
				{
					  padding:'5 5 5 5',
					  xtype:'combobox',
					  name: 'uid',
					  itemId:'chatUserPicker',
					  grow:true,
					  fieldLabel:'Select User',
					  emptyText:'<Select User>',
					  typeAhead:true,
					  allowBlank:false,
					  forceSelection:true,
					  displayField:'displayName',
					  valueField:'uid',
					  store: Ext.create('org.osehra.cpe.LoggedInUserStore')
				},
				{
					padding: '5 5 5 5',
					xtype: 'textfield',
					name: 'message',
					itemId:'chatMessageEntry',
					fieldLabel:'Message',
					emptyText:'<Enter Message>',
					allowBlank:false,
                    listeners:{
                        specialkey:function (field, e) {
                            if (e.getKey() == e.ENTER) {
                                field.up('window').submitMsg();
                            }
                        }
                    }
				},
				{
					xtype: 'panel',
					flex: 1,
					padding: '5 5 5 5',
					autoScroll: true						
				}
			]
		}
	],
    onBoxReady:function() {
        this.callParent(arguments);
        this.down('combobox').getStore().load();
    },
	submitMsg: function() {
		var parms = this.down('form').getForm().getValues();
		if(parms.uid!=null && parms.message!=null) {
			Ext.Ajax.request({
				url: '/chat/sendMessage',
				method: 'POST',
				params: parms,
				success: function(response) {
					//Ext.log(response);
					var msg = Ext.decode(response.responseText).message;
					var frm = this.down('form');
					var itms = frm.items;
					var pnl = frm.down('panel');
					pnl.insert(0, {xtype: 'panel', html: '<p style="color:#008888"> SENT: '+msg.message+'</p>'});
				},
				failure: function(response) {
					Ext.log(response);
				},
				scope: this
			})
		}
	},
	receiveMsg: function(uid, userName, msg) {
		var frm = this.down('form');
		var itms = frm.items;
		var pnl = frm.down('panel');
		pnl.insert(0, {xtype: 'panel', html: '<p style="color:#888800">'+userName+': '+msg+'</p>'});
	}
});
