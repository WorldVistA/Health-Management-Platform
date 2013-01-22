Ext.define('EXT.DOMAIN.cpe.patient.PatientBanner', {
    extend:'Ext.panel.Panel',
    requires:[
        'EXT.DOMAIN.hmp.PopUpButton',
        'EXT.DOMAIN.hmp.EventBus',
        'EXT.DOMAIN.hmp.PhotoPicker'
    ],
    alias:'widget.ptbanner',
    mixins:{
        patientaware:'EXT.DOMAIN.hmp.PatientAware'
    },
    layout:{
        type:'hbox',
        align:'stretch'
    },
    height:85,
    componentCls:'hmp-pt-banner-ct',
    id:'ptBanner_id',
    itemId:'ptBannerID',
    disabled:true,
    defaults:{
        componentCls:'hmp-pt-banner-item'
    },
    items:[
        {
            xtype:'button',
            icon:'/images/icons/back.png',
            id:'patientNavPreviousButton',
            itemId:'previous_ID',
            iconAlign:'center',
            ui:'plain',
            scale:'large',
            minWidth:17,
            maxWidth:17,
            padding:'26 0 0 0',
            handler:function (button, e) {
                EXT.DOMAIN.hmp.EventBus.fireEvent('prevPatient', button);
            }
        },
        {
//            xtype:'image',
            xtype:'popupbutton',
            itemId:'pt-picture',
            cls:'hmp-pt-picture',
            scale:'large',
            minWidth:74,
            popUp:{
                xtype:'photopicker',
                listeners: {
                    load: function(picker) {
                        var menu = picker.up('menu');
                        menu.ownerButton.setIcon("/vpr/v1/" + EXT.DOMAIN.hmp.PatientContext.pid + "/photo?_dc=" + (new Date().getTime()));
                    }
                }
            },
            popUpButtons:[
                {
                    text:'Cancel',
                    handler:function (btn) {
                        var menu = btn.up('menu');
                        menu.hide();
                    }
                },
                {
                    ui:'theme-colored',
                    text:'Set Patient Photo',
                    handler:function (btn) {
                        var menu = btn.up('menu');
                        var photopicker = menu.down('photopicker');
                        photopicker.uploadTo('/vpr/v1/' + EXT.DOMAIN.hmp.PatientContext.pid + '/photo?_dc=' + (new Date().getTime()));
                        menu.hide();
                    }
                }
            ],
            listeners:{
                afterrender:function () {
                    var dragZone = new Ext.dd.DragZone(this.getEl(), {

                        //ddGroup: 'PatientGroup',
                        getDragData:function (e) {
                            var sourceEl = e.getTarget(this.itemSelector, 10);
                            if (sourceEl) {
                                d = sourceEl.cloneNode(true);
                                d.id = Ext.id();
                                return {
                                    ddel:d,
                                    sourceEl:sourceEl,
                                    repairXY:Ext.fly(sourceEl).getXY()
                                    //sourceStore: this.store,
                                    //draggedRecord: this.getRecord(sourceEl)
                                }
                            }
                        },

                        getRepairXY:function () {
                            return this.dragData.repairXY;
                        }
                    });
                },
                menuhide:function (popupBtn, menu) {
                    var photopicker = menu.down('photopicker');
                    photopicker.reset();
                }
            }
        },
        {
            xtype:'button',
            icon:'/images/icons/frwd.png',
            id:'patientNavNextButton',
            itemId:'next_ID',
            iconAlign:'center',
            ui:'plain',
            scale:'large',
            minWidth:17,
            maxWidth:17,
            padding:'26 0 0 0',
            handler:function (button, e) {
                EXT.DOMAIN.hmp.EventBus.fireEvent('nextPatient', button);
            }
        },
        {
            xtype:'popupbutton',
            scale:'large',
            flex:2,
            tpl:['<div><span style="font-size: 125%; font-weight: bold;">{name}</span> ({age}yo {gender})</div>',
                '<table class="hmp-labeled-values" style="float:left">' +
                    '<tr><td>DOB</td><td>{dob:date("Y-m-d")}</td></tr>' +
                    '<tr><td>SSN</td><td>{ssn}</td></tr>' +
                    '<tr><td/><td><tpl if="city">{address.city}, &nbsp;</tpl>{address.state}&nbsp;&nbsp;{address.zip}</td></tr>' +
                    '</table>',
                '<table class="hmp-labeled-values" style="float:right">' +
                    '<tpl if="lastVitals.height"><tr><td>Ht</td><td>{lastVitals.height.value} ({lastVitals.height.lastDone})</td></tr></tpl>' +
                    '<tpl if="lastVitals.weight"><tr><td>Wt</td><td>{lastVitals.weight.value} ({lastVitals.weight.lastDone})</td></tr></tpl>' +
                    '<tr><td>Tel</td><td>{address.phone}</td></tr>' +
                    '</table>'],
            popUp:{
                width:650,
                height:350,
                autoScroll:true,
                tpl:'<pre style="padding-left: 6">{patDemDetails.text}</pre>'
            }
        },
        {
            xtype:'popupbutton',
            scale:'large',
            flex:3,
            tpl:['<table class="hmp-labeled-values" style="float:left">' +
                '<tr><td>Inpatient Location</td><td><tpl if="inpatientLocation">{inpatientLocation}</tpl></td></tr>' +
                '<tr><td>Primary Care Team</td><td>{teamInfo.team.name}</td></tr>' +
                '<tr><td>Primary Care Provider</td><td>{teamInfo.primaryProvider.name}</td></tr>' +
                '<tr><td>Attending</td><td>{teamInfo.attendingProvider.name}</td></tr>' +
                '</table>'
            ],
            popUp:{
                width:400,
                height:200,
                autoScroll:true,
                tpl:'<pre style="padding-left: 6">{teamInfo.text}</pre>'
            }
        },
        {
            xtype:'popupbutton',
            scale:'large',
            flex:1,
            tpl:[
                '<div class="hmp-label" style="text-align: center">Postings</div>' +
                    '<tpl if="cwad"><div style="color: red; font-weight: bold; font-size:125%;text-align: center" id="CWADPostings">{cwad}</div></tpl>' +
                    '<tpl if="cwad===undefined"><div style="font-weight: bold; font-size:125%;text-align: center">\<None\></div></tpl>'
            ],
            menuAlign:'tr-br?',
            popUp:{ // Perhaps this should be a hidden component that is merely shown later.
                xtype:'viewdefgridpanel',
                width:650,
                height:300,
                title:"Profile Docs",
                titleTpl:'Profile Docs ({total})',
                viewID:'EXT.DOMAIN.cpe.vpr.queryeng.ProfileDocsViewDef',
                detailType:'right',
                tools:[]
            }
        }//,
//        {
//            xtype:'component',
//            minWidth:225,
//            flex:0,
//            tpl:[
//                '<div>Svc Connected: {serviceConnectedPct}%</div>',
//                '<div><span style="color: red">[{[values.allergies?values.allergies.length:0]}] ALLERGIES</span><br/><tpl if="allergies" for="allergies"><span title="{Facility}\nEntered: {entered}\n{comments}"><li>{Summary}</li></span></tpl></div>'
//            ]
//
//        }
    ],
    listeners:{
        beforepatientchange:function (cmp, pid) {
            var patientInfoWindow = Ext.getCmp('patientInfoWindow');
            if (patientInfoWindow) patientInfoWindow.hide();
        },
        patientchange:function (pid) {
            this.setPatient(pid);
        }
    },
    initComponent:function () {
        this.callParent(arguments);
        EXT.DOMAIN.hmp.EventBus.on('patientNav', this.refreshNavButtons, this);
    },
    onBoxReady:function () {
        this.initPatientContext();
        this.callParent(arguments);
    },
    /**
     * @private
     */
    setPatient:function (pid) {
//        console.log("setPatient(" + pid + ")");
        var me = this;
        me.disable();
        me.pid = pid;
        me.down('#pt-picture').setIcon("/vpr/v1/" + pid + "/photo");
        if (pid > 0) {
            // TODO: build this into PatientContext some how
            Ext.Ajax.request({
                url:'/vpr/chart/getPatientInfo',
                method:'GET',
                params:{
                    pid:pid
                },
                listeners:{
                    beforerequest:function () {
                        if (me.body)
                            me.body.unmask();
                    }
                },
                success:function (resp) {
                    var data = Ext.decode(resp.responseText);
                    me.update(data);
                    me.enable();
                },
                failure:function (loader, response) {
                    //this looks like bug in JS when failure called if success is undefined for loader
                    //look in response status
                    if (response.status != 200) {
                        me.update("");
                        if (me.body) {
                            me.body.mask("Component Received an Error. Try Reloading.");
                        }
                    }
                }
            });
        }
    },
    update:function (htmlOrData, loadScripts, cb) {
        var me = this;
        if (Ext.isString(htmlOrData)) {
            me.callParent(arguments);
        } else {
            for (var i = 0; i < me.items.getCount(); i++) {
                var pnl = me.getComponent(i);
                if (pnl.tpl) {
                    pnl.update(htmlOrData);
                }
            }
        }
    },
    refreshNavButtons:function (source, event) {
        if (event.hasNext) {
            this.down('#next_ID').enable();
        } else {
            this.down('#next_ID').disable();
        }
        if (event.hasPrev) {
            this.down('#previous_ID').enable();
        } else {
            this.down('#previous_ID').disable();
        }
    }
});
