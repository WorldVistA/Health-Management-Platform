/**
 * Singleton for tracking patient context and coordinating patient context changes.
 *
 * The <pre>setPatientContext()</pre> method can be called to initate the multi-step
 * process of switching patient context which goes something like this:
 *
 * <ol>
 * <li>Fire the <pre>beforepatientchange</pre> event on all PatientAware components</li>
 * <li>If all event listeners return true, then ask the server to validate/authorize the new patient context
 *    - checks if its a valid patient (in the VPR)
 *    - checks for any patient access flags/warnings/checks</li>
 * <li>If there are any patient flags/warnings/checks to display, prompt the user and cancel
 * the whole patient context change if they do not acknowledge them</li>
 * <li>Fire the <pre>patientchange</pre> event on all PatientAware components.</li>
 * <li>Query all the PatientAware.pid component values to ensure that they all agree that the new patient context is the same.</li>
 * </ol>
 *
 * @see org.osehra.hmp.PatientAware
 */
Ext.define('org.osehra.hmp.PatientContext', {
    requires: [
        'org.osehra.cpe.patient.PatientChecksWindow',
        'org.osehra.cpe.EventListener'
    ],
    singleton: true,
    config: {
        /**
         * @cfg patientInfo A patient
         *
         */
        patientInfo: {}
    },
    pid: 0, // the current patient ID (0=no patient currently selected)
    patientPoll: true, // if true, will poll the current patient periodically to look for updated data.
    patientPollIntervalSec: 30, // how often should the patient be polled?
    patientPollLast: 0, // internal
    constructor: function(cfg) {
        var me = this;

        this.initConfig(cfg);

        // task for patient update polling (if patient polling and authenticated)
        if (this.patientPoll && this.patientPollIntervalSec > 0) {
            this.pollTask = {
                interval: me.patientPollIntervalSec * 1000,
                run: function() {
                    // if no patient context set, return
                    if (!me.patientAware || !me.pid || me.pid <= 0) {
                        return;
                    }
                    // AJAX call to get the last modified/updated for the current patient
                    Ext.Ajax.request({
                        url: '/roster/ping',
                        params: {
                            'pid': me.pid
                        },
                        success: function(response) {
                            // TODO: check for 404/redirect (or better yet, have the server not redirect on XHR requests)
                            var results = JSON.parse(response.responseText);

                            if (me.patientPollLast === 0) {
                                me.patientPollLast = results.items[0].lastUpdated;
                            } else if (me.patientPollLast !== results.items[0].lastUpdated) {
                                // if there is a difference, alert!
                                me.warn('Notice: New data available for  ' + results.items[0].domainsUpdated + '<a href="javascript:refresh('+ me.pid+');"> Click to reload</a>');
//                                me.warn('Notice: New data available for  ' + results.items[0].domainsUpdated + ' refreshing patient data', 6000);

                                var updates = results.items[0].domainsUpdated;
//                                updates = 'meds, documents, orders';
                                me.patientPollLast = results.items[0].lastUpdated;
                                //create array and fire event to change tab colors on new patient data
                                //TODO need to attached meta-data instead of searching by tab title
                                var domains = updates.split(', ');
                                org.osehra.hmp.PatientAware.setPatientUpdate(domains);
//                                refresh(me.pid);
                            }
                        }
                    });

                }
            }
            Ext.TaskManager.start(this.pollTask);
        }
        org.osehra.cpe.EventListener.listen();
    },
    /**
     * This initiates the process of coordinating/authorizing/updating the patient context change
     * of all the PatientAware components on the screen by running the pre-switch-checks
     * on both the client and server.
     *
     * @return false IIF one of the PatientAware components veto'ed the change
     */
    setPatientContext: function(pid) {

        var clazz = this; // b/c this is in a static context, this refers to the class (not instance)

        // reset the patient update date (for the patient poller)
        clazz.patientPollLast = 0;

        // first let any component veto a context change (ie: maybe there is a dirty editor)
        var comps = Ext.ComponentQuery.query('[patientAware=true]');
        for (var i=0; i < comps.length; i++) {
            // TODO: could they return a string (veto reason eg 'You must save your worksheet first')?
            if (comps[i].fireEvent('beforepatientchange', pid) !== true) {
                // TODO: should this be the error mask? Mabye ErrorManager.warn()?
                return false;
            }
        }

        // now let the server validate/authorize the new patient context
        clazz.pid = pid;
//        Ext.log(Ext.getClassName(clazz) + ".pid=" + clazz.pid);
        clazz.patientInfo = {};
        Ext.Ajax.request({
            url: '/roster/select?pid=' + pid,
            failure: function(resp) {
                clazz.reportError('Error selecting/changing patient context.  Please reload');
            },
            success: function(resp) {
                var data = Ext.JSON.decode(resp.responseText);
                clazz.patientInfo = data.patient || {};
                Ext.apply(clazz.patientInfo, {pid: pid});

                // if no patient security checks/flags/acknowledgements are required, then
                // actually do the patient context update and return
                if (!data || !data.checks || Ext.Object.getSize(data.checks) == 0) {
                    clazz.updatePatientContext(pid);
                    return;
                }

                // Otherwise, create/configure the PatientChecksWindow and display what is needed
                var checkWindow = Ext.getCmp('patientChecksWindow');
                if (!checkWindow) checkWindow = Ext.create('org.osehra.cpe.patient.PatientChecksWindow', {
                    listeners: {
                        hide: function() {
                            if (checkWindow.continuePatientLoading) {
                                clazz.updatePatientContext(checkWindow.patient);
                            } else {clazz.updatePatientContext(0) }
                            checkWindow.patient = '';
                        }
                    }
                });

                // configure the window with the data and show it
                checkWindow.patient = pid;
                checkWindow.setTitle('Patient Checks for: ' + clazz.patientInfo.fullName);
                checkWindow.load(data.checks);
            }
        });
    },

    /**
     * @private
     * Once the client and server have validated/confirmed the new requested patient context,
     * this does the actual work of changing the context and then verifying the change.
     */
    updatePatientContext: function(pid) {
        // get all patient aware components and change their context
        var comps = Ext.ComponentQuery.query('[patientAware=true]');
        for (var i=0; i < comps.length; i++) {
            if (comps[i].fireEvent('patientchange', pid) !== true) {
                this.reportError('Unable to register new patient context!', comps[i]);
                return false;
            }

            if (comps[i].pid !== pid) {
                this.reportError('Patient context validation error.', comps[i]);
                return false;
            }
        }
    },
    /**
     * @public
     * use to refresh patient form when new data comes in
     */
    refreshPatientContext: function(pid) {
        var clazz = this;
        // first let any component veto a context change (ie: maybe there is a dirty editor)
        var comps = Ext.ComponentQuery.query('[patientAware=true]');
        for (var i=0; i < comps.length; i++) {
            // TODO: could they return a string (veto reason eg 'You must save your worksheet first')?
            if (comps[i].fireEvent('beforepatientchange', pid) !== true) {
                // TODO: should this be the error mask? Mabye ErrorManager.warn()?
                return false;
            }
        }
        clazz.updatePatientContext(pid);
    },

    /**
     * @private
     * If there was an error in the context change process, then this will display/report/resolve it.
     */
    reportError: function(msg, errorCmp) {
        Ext.getBody().mask('Unrecoverable error changing the patient context.  <br/>Reason: ' + msg, 'x-mask-error');
        Ext.log("Error component: "+errorCmp);
    },

    setPatientUpdate: function(domains) {
        var comps = Ext.ComponentQuery.query('[patientAware=true]');
        for (var i=0; i < comps.length; i++) {
            if (comps[i].fireEvent('patientupdate', domains) !== true) {
                this.reportError('Unable to register updated patient!', comps[i]);
                return false;
            }
        }
    }
});

function refresh(pid) {
    org.osehra.hmp.PatientContext.refreshPatientContext(pid);
    Ext.ComponentQuery.query('#msgwinWarnId')[0].hide();
}
