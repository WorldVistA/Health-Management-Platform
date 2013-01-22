var gridOneStore = Ext.create('Ext.data.ArrayStore', {
    fields:[
        {name:'id'},
        {name:'name'},
        {name:'type'}
    ],
    data:{items:[
        {'id':1, 'name':'test1', 'type':'user'},
        {'id':2, 'name':'test2', 'type':'user'},
        {'id':3, 'name':'test3', 'type':'user'},
        {'id':4, 'name':'test4', 'type':'user'},
        {'id':5, 'name':'test5', 'type':'user'}
    ]},
    proxy:{
//                url: "/vpr/chart/orderingControl",
        type:"memory",
        reader:{
            type:'json',
            root:"items"
        }
    }
});

var gridTwoStore = Ext.create('Ext.data.ArrayStore', {
    fields:[
        {name:'id'},
        {name:'name'},
        {name:'type'}
    ]
//            data:{items: [
//                {'id': 1, 'name':'test1', 'type':'user'},
//                {'id': 2, 'name':'test2', 'type':'user'},
//                {'id': 3, 'name':'test3', 'type':'user'},
//                {'id': 4, 'name':'test4', 'type':'user'},
//                {'id': 5, 'name':'test5', 'type':'user'}
//            ]},
//            proxy: {
//                url: "/vpr/chart/orderingControl",
//                type: "ajax",
//                reader: {
//                    type: 'json',
//                    root: "items"
//                }
//            }
});


Ext.define('EXT.DOMAIN.cpe.TaskWindow', {
    extend:'Ext.window.Window',
    title:'Create a Task',
    height:400,
    id:'taskWindow',
    width:400,
    layout:{
        type:'fit'
    },
    items:[
        {
            xtype:'form',
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
            items:[
                {
                    xtype:"textfield",
                    name:'taskName',
                    fieldLabel:'Title',
                    allowBlank:false,
                    itemId:'taskNameField',
                    hideEmptyLabel:false,
                    width:'100%'

                },
                {
                	xtype: 'panel',
                	width: '100%',
                	layout: {type: 'hbox', align: 'stretch'},
                	items: [
                	       {
                	    	   margin: '0 0 0 0',
                	    	   xtype:'datefield',
                               fieldLabel:'Due',
                               name:'dueDate',
                               format: 'Y-m-d',
                               allowBlank:false,
                               submitValue:false,
                               flex: 1
                	       },
                	       {
                	    	   margin: '0 0 0 10',
                	    	   xtype: 'combobox',
                	    	   fieldLabel: 'Type',
                	    	   name: 'type',
                	    	   allowBlank:false,
                	    	   store: ['General','Order','Transfer','Treatment'],
                	    	   value: 'General',
                	    	   flex: 1
                	       }
                	]
                   
                },
                {
                    xtype:'htmleditor',
                    fieldLabel:'Description',
                    name:'description',
                    allowBlank:false,
                    flex:1
                }
            ]
        }
    ],
            buttons:  [
                { xtype: 'button', itemId: "closeBtn",  text: 'Cancel',
                    listeners: {
                        click: function() {
                            var win = Ext.getCmp('taskWindow');
                            win.close();
                        }
                    }
                },
                { xtype: 'button', itemId: "acceptBtn",  text: 'Accept',
                    listeners: {
                        click:function (bn) {
                        	bn.disable();
//                        var me = this;
                        var win = bn.up('window');
//                        var po = win.getPatientInfo();
                        var pid = win.pid;
                        var form = win.down('form').getForm();
                        if (form.isValid()) {
                            var dueDate = form.findField("dueDate").getValue();
                            if (dueDate) {
                                dueDate = Ext.Date.format(dueDate, Ext.Date.patterns.HL7);
                            }
                            form.submit({
                                url:'/vpr/chart/addTask',
                                params:{
                                    patientId: pid,
                                    dueDate: dueDate
                                },
                                success:function (form, action) {
                                    var grids = Ext.ComponentQuery.query('viewdefgridpanel');
                                    for (var i = 0; i < grids.length; i++) {
                                        var title = grids[i].title.toString();
                                        if (!grids[i].curViewID) continue;
                                        if (title.substring(0,5) == 'Tasks') {
                                            var grid = grids[i];
                                            grid.setViewDef(grid.curViewID, grid.curViewParams);
                                        }
                                    }
                                    win.close();
                                },
                            failure:function (form, action) {
                                var data = Ext.decode(action.response.responseText);
                                Ext.Msg.alert('Failed', data.error.message);
                                win.close();
                            }
                        });
                    }
                }
            }
        }
    ],
    onBoxReady:function () {
        this.initPatientContext();
        this.callParent(arguments);
        this.disable();
        this.load();
    },
    initPatientContext: function() {
    	var me = this;
    	var po = EXT.DOMAIN.hmp.PatientContext.getPatientInfo();
    	var pid, name;
    	if(me.patientRec) {pid = me.patientRec.get('pid'); name = me.patientRec.get('name');}
    	else if(po!=null) {pid = po.pid; name = po.fullName;}
    	else { Ext.Msg.alert("Error", "No patient is selected");
    	me.close();return;}
    	me.setTitle('Task for ' + name);
    	me.pid = pid;
    	me.enable();
    },
    load: function() {
        var me = this;
        if(me.patientRec) {
        	me.setTitle('Task for ' + me.patientRec.get('name'));
        	me.enable();
        } else {
        	var po = EXT.DOMAIN.hmp.PatientContext.getPatientInfo();
            if (po.fullName) {
                me.setTitle('Task for ' + po.fullName);
                me.enable();
            } else {
                Ext.Msg.alert("Error", "No patient is selected")
                me.close();
            }
        }
       
        if(me.task) {
        	me.down('form').getForm().loadRecord(me.task);
        }
    },
    statics: {
    	/*
    	 * Black magic part 2 of 2
    	 */
    	showTaskForPatient: function(e, pid) {
    		if(e) {
    			if(e.stopPropogation) {
        			e.stopPropogation();
    			} else {
    				e.cancelBubble = true;
    			}
    		}
			var taskWindow = Ext.getCmp('taskWindow');
            if (!taskWindow) taskWindow = Ext.create('EXT.DOMAIN.cpe.TaskWindow', {
            	task: {
            		data: {
            			'type':'Order'
             		}
            	}
            });
            taskWindow.pid = pid;
            var picker = Ext.ComponentQuery.query('patientpicker')[0];
            var vdgp = picker.down('viewdefgridpanel');
            var store = vdgp.getStore();
            var rec = store.findRecord('pid',pid);
            taskWindow.patientRec = rec;
            taskWindow.show();
            return taskWindow;
    	}
    }
});
