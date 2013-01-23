Ext.define('org.osehra.cpe.search.SuggestBox', {
    extend:'Ext.form.field.ComboBox',
    mixins:{
        patientaware:'org.osehra.hmp.PatientAware'
    },
    alias:'widget.suggestbox',
    enableKeyEvents:true,
    hideTrigger:true,
    typeAhead: true,
    minLength:3,
    valueField: 'id',
    displayField: 'displayText',
    store:Ext.create('Ext.data.Store', {
    	fields:['id','displayText'],
        proxy:{
            type:'ajax',
            extraParams: {
                format: 'json'
            },
            reader: {
                type: 'json',
                root: 'data.items',
                totalProperty: 'totalItems'
            }
        }
    }),
    listeners:{
//        beforepatientchange: function(pid) {
//            var cmp = this;
//            if (pid != 0) {
//                cmp.clearValue();
//                cmp.collapse();
//            }
//        },
        patientchange: function(pid) {
            var cmp = this;
            this.pid = pid;
            if (pid != 0) {
                cmp.setDisabled(false);
                this.getStore().getProxy().url = '/vpr/v1/' + pid + '/suggest';
            } else {
                cmp.setDisabled(true);
            }
        },
        keydown: function(field, event) {
        	//Only suggest on alphanumeric input
        	if (event.isSpecialKey()) return;
        	
            var store = field.getStore();
            var value = field.getValue();
            
            if (store.isLoading()) {
//                Ext.Ajax.abort(store.getProxy().getConnection().transId);
            }
            if (value == null)  return;
            if (value.length < 3) return;
            
            store.getProxy().extraParams.prefix = value;
            store.load();
        }
    },
    initComponent: function() {
		var me = this;

		// handlers for exceptions
	    this.store.getProxy().on('exception', function() {
	    	var a = me;
	        me.el.mask('Component Received an Error. Try Reloading.')
	        me.store.removeAll();
	    });
	
		this.store.on('beforeload', function(store, op, eopts) {
		   op.skipErrors = true;
		});
		
    	// initalize component
    	this.callParent();
    },
    onBoxReady:function() {
        this.initPatientContext();
        this.callParent(arguments);
    }
});
