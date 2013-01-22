Ext.define('EXT.DOMAIN.cpe.patient.PatientSelectBox', {
    extend:'Ext.form.field.ComboBox',
    alias:'widget.patientselectbox',
    id:'patientselectbox',
    enableKeyEvents:true,
    hideTrigger:true,
    typeAhead: true,
    typeAheadDelay:3,
    valueField: 'id',
    displayField: 'name',
    // template to format the content in the combo box selection
    tpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
                '<div class="x-boundlist-item">{name},{gender},{ssn}</div>',
            '</tpl>'
        ),
    // template to format the content inside text field
    displayTpl: Ext.create('Ext.XTemplate',
        '<tpl for=".">',
            '{id} - {name}',
        '</tpl>'
    ),
    store:Ext.create('Ext.data.Store', {
    	fields:['id','name','gender','ssn','icn','dfn'],
        proxy:{
            type:'ajax',
        	url: '/roster/source',
            extraParams: {
                id: 'Patient'
            },
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),
 listeners:{  
       keydown: function(field, event) {
        	//only accept alphanumeric input
        	if (event.isSpecialKey()) return;
        	
            var value = field.getValue();
            if (value == null)  return;
            if (value.length < 4) return;
            
            var store = field.getStore();
            store.getProxy().extraParams.filter = value;
            store.load();
        },
        select: function(combo, record) {
            var dfn = record[0].data['dfn'];
        	Ext.getStore('patientSelectorStore').getProxy().extraParams.dfn=dfn;
        }
    }
});


//------------------------------------------------------
Ext.define('EXT.DOMAIN.cpe.patient.PatientSelectorPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.patientselectorpanel',
    frame: false,
    border: 0,
    store:Ext.create('Ext.data.Store', {
    	storeId:'patientSelectorStore',
    	fields:['message'],
        proxy:{
            type:'ajax',
        	url: '/patientSelector/select',
            extraParams: {
                id: 'icn'
            },
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            ui: 'plain',
            border: 0,
            padding: '20 0 20 0',
            items: [
                '->',
                {
                    xtype: 'patientselectbox',
                    itemId: 'patientselectBox',
					width: 400
                },
                {
                	xtype: 'button',
                	itemid: 'selectPatient',
                	text: 'Select Patient',
                	listeners: {
                		click: function(){
                			var store = Ext.getStore('patientSelectorStore');
                			if(store.getProxy().extraParams.dfn){
                				store.load();
                			}
                		}
                	}
                },
                '->'
            ]
        }
    ]
});
