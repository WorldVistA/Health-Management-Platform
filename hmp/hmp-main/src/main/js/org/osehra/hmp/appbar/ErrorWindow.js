/*
 * The idea here is to have a diagnostics window that can take an exception and display
 * it with all sorts of other potentially relevant information.
 * 
 * TODO: Eventually it would be cool to be able to submit structured JSON somewhere.
 * TODO: Include: error type, userID, stack trace, session info/id, current pid, cookies?, roster id, (all of the cpe context stuff)
 * TODO: Maybe it could keep the last ajax request/response info? (probably should live in AppContext)
 * TODO: Button to reload the app in development mode (with ext-dev.js)
 * TODO: when the "Unexpected browser error" happens, clicking on the warning should display this window. 
 * TODO: how to make the properties grid read-only?
 * TODO: Vista connection info + status + version?
 * TODO: DB Connection URL/status?
 * TODO: Add tabs to look at server logs?
 * TODO: Something similar to RPC log? (last RPC + last AJAX)?
 * TODO: Session keys?
 * TODO: Cookies?
 * TODO: keep track of muliple exceptions? show them as a list?
 */
Ext.define('org.osehra.hmp.appbar.ErrorWindow', {
    extend: 'Ext.window.Window',
    requires: [
        'org.osehra.hmp.AppContext',
        'org.osehra.hmp.UserContext',
        'org.osehra.hmp.appbar.AppInfoPropertiesGrid'
    ],
    singleton: true,
    alias: 'widget.errorwin',
    layout: 'fit',
    title: 'Error/Diagnostics',
    height: 400,
    width: 800,
    closeAction: 'hide',
    initComponent: function() {
    	this.callParent();
		this.errorstore = this.down('gridpanel').getStore();
		this.usrprops = this.down('propertygrid[title="User"]');
		this.hmpprops = this.down('propertygrid[title="HMP Properties"]');
		this.sysprops = this.down('propertygrid[title="System Properties"]');
		this.envprops = this.down('propertygrid[title="Environment Variables"]');
    },
	listeners: {
		show: function() {
			this.errorstore.removeAll();
        	this.errorstore.add(org.osehra.hmp.appbar.ErrorManager.errors);
		},
		render: function() {
            var me = this;
            me.usrprops.setSource(org.osehra.hmp.UserContext.getUserInfo());
//            me.hmpprops.setSource(data.props);
//            me.sysprops.setSource(data.system);
//            me.envprops.setSource(data.env);
        }
	},
	addException: function(ex) {
		this.errorstore.add(ex);
	},
 	items: {
    	xtype: 'tabpanel',
    	defaults: {
    		listeners: {
    			beforeedit: function() {
    				return false;
    			}
    		}
    	},
    	items: [
	        {
	        	xtype: 'gridpanel',
	        	title: 'Errors',
	        	store: {
	        		fields: ['type','msg','loc','details'],
	        		data: []
	        	},
	        	features: [{
	        		ftype: 'rowbody', 
	        		rowBodyTrCls: 'x-grid-row-focused',
	        		rowBodyTdCls: 'x-grid-cell',
        			getAdditionalData: function(data, rowIndex, record, orig) {
        				var headerCt = this.view.headerCt, colspan = headerCt.getColumnCount();
        				return {
        					rowBody: data.details,
        					rowBodyCls: 'x-grid-rowbody',
        					rowBodyColspan: colspan
        				}
        			}
    			}],
	        	columns: [
                    {dataIndex: 'type', text: 'Type', width: 50},
                    {dataIndex: 'msg', text: 'Message', width: 175},
                    {dataIndex: 'loc', text: 'Location', flex: 1}
                ]
	        },
	        {
	        	xtype: 'propertygrid',
	        	title: 'Client Info',
                source: {

                },
	            listeners: {
                    'beforerender':function() {
                        this.refreshSource();
                    }
                },
                refreshSource:function() {
                    this.setSource({
                        "Current URL": location.href,
                        "Browser Agent": navigator.userAgent,
                        "HMP Version": org.osehra.hmp.AppContext.getVersion(),
                        "HMP App": org.osehra.hmp.AppContext.getAppInfo().app,
                        "HMP Build": '',
                        "User Name": org.osehra.hmp.UserContext.getUserInfo().displayName,
                        "User DUZ": org.osehra.hmp.UserContext.getUserInfo().duz,
                        "Screen Size": screen.width + "x" + screen.height,
                        "Patient Context": Ext.util.Cookies.get("CONTEXT")
                    });
                }
	    	},
	    	{
	    		xtype: 'propertygrid',
	    		title: 'User',
	    		source: {}
	    	},
            {
                xtype: 'appinfopropertygrid',
                title: 'HMP Properties',
                appInfo: 'props'
            },
            {
                xtype: 'appinfopropertygrid',
                title: 'System Properties',
                appInfo: 'system'
            },
            {
                xtype: 'appinfopropertygrid',
                title: 'Environment Variables',
                appInfo: 'env'
            }
        ]
    }
});
