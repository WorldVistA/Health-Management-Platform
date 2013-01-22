/**
 * Controls TeamPositionPanel.
 */
Ext.define('EXT.DOMAIN.hmp.team.TeamPositionController', {
    extend:'EXT.DOMAIN.hmp.Controller',
    requires:[
        'EXT.DOMAIN.hmp.team.TeamPositionPanel'
    ],
    refs:[
        {
            ref:'nameField',
            selector:'#nameField'
        },
        {
            ref:'positionList',
            selector:'#positionList'
        },
        {
            ref:'positionForm',
            selector:'#positionEdit'
        }
    ],
    init:function () {
//        console.log(Ext.getClassName(this) + ".init()");
        var me = this;
        me.control({
            '#positionList':{
                select:me.onSelect
            },
            '#createButton':{
                click:me.onNew
            },
            '#deleteButton':{
                click:me.onDelete
            },
            '#saveButton':{
                click:me.onSave
            },
            '#nameField':{
                blur:me.onNameBlur
            }
        });
    },
    onSelect:function (grid, record, index) {
//        console.log("select()");
        var form = this.getPositionForm().getForm();
        form.loadRecord(record);
    },
    onNameBlur:function (field) {
        var form = this.getPositionForm().getForm();
        if (!form.isDirty()) return;
        form.updateRecord();
    },
    onNew:function () {
//        console.log("new position please!");
        var newPosition = Ext.create('EXT.DOMAIN.hmp.team.TeamPosition', {
            name:"New Position"
        });
        var positionStore = Ext.getStore('teamPositions');
        positionStore.add(newPosition);
        this.getPositionList().getSelectionModel().select(newPosition);
        this.getNameField().focus(true, 40);
    },
    onDelete:function () {
        this.getPositionList().getSelectionModel().clearSelections();
        var form = this.getPositionForm().getForm();
        var position = form.getRecord();
        var positionStore = Ext.getStore('teamPositions');
        positionStore.remove(position);
        var uid = position.getId();
        if (!uid) return; // no need to delete - it hasn't been saved yet.
        Ext.Ajax.request({
            url: '/teamMgmt/v1/position/' + uid,
            method: 'DELETE',
            jsonData: position.getData(),
            success: function(response, opts) {
                var json = Ext.decode(response.responseText);
                form.reset(true);
            },
            failure: function(response, opts) {
                console.log('server-side failure with status code ' + response.status);
            }
        });
//        this.down('grid').getSelectionModel()
    },
    onSave:function () {
        console.log("save position please!");
        var form = this.getPositionForm().getForm();
        var positionStore = Ext.getStore('teamPositions');
        form.updateRecord();
        var position = form.getRecord();
        if (!position) return;
        var uid = position.getId();
        Ext.Ajax.request({
            url: uid ? '/teamMgmt/v1/position/' + uid : '/teamMgmt/v1/position/new',
            method: 'POST',
            jsonData: position.getData(),
            success: function(response, opts) {
                var json = Ext.decode(response.responseText);
                if (!uid) position.set('uid', json.data.uid);
                position.commit();
            },
            failure: function(response, opts) {
                console.log('server-side failure with status code ' + response.status);
            }
        });
    }
});
