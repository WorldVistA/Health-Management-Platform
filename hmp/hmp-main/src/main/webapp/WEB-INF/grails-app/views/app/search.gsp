<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:render template="/layouts/viewport"/>

    <script type="text/javascript">
        Ext.require([
            'EXT.DOMAIN.cpe.search.SearchPanel'
        ]);

        Ext.onReady(function() {
            var viewport = Ext.ComponentQuery.query('viewport')[0];
            viewport.setCenter({
                xtype: 'searchpanel',
                height: '100%',
                width: '100%'
            });
        });
    </script>
     <link rel="stylesheet" href="${resource(dir: 'css', file: 'cpe.css')}"/>
</head>

<body>
<div id="center"></div>
</body>
</html>
