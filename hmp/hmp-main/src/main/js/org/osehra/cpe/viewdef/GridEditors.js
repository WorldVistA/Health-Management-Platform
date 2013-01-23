Ext.define('org.osehra.cpe.viewdef.GridEditors',{
	statics: {
		applyEditOpt: function(grd, coldef, editOpt) {
			var grid = grd;
			switch(editOpt.dataType) {
			case 'text':
				Ext.apply(coldef, {editor: {xtype: 'textfield'}});
				break;
			case 'boolean':
				Ext.apply(coldef, {editor: {xtype: 'checkbox'},
	                cls: 'x-grid-checkheader-editor'});
				Ext.apply(coldef, {xtype: 'checkcolumn', listeners: {checkchange: function(column, recordIndex, checked){
                    grid.getSelectionModel().select(recordIndex);
                    e = {
                         grid : grid,
                         record : grid.getSelectionModel().getSelection()[0],
                         field : 'visible',
                         value : checked,
                         rowIdx: recordIndex,
                         colIdx : column.getIndex(),
                         column: column
                        };
                    var debug = grid;
                    grid.editingPlugin.fireEvent('edit', this, e)  ;
				}}});
				break;
			case 'treatment':
				
				break;
			}
		}
	}
});
