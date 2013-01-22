/**
 * Created by IntelliJ IDEA.
 * User: vhaislpuleoa
 * Date: 11/17/11
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */

var gridStore = Ext.create('Ext.data.ArrayStore', {
    fields:[
//            ['name','category','assignmentStatus']
        {name:'name'},
        {name:'category'},
        {name: 'assignmentStatus'}
    ]
});

Ext.define('EXT.DOMAIN.cpe.patient.PatientChecksWindow', {
            extend: 'Ext.window.Window',
            title: 'Patient Checks',
            height: 200,
            width: 400,
            id: 'patientChecksWindow',
            alias: 'widget.patientcheckswindow',
            layout: 'fit',
            closable: false,
            closeAction: 'hide',
            modal: true,
            patient: '',
            mayAccess: true,
            logAccess: false,
            isSecurePatient: false,
            continuePatientLoading: true,
            defaultButton: 1,

            onEsc: function() {
//                if (this.isSecurePatient) this.continuePatientLoading = false;
                this.continuePatientLoading = false;
                this.hide();
            },
            
            listeners: {
            	render: function() {
	            	this.nav = Ext.create('Ext.util.KeyNav', this.getEl(), {
	                  	scope: this,
	                 	'esc': this.onEsc
	                });
            	}
            },
            
            load: function(data) {
                var me = this;
                var start = -1;
                var text='';
                var panel = me.down("#patientChecksPanel");

                // reset the var state
                me.continuePatientLoading = true;
                me.mayAccess = true;
                me.logAccess = false;
                me.isSecurePatient = false;
                if (data.deceased) {
                    start = 0;
                    panel.end ='deceased';
                    panel.items.items[0].update('<html><head></head><body><pre>' + data.deceased.text + '</pre></body></html>')
                }
                if (data.sensitive) {
                    me.isSecurePatient = true;
                    if (start == -1) start=1;
                    panel.end='security';
                    panel.items.items[1].update('<html><head></head><body><pre>' + data.sensitive.text + '</pre></body></html>');
                    if (!data.sensitive.mayAccess) {
                        me.mayAccess = false;
                        panel.items.items[1].update('This patient record may not be access')
                    }
                    if (data.sensitive.logAccess) me.logAccess = true
                }
                if (data.patientRecordFlag) {
                    if (start == -1) start=2;
                    panel.end = 'prf';
//                        text=buildPrfText(data.patientRecordFlag);
//                        panel.items.items[2].update(text);
                    var grid = panel.down('#prfGrid');
                    grid.store.removeAll();
                    grid.store.loadData(data.patientRecordFlag);

                }
                if (data.similar) {
                    if (start == -1) start=3;
                    panel.end = 'similar';
                    text="Placeholder for ss test";
                    panel.items.items[3].update('<html><head></head><body><pre>' + data.similar.text + '</pre></body></html>');
                }
                if (data.means) {
                    if (start == -1) start=4;
                    panel.end = 'means';
                    text="Placeholder for means test";
                    panel.items.items[4].update(text);
                }
                if (start>-1) {
                    if (!me.mayAccess) {
                        panel.end = 1;
                        me.continuePatientLoading = false;
                    }
                    me.show();

//                    picker.enable = false;
                    panel.getLayout().setActiveItem(start);
                } else {
                	me.hide();
                }
                
            },

            items: {
                xtype: 'panel',
                itemId: 'patientChecksPanel',
                border: false,
                layout: 'card',
                activeItem: 0,
                end: '',
                bbar: ['->',
                    {
                        id: 'cancelBtn',
                        text: 'Cancel',
                        listeners: {
                            click: function() {
                                this.up('#patientChecksWindow').onEsc();
                            }
                        }
                    },
                    {
                        id: 'card-next',
                        text: 'Continue &raquo;',
                        listeners: {
                            click: function() {
                                var panel = this.up('#patientChecksPanel');
                                var cardLayout = panel.getLayout();
                                var window = this.up('#patientChecksWindow');
                                if (cardLayout.getActiveItem().id == panel.end) {
                                   if (window.logAccess) {
                                       Ext.Ajax.request({
                                            url: '/vpr/chart/patientSecurityLog?pid=' + window.patient,
                                            failure: function(resp) {
                                                alert("Error could not connect to VistA");
					                            clazz.reportError('Error saving to VistA: ' + resp.responseText);
				                            },
                                            success: function(resp) {
                                                var data = Ext.JSON.decode(resp.responseText);
                                                if (data.result == 'fail') {alert('Could not log the patient in VistA')}
                                            }
                                       })
                                   }
                                   window.hide();
                                   return;
                                }
                                if (cardLayout.getNext()) cardLayout.next();
                                else {

                                    window.hide();
                                }
                            }
                        }
                    }
                ],


                items: [
                    {
                        id:'deceased',
                        html: 'Deceased'
                    },
                    {
                        id:'security',
                        html: 'Security'
                    },
                    {
                        id:'prf',
                        xtype: 'grid',
                        itemId: 'prfGrid',
                        store: gridStore,
                        columns: [
                            {
                                header: 'PRF',
                                dataIndex: 'name',
                                sortable: false,
                                hideable: false,
                                flex: 1
                            },
                            {
                                header: 'Category',
                                dataIndex: 'category',
                                sortable: false,
                                hideable: false,
                                order: 'asc'
                            },
                            {
                                header: 'Status',
                                dataIndex: 'assignmentStatus',
                                sortable: false
                            }
                        ],
                        renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                         if (value == "I (NATIONAL)") {
                            metaData.attr = 'style="color:red;"';
                         }
                        }
                    },
                    {
                        id:'similar',
                        html: 'Similar'
                    },
                    {
                        id:'means',
                        html: 'means'
                    }
                ]
            }
        });
