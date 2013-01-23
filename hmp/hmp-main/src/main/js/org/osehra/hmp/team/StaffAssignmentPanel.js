Ext.define('org.osehra.hmp.team.StaffAssignmentPanel', {
    extend:'Ext.grid.Panel',
    requires:[
        'org.osehra.hmp.SegmentedButton',
        'org.osehra.hmp.team.TeamAssignment',
        'org.osehra.hmp.team.TeamPositionField'
    ],
    alias:'widget.staffeditor',
    minHeight:100,
    hideHeaders:true,
    rowLines:true,
    disableSelection:true,
    viewConfig:{
        stripeRows:false
    },
    emptyText:'There are no staff members associated with or positions configured for this team.',
    columns:[
        {
            text:'Position',
            dataIndex:'positionName',
            flex:1,
            tdCls:'hmp-label',
            editor:{
                xtype:'teampositionfield'
            }
        },
        {
//            xtype:'actioncolumn',
//            width:40,
//            items: [
//                {
//                    text: 'Remove',
//                icon: 'extjs/examples/restful/images/delete.png',
//                tooltip: 'Delete',
//                handler: function(grid, rowIndex, colIndex) {
//                    var rec = grid.getStore().getAt(rowIndex);
//                    alert("Terminate " + rec.get('firstname'));
//                }
//            }]
//            text:'Remove Position',
            xtype:'templatecolumn',
            width:60,
            tpl:'<a href="#">Remove</a>'
        },
        {
            flex:1,
            tdCls:'hmp-label',
            dataIndex:'boardName',
            renderer:function (value) {
                if (value) {
                    return value;
                } else {
                    return 'No Board Assigned';
                }
            }
        }
    ],
    features:[
        {
            ftype:'rowbody',
            getAdditionalData:function (data, rowIndex, record, orig) {
                var headerCt = this.view.headerCt,
                    colspan = headerCt.getColumnCount(),
                    rowBodyHtml = '<div class="hmp-team-position-tile hmp-team-position-tile-unassigned"><span>Empty</span></div>';
                var personName = record.get('personName');
                if (personName) {
                    rowBodyHtml = '<div class="hmp-team-position-tile"><img src="' + record.get('personPhotoHref') + '"/><span>' + personName + '</span><span class="x-form-clear-trigger"/></div>';
                }

                return {
                    rowBody:rowBodyHtml,
                    rowBodyColspan:colspan
                };
            }
        }
    ],
    plugins:[
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit:2
        })
    ],
    bbar:[
        {
            xtype:'teampositionfield',
            itemId:'addPositionButton',
            emptyText:'Add Position (Icon TBD)',
            tooltip:'Add Position'
        }
    ],
    initComponent:function () {
        this.store = Ext.create('Ext.data.Store', {
            model:'org.osehra.hmp.team.TeamAssignment',
            data:[]
        });
        this.callParent(arguments);
        this.addEvents(
            'assignmentchange',
            'positionadd',
            'positionremove'
        );
    },
    initEvents:function () {
        this.callParent(arguments);

        this.mon(this.down('#addPositionButton'), 'select', this.onSelectNewPosition, this);
    },
    afterRender:function () {
        this.callParent(arguments);
        this.initializeDropZone();
    },
    initializeDropZone:function () {
        var me = this;
        var v = me.getView();
        var gridView = v;

        this.dropZone = Ext.create('Ext.dd.DropZone', v.el, {

//      If the mouse is over a target node, return that node. This is
//      provided as the "target" parameter in all "onNodeXXXX" node event handling functions
            getTargetFromEvent:function (e) {
                return e.getTarget('.hmp-team-position-tile');
            },

//      On entry into a target node, highlight that node.
            onNodeEnter:function (target, dd, e, data) {
                Ext.fly(target).addCls('hmp-team-position-tile-over');
            },

//      On exit from a target node, unhighlight that node.
            onNodeOut:function (target, dd, e, data) {
                Ext.fly(target).removeCls('hmp-team-position-tile-over');
            },

//      While over a target node, return the default drop allowed class which
//      places a "tick" icon into the drag proxy.
            onNodeOver:function (target, dd, e, data) {
                return Ext.dd.DropZone.prototype.dropAllowed;
            },

//      On node drop, we can interrogate the target node to find the underlying
//      application object that is the real target of the dragged data.
//      In this case, it is a Record in the GridPanel's Store.
//      We can use the data set up by the DragZone's getDragData method to read
//      any data we decided to attach.
            onNodeDrop:function (target, dd, e, data) {
                var rowBody = Ext.fly(target).findParent('.x-grid-rowbody-tr', null, false),
                    mainRow = rowBody.previousSibling,
                    staffAssignment = gridView.getRecord(mainRow),
                    targetEl = Ext.get(target);
                staffAssignment.beginEdit();
                staffAssignment.set('personUid', data.person.get('uid'));
                staffAssignment.set('personName', data.person.get('name'));
                staffAssignment.set('personPhotoHref', data.person.get('photoHref'));
                staffAssignment.endEdit();
                staffAssignment.commit();
                gridView.refresh();
                me.fireEvent('assignmentchange', me, staffAssignment);
                return true;
            }
        });
    },
    onSelectNewPosition:function (combo, records) {
        var position = records[0];
        this.fireEvent('positionadd', this, position);
        var gridView = this.getView();
        var staffAssignment = Ext.create('org.osehra.hmp.team.TeamAssignment', {
            positionUid:position.get('uid'),
            positionName:position.get('name')
        });
        this.getStore().add(staffAssignment);
        gridView.refresh();
        combo.clearValue();
    }
});
