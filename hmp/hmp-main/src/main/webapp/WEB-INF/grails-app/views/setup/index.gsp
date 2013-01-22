<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<title>${message(code: 'platform.name')} &raquo; Setup</title>
<g:render template="/layouts/extjs"/>
<g:render template="/setup/style"/>
<script type="text/javascript">
    Ext.require('Ext.container.Viewport');
    Ext.onReady(function () {
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items:[
                {
                    xtype:'container',
                    region: 'center',
                    width:'100%',
                    height:'100%',
                    layout:{
                        type:'border'
                    },
				    autoScroll:true,
                    items:[
                       {
                        xtype:'form',
                        frame:false,
                        border:0,
                        width:791,
                        margin:'0 80',
                        // Fields will be arranged vertically, stretched to full width
                        layout:'vbox',
                        fieldDefaults:{
                            labelAlign:'right'
                        },
                        defaultType:'component',
                        items:[
							{
								xtype:'panel',
								width:791,
								defaults:{ bodyStyle:'padding:10px'},
								layout:{
								   type:'table',
								   columns: 3,
								},
								items:[
									{
									<g:if test="${status.complete}">
										html:'<h1>${message(code:"platform.name")} Setup</h1><p>The ${message(code:"platform.name")} setup complete.</p>',
									</g:if>
									<g:else>
									 	html:'<h1>${message(code:"platform.name")} Setup</h1><p>The ${message(code:"platform.name")} needs some more information before it is fully installed.</p>',
									</g:else>
									colspan: 3
									},
									//first row
									{html:'<b>System Health Check</b>', colspan: 3},
									{html:'<i>JDS:</i>'},
									{ xtype:'image',
									  <g:if test="${status.jds}">
										  src:'/images/themes/hi2/valid.gif',
									  </g:if>
									  <g:else>
										  src:'/images/themes/hi2/invalid.gif',
									  </g:else>
									   colspan:2
									},
									{html:'<i>Solr:</i>'},
									{ xtype:'image',
									  <g:if test="${status.solr}">
										  src:'/images/themes/hi2/valid.gif',
									  </g:if>
									  <g:else>
										  src:'/images/themes/hi2/invalid.gif',
									  </g:else>
									   colspan:2
									},
									{html:'<i>Infobutton Service:</i>'},
									{ xtype:'image',
									  <g:if test="${status.openInfobutton}">
										  src:'/images/themes/hi2/valid.gif',
									  </g:if>
									  <g:else>
										  src:'/images/themes/hi2/invalid.gif',
									  </g:else>
									   colspan:2
									},
									{html:'<i>Term Engine:</i>'},
									{ xtype:'image',
									  <g:if test="${status.termDb}">
										  src:'/images/themes/hi2/valid.gif',
									  </g:if>
									  <g:else>
										  src:'/images/themes/hi2/invalid.gif',
									  </g:else>
									},
									{html:
									  <g:if test="${status.msgTermDb}">
										"${status.errTermDb}"
									   </g:if>
									   <g:else>
										 "", colspan:2
									   </g:else>
									},
									{html:'<i>Vista RPC connection</i>:'},
									{ xtype:'image',
									  <g:if test="${status.vista}">
										  src:'/images/themes/hi2/valid.gif',
									  </g:if>
									  <g:else>
										  src:'/images/themes/hi2/invalid.gif',
									  </g:else>
									},
									<g:if test="${status.errVista}">
										{html:"${status.errVista}"}
									</g:if>
								  ]
							   
							},
                            {
                                xtype:'panel',
                                //layout:'hbox',
                                width:791,
                                flex:1,
                                items:[
                                    {
                                        xtype:'panel',
                                        layout:'anchor',
                                        //padding:'0 0 40 0',
                                        defaults:{
                                            width:'100%',
                                            anchor:'100%',
                                            labelSeparator:null
                                        },
                                        flex:1,
                                        items:[
                                            {
                                                html:'<h2>Enter ${message(code:"platform.name")} Settings</h2>',
                                                itemId:'platformConfigTitle',
                                            },
                                            {
                                                xtype:'container',
                                                itemId:'platformConfig',
                                                layout:'anchor',
                                                defaults:{
                                                    anchor:'100%',
                                                    labelSeparator:null
                                                },
                                                items:[
                                                    {
                                                        xtype:'textfield',
                                                        fieldLabel:'Server ID',
                                                        name:'serverId',
                                                        value:'${setup.serverId}',
                                                        readOnly:true
                                                    },
                                                    {
                                                        xtype:'textfield',
                                                        fieldLabel:'Server Host',
                                                        name:'serverHost',
                                                        value:'${setup.serverHost}'
                                                    },
                                                    {
                                                        xtype:'numberfield',
                                                        fieldLabel:'HTTP Port',
                                                        name:'httpPort',
                                                        value:'${setup.httpPort}'
                                                    },
                                                    {
                                                        xtype:'numberfield',
                                                        fieldLabel:'HTTPS Port',
                                                        name:'httpsPort',
                                                        value:'${setup.httpsPort}'
                                                    }
                                                ]
                                            },
                                        ]
                                    },
                                    {
                                        xtype:'panel',
                                        layout:'anchor',
                                        //padding:'0 0 0 40',
                                        defaults:{
                                            width:'100%',
                                            anchor:'100%',
                                            labelSeparator:null
                                        },
                                        flex:1,
                                        items:[
                                            %{--<g:if test="${vistaAccounts.isEmpty()}">--}%
                                            {
                                                html:'<h2>VistA System Configuration</h2><p>Configure a VistA system for The ${message(code:"platform.name")} to use as a data source.</p>',
                                                itemId:'initialVistaAccountConfigTitle'
                                            },
                                            {
                                                xtype:'container',
                                                itemId:'initialVistaAccountConfig',
                                                layout:'anchor',
                                                defaults:{
                                                    anchor:'100%',
                                                    labelSeparator:null
                                                },
                                                defaultType:'textfield',
                                                items:[
                                                    {
                                                        xtype:'hiddenfield',
                                                        itemId:'vistaIdField',
                                                        name:'vista.vistaId',
                                                        value:'${vistaAccount?.vistaId}'
                                                    },
                                                    {
                                                        xtype:'textfield',
                                                        fieldLabel:'<g:message code="vistaAccount.division.label" default="Station Number" />',
                                                        name:'vista.division',
                                                        value:'${vistaAccount?.division}',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        xtype:'textfield',
                                                        fieldLabel:'<g:message code="vistaAccount.name.label" default="Name" />',
                                                        name:'vista.name',
                                                        value:'${vistaAccount?.name}',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        xtype:'textfield',
                                                        fieldLabel:'<g:message code="vistaAccount.host.label" default="Host" />',
                                                        name:'vista.host',
                                                        value:'${vistaAccount?.host}',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        xtype:'numberfield',
                                                        fieldLabel:'<g:message code="vistaAccount.port.label" default="Port" />',
                                                        name:'vista.port',
                                                        value:'${vistaAccount?.port}',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        xtype:'checkboxfield',
                                                        fieldLabel:'<g:message code="vistaAccount.production.label" default="Production" />',
                                                        name:'vista.production',
                                                        value:'${vistaAccount?.production}'
                                                    }
                                                ]
                                            },
                                            %{--</g:if>--}%
                                            {
                                                html:'<h2>VPR Synchronization User Configuration</h2>',
                                                itemId:'vprSyncUserConfigTitle'
                                            },
                                            {
                                                xtype:'container',
                                                itemId:'vprSyncUserConfig',
                                                padding:'5',
                                                layout:'anchor',
                                                defaultType:'textfield',
                                                defaults:{
                                                    anchor:'100%',
                                                    labelSeparator:null
                                                },
                                                items:[
                                                    <g:if test="${!vistaAccounts.isEmpty()}">
                                                    {
                                                        xtype:'combobox',
                                                        itemId:'divisionCombo',
                                                        fieldLabel:'Facility',
                                                        autoSelect:false,
                                                        emptyText:'-- select one --',
                                                        store:Ext.create('Ext.data.Store', {
                                                            autoLoad:true,
                                                            fields:[
                                                                'id', 'vistaId', 'division', 'name', 'host', 'port', 'production'
                                                            ],
                                                            data:[
                                                                <g:each in="${vistaAccounts}" status="i" var="vistaAccountInstance">
                                                                {
                                                                    "id":"${vistaAccountInstance.id}",
                                                                    "vistaId":"${vistaAccountInstance.vistaId}",
                                                                    "division":"${vistaAccountInstance.division}",
                                                                    "name":"${vistaAccountInstance.name}",
                                                                    "host":"${vistaAccountInstance.host}",
                                                                    "port":"${vistaAccountInstance.port}",
                                                                    "production":"${vistaAccountInstance.production}"
                                                                },
                                                                </g:each>
                                                            ]
                                                        }),
                                                        valueField:'vistaId',
                                                        displayField:'name',
                                                        editable:false,
                                                        disableKeyFilter:true,
                                                        queryMode:'local',
                                                        allowBlank:false,
                                                        forceSelection:true,
                                                        listeners:{
                                                            select:function (comboBox, selection) {
                                                                var record = selection[0];
                                                                var formComponent = this.up('form');
                                                                formComponent.down('hiddenfield[name="vista.vistaId"]').setValue(record.get("vistaId"));
                                                                formComponent.down('textfield[name="vista.division"]').setValue(record.get("division"));
                                                                formComponent.down('textfield[name="vista.name"]').setValue(record.get("name"));
                                                                formComponent.down('textfield[name="vista.host"]').setValue(record.get("host"));
                                                                formComponent.down('numberfield[name="vista.port"]').setValue(record.get("port"));
                                                                formComponent.down('checkboxfield[name="vista.production"]').setValue(record.get("production"));
                                                            }
                                                        }
                                                    },
                                                    </g:if>
                                                    {
                                                        fieldLabel:'Access Code',
                                                        name:'access',
                                                        inputType:'password',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        fieldLabel:'Verify Code',
                                                        name:'verify',
                                                        inputType:'password',
                                                        allowBlank:false
                                                    },
                                                    {
                                                        xtype:'fieldcontainer',
                                                        fieldLabel:'',
                                                        hideEmptyLabel:false,
                                                        layout:'vbox',
                                                        items:[
                                                            {
                                                                xtype:'button',
                                                                text:'Test Connection',
                                                                flex:1,
                                                                handler:function () {
																	//op.skipErrors = true;
                                                                    // The getForm() method returns the Ext.form.Basic instance:
                                                                    var formComponent = this.up('form');
                                                                    var form = formComponent.getForm();
                                                                    if (form.isValid()) {
                                                                        formComponent.setLoading("Testing Connection...", true);
                                                                        // Submit the Ajax request and handle the response
                                                                        form.submit({
                                                                            url:'/setup/test',
																			skipErrors:'true',
                                                                            success:function (form, action) {
                                                                                formComponent.setLoading(false);

                                                                                var r = Ext.JSON.decode(action.response.responseText, true);

                                                                                var vistaIdCmp = formComponent.down('#vistaIdField');
                                                                                vistaIdCmp.setValue(r.data.vistaId);

                                                                                var statusCmp = formComponent.down('#status');
                                                                                statusCmp.removeAll();
                                                                                statusCmp.add({html:'<span class="success">Connection<br>Successful</span>'});

                                                                                var completeButton = formComponent.down('#setupComplete');
                                                                                completeButton.setDisabled(false);
																				//op.skipErrors = true;
                                                                            },
                                                                            failure:function (form, action) {
                                                                                formComponent.setLoading(false);

                                                                                var r = Ext.JSON.decode(action.response.responseText, true);
                                                                                var errorMsg = r.error.message;

                                                                                var statusCmp = formComponent.down('#status');
                                                                                statusCmp.removeAll();
                                                                                statusCmp.add({html:'<span class="failure">Connection<br>Failed:<br>' + errorMsg + '</span>'});
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            },
                                                            {
                                                                xtype:'panel',
                                                                itemId:'status',
                                                                flex:1
                                                            }
                                                        ]
                                                    },
												{
//
													xtype:'fieldcontainer',
													fieldLabel:'',
													hideEmptyLabel:false,
													layout:'vbox',
													margin:'40 0 0 0',
													items:[
													{	
													xtype:'button',
													text:'Finish Configuration',
													itemId:'setupComplete',
													disabled:true,
													flex:1,
													handler:function () {
														var formComponent = this.up('form');
														// The getForm() method returns the Ext.form.Basic instance:
														var form = formComponent.getForm();
														if (form.isValid()) {
															formComponent.setLoading("Finishing Configuration...", true);
															form.standardSubmit = true;
															form.submit({
																url:'/setup',
																params:{
																	done:true
																},
																%{--success: function(form, action) {--}%
																%{--formComponent.setLoading(false);--}%
					
																%{--window.location = "${createLink(uri:'/', absolute:true)}";--}%
																%{--},--}%
																%{--failure: function(form, action) {--}%
																%{--formComponent.setLoading(false);--}%
																%{--}--}%
															});
														}
													}
													}
												]
//													
												}
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            },
                        ]
                    }
                    ]
                }
            ]
        });
    });
</script>
</head>

<body>
<div id="center"></div>
%{--<div class="body">--}%
%{--Hello setup world--}%
%{--<g:if test="${flash.invalidToken}">--}%
%{--Don't click the button twice!--}%
%{--</g:if>--}%
%{--<g:form url="/setup" useToken="true">--}%

%{--<g:textField name="serverId" value="${setup.serverId}"/>--}%
%{--<g:textField name="url" value="${setup.url}"/>--}%

%{--<g:submitButton name="done" value="Complete Setup"/>--}%
%{--</g:form>--}%
%{--</div>--}%
</body>
</html>