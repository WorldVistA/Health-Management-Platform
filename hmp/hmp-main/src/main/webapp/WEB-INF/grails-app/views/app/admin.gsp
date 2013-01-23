<%@ page import="org.osehra.cpe.HmpProperties" %>
<html>
<head>
    <title>${message(code: 'platform.name')} &raquo; Admin</title>
    <g:render template="/layouts/extjs"/>
    <script type="text/javascript">
        Ext.require(['org.osehra.hmp.admin.AdminApp']);
        Ext.onReady(function () {
            Ext.create('org.osehra.hmp.admin.AdminApp');
        });
    </script>
</head>
<body>
<div id="center"/>
</body>
</html>
