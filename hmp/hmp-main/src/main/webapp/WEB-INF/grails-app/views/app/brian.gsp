<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="viewport"/>
    <script type="text/javascript">
    	Ext.require(['EXT.DOMAIN.cpe.viewdef.ViewDefGridPanel']);
		Ext.onReady(function() {

			var grid = Ext.create('EXT.DOMAIN.cpe.viewdef.ViewDefGridPanel', {
				viewAutoLoad: true, 'detail.emptyHTML': 'one string!',
				viewID: 'LabViewDef', viewParams: {'patient.id': 1}
			});

            var viewport = Ext.ComponentQuery.query('viewport')[0];
			viewport.setCenter({
				xtype: 'panel',
				title: 'title',
				layout: {
					type: 'vbox',
					align: 'stretch'
				},
				items: [{
					xtype: 'viewdefgridpanel',
					viewAutoLoad: true,
					detailType: 'window',
					detail: {
						minHeight: 300, minWidth: 400,
						emptyHTML: 'no record in the bottom'
					},
					viewID: 'MedsViewDef', viewParams: {'patient_id': 1}
				}, grid]
			});
		});
    </script>
</head>
<body>
</body>
</html>
