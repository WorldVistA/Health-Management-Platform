Ext.define('EXT.DOMAIN.cpe.EventListener',{
	requires: 'EXT.DOMAIN.cpe.ChatWindow',
	statics: {
		listen: function() {
			Ext.Ajax.request({
				url: '/event/comet2',
				method: 'GET',
				success: function(response, opts) {
					// TODO: Process stuff.
					//this.down('panel').update("LocalMessage: "+response.responseText);
					EXT.DOMAIN.cpe.EventListener.handleMessage(response.responseText);
					EXT.DOMAIN.cpe.EventListener.listen();
				},
				failure: function(response, opts) {
					// TODO: Process stuff.
					//this.down('panel').update("LocalMessage: "+response.responseText);
					//EXT.DOMAIN.cpe.EventListener.listen();
					Ext.log('Event listener stopped: '+response.responseText);
				},
				timeout: 10000000,
				scope: this
			});
		},
		handleMessage: function(msg) {
//			console.log(msg);
			var mob = Ext.decode(msg, true);
			if(mob) {
				if(mob['viewdef.id']) {
					EXT.DOMAIN.cpe.EventListener.handleViewDefUpdate(mob);
				} else if(mob['chatMessage']) {
					EXT.DOMAIN.cpe.EventListener.handleChatMessage(mob.chatMessage);
				} else if(mob['syncComplete']) {
					EXT.DOMAIN.cpe.EventListener.handleSyncCompleteMessage(mob.syncComplete);
				} else if(mob['syncStatus']) {
					EXT.DOMAIN.cpe.EventListener.handleSyncStatusMessage(mob.syncStatus);
				} 
			}
		},
		handleViewDefUpdate: function(mob) {
			var viewId = mob['viewdef.id'];
			var uid = mob['uid'];
			var changes = mob['changes'];
//			console.log(viewId+"."+uid);
			var defs = Ext.ComponentQuery.query('[viewID=\''+viewId+'\']');
			for(var x in defs) {
				var def = defs[x];
				var rec = def.getStore().findRecord('uid',uid);
				if(rec) {
					for(var y in changes) {
						var change = changes[y];
						var fld = change['FIELD'];
						var oldVal = change['OLD_VALUE'];
						var newVal = change['NEW_VALUE'];
						rec.set(fld, newVal);
					}
				}
			}
		},
		handleChatMessage: function(mob) {
			var targetUid = 'chatwindow-'+mob.from.uid;
			var wnds = Ext.ComponentQuery.query('chatwindow');
			var wnd = null;
			for(key in wnds) {
				if(wnds[key].id==targetUid || wnds[key].down('combobox').getValue()==mob.from.uid) {
					wnd = wnds[key];
				}
			}
			if(wnd == null) {
				wnd = Ext.create('EXT.DOMAIN.cpe.ChatWindow', {id: targetUid});
				wnd.show();
				wnd.down('combobox').setValue(mob.from.uid);
			}
			wnd.receiveMsg(mob.from.uid, mob.from.displayName, mob.message);
		},
		handleSyncStatusMessage: function(mobAry) {
			for(mkey in mobAry) {
				var mob = mobAry[mkey];
				var pid = mob.pid;
				var dfn = mob.dfn;
				var qty = mob.qty;
				var total = mob.total;
				var pickerz = Ext.ComponentQuery.query('patientpicker');
				for(key in pickerz) {
					var picker = pickerz[key];
					var vdgp = picker.down('viewdefgridpanel');
					var str = vdgp.getStore();
					var mdl = str.findRecord('pid',pid);
					if(!mdl) {mdl = str.findRecord('dfn',dfn);}
					if(mdl) {
						if(pid && !mdl.get('pid')) {mdl.set('pid',pid);}
						var nm = mdl.get('name');
						if(mdl.oldName) {
							nm = mdl.oldName;
						} else {
							mdl.oldName = nm;
						}
						if(qty<total) {
							mdl.set('name', nm + ' <font color=RED><i>('+qty+'/'+total+')</i></font>');
						} else {
							mdl.set('name', nm + ' <font color=GREEN><i>(Sync Complete)</i></font>');
						}
					}
				}
			}
		},
		handleSyncCompleteMessage: function(mob) {
			var pid = mob.pid;
			var pickerz = Ext.ComponentQuery.query('patientpicker');
			for(key in pickerz) {
				var picker = pickerz[key];
				var vdgp = picker.down('viewdefgridpanel');
				var str = vdgp.getStore();
				var mdl = str.findRecord('pid',pid);
				if(mdl) {
					var nm = mdl.get('name');
					if(mdl.oldName) {
						nm = mdl.oldName;
					} else {
						mdl.oldName = nm;
					}
					mdl.set('name', nm + ' <font color=GREEN><i>(Sync Complete)</i></font>');
				}
			}
		}
	}
});
