/**
 * Controls behavior of {@link EXT.DOMAIN.hmp.admin.SyncAdminPanel}
 */
Ext.define('EXT.DOMAIN.hmp.admin.SyncAdminController', {
    extend:'EXT.DOMAIN.hmp.Controller',
    refs:[
        {
            ref:'messageField',
            selector:'#message'
        },
        {
            ref:'autoUpdateToggleButton',
            selector:'#autoUpdateToggle'
        },
        {
            ref:'syncForm',
            selector:'#syncForm'
        },
        {
            ref:'rosterField',
            selector:'#rosterField'
        },
        {
            ref:'clearForm',
            selector:'#clearForm'
        },
        {
            ref:'reindexForm',
            selector:'#reindexForm'
        }
    ],
    init:function () {
//        console.log(Ext.getClassName(this) + ".init()");
        var me = this;

        me.control({
            '#autoUpdateToggle': {
                click:me.toggleAutoUpdates
            },
            '#syncRosterButton':{
                click:me.syncRoster
            },
            '#syncPatientButton':{
                click:me.syncPatient
            },
            '#clearAllSyncErrorsButton':{
                click:function () {
                    Ext.MessageBox.confirm('Clear All Sync Errors', 'Are you sure you want to clear all sync errors?', me.clearAllSyncErrors, me);
                }
            },
            '#clearAllPatientsButton':{
                click:function () {
                    Ext.MessageBox.confirm('Clear All Patients', 'Are you sure you want to clear all patients from the VPR?', me.clearAllPatients, me);
                }
            },
            '#clearPatientButton':{
                click:function () {
                    var form = me.getClearForm().getForm();
                    var vals = form.getValues();
                    var pid = null;
                    var valid = (
                        (vals['pid'] && vals['pid'].length > 0) ||
                            (vals['icn'] && vals['icn'].length > 0) ||
                            ((vals['dfn'] && vals['dfn'].length > 0) && (vals['fcode'] && vals['fcode'].length > 0))
                        );
                    if (!valid) {
                        Ext.MessageBox.alert('Clear Patient', 'No patient specified. Please supply a DFN + Facility Code, an ICN, or a VPR PID to clear from the VPR.');
                    } else {
                        Ext.MessageBox.confirm('Clear Patient', 'Are you sure you want to clear this patient from the VPR?', me.clearPatient, me);
                    }
                }
            },
            '#reindexPatientButton':{
                click:me.reindexPatient
            },
            '#reindexAllButton':{
                click:me.reindexAllPatients
            }
        });
    },
    onLaunch:function() {
        Ext.getStore('statsStore').on('load', this.onLoad, this);
    },
    /**
     * @private
     */
    onLoad:function(store, records, successful) {
        var autoUpdates = store.findRecord('name', 'Automatic Updates');
        if (autoUpdates) {
            if (autoUpdates.get('value') == 'Disabled') {
                this.getAutoUpdateToggleButton().setText("Enable " + autoUpdates.get('name'));
            } else {
                this.getAutoUpdateToggleButton().setText("Disable " + autoUpdates.get('name'));
            }
        }
    },
    toggleAutoUpdates:function () {
        var me = this;
        Ext.Ajax.request({
            url:'/sync/toggleAutoUpdates',
            method:'POST',
            params:{
                format:'json'
            },
            success:function (response) {
                var json = Ext.JSON.decode(response.responseText);
                me.getMessageField().update(json.data.message);
                me.refreshStats();
            }
        });
    },
    syncRoster:function () {
        var me = this;
        var form = me.getSyncForm().getForm();
        if (form.isValid()) {
            form.submit({
                url:'/sync/loadRosterPatients',
                params:{
                    rosterId:me.getRosterField().getValue()
                },
                success:function (form, action) {
                    me.getMessageField().update(action.result.data.message);
                    me.refreshStats();
                },
                failure:function (form, action) {
                    Ext.Msg.alert('Failed', action.result.error.message);
                    me.refreshStats();
                }
            });
        }
    },
    syncPatient:function () {
        var me = this;
        var form = me.getSyncForm().getForm();
        if (form.isValid()) {
            form.submit({
                url:'/sync/load',
                success:function (form, action) {
                    me.getMessageField().update(action.result.data.message);
                    form.reset();
                    me.refreshStats();
                },
                failure:function (form, action) {
                    Ext.Msg.alert('Failed', action.result.error.message);
                }
            });
        }
    },
    clearPatient:function (btn) {
        if (btn === "no") return;

        var me = this;
        var form = me.getClearForm().getForm();
        if (form.isValid()) {
            form.url = '/sync/clearPatient';
            form.method = "POST";
            form.submit({
                params:{
                    vistaId:EXT.DOMAIN.hmp.UserContext.getUserInfo().vistaId
                },
                success:function (form, action) {
                    me.getMessageField().update(action.result.data.message);
                    form.reset();
                    me.refreshStats();
                },
                failure:function (form, action) {
                    Ext.Msg.alert('Failed', action.result.error.message);
                    me.refreshStats();
                }
            });
        }
    },
    clearAllSyncErrors:function (btn) {
        if (btn === "no") return;

        var me = this;
        Ext.Ajax.request({
            url:'/sync/syncErrors/clear',
            method:'POST',
            params:{
                format:'json'
            },
            success:function (response) {
                var json = Ext.JSON.decode(response.responseText);
                me.getMessageField().update(json.data.message);
                Ext.getStore('statsStore').load();
            }
        });
    },
    clearAllPatients:function (btn) {
        if (btn === "no") return;

        var me = this;
        Ext.Ajax.request({
            url:'/sync/clearAllPatients',
            method:'POST',
            params:{
                format:'json'
            },
            success:function (response) {
                var json = Ext.JSON.decode(response.responseText);
                me.getMessageField().update(json.data.message);
                Ext.getStore('statsStore').load();
            }
        });
    },
    reindexPatient:function () {
        var me = this;
        var form = me.getReindexForm().getForm();
        if (form.isValid()) {
            form.url = '/sync/reindexPatient';
            form.method = 'POST';
            form.submit({
                success:function (form, action) {
                    me.getMessageField().update(action.result.data.message);
                    form.reset();
                    me.refreshStats();
                },
                failure:function (form, action) {
                    var msg = me.getMessageField();
                    msg.addCls("hmp-error");
                    msg.update(action.result.error.message);
                    me.refreshStats();
                }
            });
        }
    },
    reindexAllPatients:function () {
        var me = this;
        var form = me.getReindexForm().getForm();
        if (form.isValid()) {
            form.url = '/sync/reindexAllPatients';
            form.method = 'POST';
            form.submit({
                success:function (form, action) {
                    me.getMessageField().update(action.result.data.message);
                    form.reset();
                    me.refreshStats();
                },
                failure:function (form, action) {
                    Ext.Msg.alert('Failed', action.result.error.message);
                    me.refreshStats();
                }
            });
        }
    },
    refreshStats:function () {
        Ext.getStore('statsStore').load();
    }
});
