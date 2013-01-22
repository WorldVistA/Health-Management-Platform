<%@ page import="EXT.DOMAIN.cpe.HmpProperties" %>
<html>
<head>
    <title>${message(code: 'platform.name')} &raquo; Admin</title>
    <g:render template="/layouts/extjs"/>
    <script type="text/javascript">
        Ext.require(['EXT.DOMAIN.hmp.admin.AdminApp']);
        Ext.onReady(function () {
            Ext.create('EXT.DOMAIN.hmp.admin.AdminApp');
        });
    </script>
</head>
<body>
<div id="center"/>
</body>
</html>
