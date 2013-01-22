<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Sync Errors for ${patient.familyName},${patient.givenNames}</title>
    <g:render template="/layouts/viewport"/>

    <g:render template="/sync/submenu"/>
    <script type="text/javascript">
    Ext.onReady(function() {
        var store = Ext.create('Ext.data.Store', {
            fields:['id', 'patient', 'dateCreated', 'item', 'message', 'stackTrace', 'xml'],
            pageSize: 20,
            proxy : {
                type: 'ajax',
                url : 'syncErrors',
                extraParams: {
                    pid: ${patient.pid},
                    format: 'json'
                },
                reader: {
                    type: 'json',
                    root: 'data.items',
                    totalProperty: 'data.totalItems'
                }
            },
            autoLoad: true
        });

        var viewport = Ext.ComponentQuery.query('viewport')[0];
        viewport.setCenter({
                xtype: 'container',
                items: [
                    {
                        xtype: 'component',
                        html: '<h1>Sync Errors for ${patient.familyName},${patient.givenNames}</h1><p>PIDs: ${patient.patientIds}</p><br />'
                    },
                    {
                        xtype: 'grid',
                        minHeight: 120,
                        loadMask: true,
                        sortableColumns: false,
                        store: store,
                        columns: [
                            {header: 'ID', dataIndex: 'id', width: 36},
                            {header: 'Date&nbsp;Created', dataIndex: 'dateCreated'},
                            {header: 'Item', dataIndex:'item', flex: 2},
                            {header: 'Message', dataIndex:'message',flex: 2},
                            {header: 'Details', xtype:'templatecolumn', tpl:'<a href="<g:createLink controller="sync" action="syncError" />/{id}">Detail</a>'}
                        ],
                        dockedItems: [
                            {
                                xtype: 'pagingtoolbar',
                                dock: 'bottom',
                                store: store,
                                displayInfo: true
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
</body>
</html>