<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:render template="/layouts/viewport"/>
    <script type="text/javascript">
    	Ext.require(['EXT.DOMAIN.cpe.designer.CPEDesigner', 'EXT.DOMAIN.cpe.designer.PanelEditor']);
		Ext.onReady(function() {

			/*
			// this is a temporary host page for my work on the new tab editor
			var win = Ext.create('Ext.window.Window', {
				xtype: 'window',
				title: 'Page Config Editor', height: 350, width: 500,
				layout: 'fit',
				items: [{xtype: 'patienttabedit'}]
			});
			win.show();
			*/

            var viewport = Ext.ComponentQuery.query('viewport')[0];
			viewport.setCenter({xtype: 'cpedesigner'})
		});
    </script>
</head>
<body>
<div id="center"/>
</body>
</html>
