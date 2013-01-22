Ext.define('EXT.DOMAIN.cpe.viewdef.ViewDefCellEditing', {
	extend: 'Ext.grid.plugin.CellEditing',
	alias: 'plugin.viewdefcellediting',
	clicksToEdit: 1,
	listeners: {
		edit: function(editor, e, eOpts) {
			var rec = e.record;
			var fld = e.field;
			var uid = rec.get('uid');
			var pid = rec.get('pid');
			if(!pid && e.grid != null) {
				pid = e.grid.pid;
			}
			var editOpt = e.column.editOpt;
			var fieldName = editOpt.fieldName; // Very well may not be the same as the fieldName in the store.
			var value = e.value;
			if(editOpt.submitOpts) {
				if(editOpt.submitOpts.type=='singleCellOrganism') {
					uid = '';
					// C'mon, why can't JS have ?. and/or Elvis operators???
					if(rec.srcJson && rec.srcJson[fld] && rec.srcJson[fld].data && rec.srcJson[fld].data.length>1) {
						uid = rec.srcJson[fld].data[0].uid;
					}
					Ext.Ajax.request({
						url: editOpt.submitOpts.url,
						method: 'POST',
						params: {
							uid: uid,
							pid: pid,
							fieldName: fieldName,
							value: value
						},
						success: function(a, b, c) {
							rec.srcJson[fld].data[0][fieldName] = value;
							rec.set(fieldName, value);
						},
						failure: function(a, b, c) {
							Ext.MessageBox.alert('Save failed','See error log for details: '+a);
						}
					})
				}
			} else {
				Ext.Ajax.request({
					url: '/editor/submitFieldValue',
					method: 'POST',
					params: {
						uid: uid,
						pid: pid,
						fieldName: fieldName,
						value: value
					},
					success: function(a, b, c) {
						Ext.log("YAY!: "+a);
					},
					failure: function(a, b, c) {
						Ext.log("FAIL: "+a);
					}
				})
			}
		}
	}
});
